package dev.datlag.esports.prodigy.ui.browser

import dev.datlag.esports.prodigy.common.launchDefault
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.model.common.suspendCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.EnumProgress
import me.friwi.jcefmaven.IProgressHandler
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler
import me.friwi.jcefmaven.impl.step.check.CefInstallationChecker
import org.cef.CefApp
import org.cef.CefClient
import org.cef.callback.CefSchemeHandlerFactory
import org.cef.callback.CefSchemeRegistrar
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

class ApplicationRestartRequiredException(message: String) : Exception(message)

fun interface CefAppBuilderConfigurator {
    fun CefAppBuilder.configure()
}

private class SchemeHandler(
    val schemeName: String,
    val domainName: String?,
    val factory: () -> CefSchemeHandlerFactory
)

private class CustomScheme(
    val schemeName: String,
    val isStandard: Boolean,
    val isLocal: Boolean,
    val isDisplayIsolated: Boolean,
    val isSecure: Boolean,
    val isCorsEnabled: Boolean,
    val isCspBypassing: Boolean,
    val isFetchEnabled: Boolean
)

private sealed interface State {
    data object New : State
    data object Initializing : State
    data object Initialized : State
    data object Error : State
    data object Disposed : State
}

data object Cef {

    private val state: MutableStateFlow<State> = MutableStateFlow(State.New)
    private var cefInitError: Throwable? = null
    private var cefAppInstance: CefApp? = null

    private val schemeHandlers = mutableSetOf<SchemeHandler>()
    private val customSchemes = mutableSetOf<CustomScheme>()

    private val progressHandlers = mutableSetOf<IProgressHandler>(ConsoleProgressHandler())
    private var progressState = EnumProgress.LOCATING
    private var progressValue = EnumProgress.NO_ESTIMATION
    private val progressLock = Any()

    var appHandlerAdapter: MavenCefAppHandlerAdapter? = null
    var builderConfigurator: CefAppBuilderConfigurator? = null
    var progressHandler: IProgressHandler? = null
    var installDir: File = File("jcef-bundle")

    private val cefApp: CefApp
        get() = checkNotNull(cefAppInstance) { "CefApp must not be null." }

    fun dispose() {
        when (state.value) {
            State.New, State.Disposed, State.Error -> return
            State.Initializing -> {
                runBlocking {
                    state.first { it != State.Initializing }
                }

                return dispose()
            }
            State.Initialized -> {
                state.value = State.Disposed
                cefApp.dispose()
                cefAppInstance = null
            }
        }
    }

    fun initAsync(
        scope: CoroutineScope,
        onError: ((Throwable) -> Unit)? = null,
        onRestartRequired: (() -> Unit)? = null
    ) {
        val installDir = this.installDir
        val builder = getInitBuilder(installDir) ?: return
        val isInstallOk = CefInstallationChecker.checkInstallation(installDir)

        if (isInstallOk) {
            scope.launchIO {
                val result = suspendCatching { builder.build() }
                setInitResult(result)

                result.exceptionOrNull()?.let { error ->
                    onError?.invoke(error) ?: throw error
                }
            }
        } else {
            scope.launchIO {
                try {
                    builder.install()
                } catch (error: Throwable) {
                    setInitResult(Result.failure(error))
                    onError?.invoke(error) ?: throw error
                }

                val exception = ApplicationRestartRequiredException("Application needs to restart.")

                setInitResult(Result.failure(exception))
                onRestartRequired?.invoke()
            }
        }
    }

    private fun getInitBuilder(installDir: File): CefAppBuilder? {
        val currentState = state.value

        when (currentState) {
            State.Disposed -> throw IllegalStateException("Cef is disposed.")
            State.Initializing, State.Initialized -> return null
            State.New, State.Error -> state.value = State.Initializing
        }

        if (currentState == State.Error) {
            cefInitError = null
        }

        val builder = CefAppBuilder().apply {
            cefSettings.windowless_rendering_enabled = false
        }

        builderConfigurator?.run {
            builder.configure()
        }

        return builder.apply {
            setProgressHandler(::dispatchProgress)
            setAppHandler(AppHandler)
            setInstallDir(installDir)
        }
    }

    private fun setInitResult(result: Result<CefApp>) {
        val nextState = if (result.isSuccess) {
            cefAppInstance = result.getOrThrow()
            builderConfigurator = null
            progressHandler = null
            State.Initialized
        } else {
            cefInitError = result.exceptionOrNull()
            State.Error
        }

        check(state.compareAndSet(State.Initializing, nextState)) {
            "State.Initializing was expected."
        }
    }

    suspend fun newClient(onProgress: IProgressHandler? = null): CefClient {
        return when (state.value) {
            State.New -> throw IllegalStateException("Cef was not initialized.")
            State.Disposed -> throw IllegalStateException("Could not create client after dispose() was called")
            State.Error -> throw checkNotNull(cefInitError) { "Error must not be null" }
            State.Initialized -> cefApp.createClient()

            State.Initializing -> {
                val added = onProgress?.let { handler ->
                    synchronized(progressLock) {
                        handler.handleProgress(progressState, progressValue)
                        progressHandlers.add(handler)
                    }
                }

                state.first { it != State.Initializing }

                if (added == true) {
                    synchronized(progressLock) {
                        progressHandlers.remove(onProgress)
                    }
                }

                return newClient(onProgress)
            }
        }
    }

    private fun dispatchProgress(state: EnumProgress, value: Float) = synchronized(progressLock) {
        progressState = state
        progressValue = value

        progressHandler?.handleProgress(state, value)

        progressHandlers.forEach { handler ->
            handler.handleProgress(state, value)
        }
    }

    fun registerCustomScheme(
        schemeName: String,
        isStandard: Boolean = false,
        isLocal: Boolean = false,
        isDisplayIsolated: Boolean = false,
        isSecure: Boolean = false,
        isCorsEnabled: Boolean = false,
        isCspBypassing: Boolean = false,
        isFetchEnabled: Boolean = false
    ) {
        ensureIsNew { "register custom scheme" }

        require(customSchemes.none { it.schemeName == schemeName }) {
            "A scheme is already registered with the name `$schemeName`."
        }

        val customScheme = CustomScheme(
            schemeName = schemeName,
            isStandard = isStandard,
            isLocal = isLocal,
            isDisplayIsolated = isDisplayIsolated,
            isSecure = isSecure,
            isCorsEnabled = isCorsEnabled,
            isCspBypassing = isCspBypassing,
            isFetchEnabled = isFetchEnabled
        )

        customSchemes.add(customScheme)
    }

    fun registerSchemeHandlerFactory(
        schemeName: String,
        domainName: String? = null,
        factory: () -> CefSchemeHandlerFactory,
    ) {
        ensureIsNew { "register scheme handler" }
        schemeHandlers.add(SchemeHandler(schemeName, domainName, factory))
    }

    private inline fun ensureIsNew(lazyAction: () -> String) {
        when (state.value) {
            State.New, State.Error -> return

            State.Initializing, State.Initialized ->
                throw IllegalStateException("Could not ${lazyAction()} after CefApp started initializing.")

            State.Disposed ->
                throw IllegalStateException("Could not ${lazyAction()} after CefApp is disposed.")
        }
    }

    private data object AppHandler : MavenCefAppHandlerAdapter() {

        override fun onRegisterCustomSchemes(registrar: CefSchemeRegistrar) {
            super.onRegisterCustomSchemes(registrar)
            appHandlerAdapter?.onRegisterCustomSchemes(registrar)

            customSchemes.onEach { customScheme ->
                with(customScheme) {
                    registrar.addCustomScheme(
                        /* schemeName = */ schemeName,
                        /* isStandard = */ isStandard,
                        /* isLocal = */ isLocal,
                        /* isDisplayIsolated = */ isDisplayIsolated,
                        /* isSecure = */ isSecure,
                        /* isCorsEnabled = */ isCorsEnabled,
                        /* isCspBypassing = */ isCspBypassing,
                        /* isFetchEnabled = */ isFetchEnabled
                    )
                }
            }.clear()
        }

        override fun onContextInitialized() {
            super.onContextInitialized()
            appHandlerAdapter?.onContextInitialized()

            schemeHandlers.onEach { handler ->
                cefApp.registerSchemeHandlerFactory(
                    /* schemeName = */ handler.schemeName,
                    /* domainName = */ handler.domainName,
                    /* factory = */ handler.factory()
                )
            }.clear()
        }

        override fun onBeforeTerminate(): Boolean {
            return appHandlerAdapter?.onBeforeTerminate()
                ?: super.onBeforeTerminate()
        }

        override fun onScheduleMessagePumpWork(delayMs: Long) {
            appHandlerAdapter?.onScheduleMessagePumpWork(delayMs)
                ?: super.onScheduleMessagePumpWork(delayMs)
        }

        override fun stateHasChanged(state: CefApp.CefAppState) {
            super.stateHasChanged(state)
            appHandlerAdapter?.stateHasChanged(state)
        }
    }
}