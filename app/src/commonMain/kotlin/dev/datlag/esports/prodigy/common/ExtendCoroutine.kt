package dev.datlag.esports.prodigy.common

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.datlag.esports.prodigy.model.common.scopeCatching
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
fun LifecycleOwner.mainScope() = CoroutineScope(mainDispatcher() + SupervisorJob(), lifecycle)

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

fun LifecycleOwner.launchIO(block: suspend  CoroutineScope.() -> Unit): Job {
    return ioScope().launchIO(block)
}

fun LifecycleOwner.launchMain(block: suspend  CoroutineScope.() -> Unit): Job {
    return mainScope().launchMain(block)
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

fun <T> runBlockingIO(block: suspend CoroutineScope.() -> T): T {
    return runBlocking(ioDispatcher()) {
        block()
    }
}

fun <T> runBlockingMain(block: suspend CoroutineScope.() -> T): T {
    return runBlocking(mainDispatcher()) {
        block()
    }
}

fun <T> Flow<T>.getValueBlocking(fallback: T): T {
    return scopeCatching {
        runBlockingIO {
            this@getValueBlocking.first()
        }
    }.getOrNull() ?: scopeCatching {
        runBlocking {
            this@getValueBlocking.first()
        }
    }.getOrNull() ?: fallback
}
