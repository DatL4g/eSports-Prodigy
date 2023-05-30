package dev.datlag.esports.prodigy.common

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.CoroutineContext

fun <T> MutableStateFlow<T>.safeEmit(value: T, scope: CoroutineScope) {
    if (!this.tryEmit(value)) {
        scope.launch(Dispatchers.IO) {
            this@safeEmit.emit(value)
        }
    }
}

fun CoroutineScope(context: CoroutineContext, lifecycle: Lifecycle): CoroutineScope {
    val scope = CoroutineScope(context)
    lifecycle.doOnDestroy(scope::cancel)
    return scope
}

fun LifecycleOwner.coroutineScope(context: CoroutineContext): CoroutineScope = CoroutineScope(context, lifecycle)

fun LifecycleOwner.ioScope() = CoroutineScope(ioDispatcher() + SupervisorJob(), lifecycle)
fun LifecycleOwner.mainScope() = CoroutineScope(ioDispatcher() + SupervisorJob(), lifecycle)

fun mainDispatcher(): MainCoroutineDispatcher = Dispatchers.DeviceMain
fun ioDispatcher(): CoroutineDispatcher = Dispatchers.DeviceIO

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return this.launch(ioDispatcher()) {
        block()
    }
}

fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
    return this.launch(mainDispatcher()) {
        block()
    }
}

suspend fun <T> withIOContext(
    block: suspend CoroutineScope.() -> T
): T {
    return withContext(ioDispatcher()) {
        block()
    }
}

suspend fun <T> withMainContext(
    block: suspend CoroutineScope.() -> T
): T {
    return withContext(mainDispatcher()) {
        block()
    }
}