package dev.datlag.esports.prodigy.ui.browser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.cef.CefClient
import org.cef.browser.CefBrowser

@Composable
fun CefBrowserAwt(
    url: String,
    osr: Boolean = false,
    transparent: Boolean = false,
    modifier: Modifier = Modifier.fillMaxSize(),
    onClientAvailable: (suspend (CefClient) -> Unit)? = null,
    onBrowserAvailable: (suspend (CefBrowser) -> Unit)? = null,
    errorContent: @Composable (Throwable) -> Unit,
    initContent: @Composable (BrowserInitState) -> Unit,
) {
    val useOsr = rememberUpdatedState(osr)
    val useTransparent = rememberUpdatedState(transparent)

    val holder = rememberBrowserHolder(url, onClientAvailable, onBrowserAvailable) { client, url ->
        CefBrowserAwtWrapper(client, url, useOsr.value, useTransparent.value)
    }

    holder.wrapper?.let { CefBrowserAwt(it, Color.Transparent, modifier) }
        ?: holder.error?.let { errorContent(it) }
        ?: initContent(holder.initState)

    LaunchedEffect(Unit) {
        snapshotFlow { useTransparent.value }.updateWrapperOnChange(holder, this)
        snapshotFlow { useOsr.value }.updateWrapperOnChange(holder, this)
    }
}

@Composable
fun CefBrowserAwt(
    wrapper: CefBrowserAwtWrapper,
    background: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    SwingPanel(
        background = background,
        modifier = modifier,
        factory = { wrapper.browser.uiComponent },
    )
}

@Composable
private fun <W : CefBrowserWrapper> rememberBrowserHolder(
    url: String,
    onClientAvailable: (suspend (CefClient) -> Unit)?,
    onBrowserAvailable: (suspend (CefBrowser) -> Unit)?,
    onCreateWrapper: (CefClient, String) -> W
): CefClientHolder<W> {
    val targetUrl = rememberUpdatedState(url)
    val holder = remember { CefClientHolder(targetUrl, onCreateWrapper) }
    val browserCallback by rememberUpdatedState(onBrowserAvailable)
    val clientCallback by rememberUpdatedState(onClientAvailable)

    holder.client?.let { instance ->
        DisposableEffect(Unit) {
            holder.wrapper = onCreateWrapper(instance, targetUrl.value)
            onDispose { instance.dispose() }
        }

        LaunchedEffect(clientCallback) {
            clientCallback?.invoke(instance)
        }
    }

    holder.wrapper?.let { instance ->
        LaunchedEffect(browserCallback) {
            browserCallback?.invoke(instance.browser)
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { targetUrl.value }.drop(1).onEach { url ->
            holder.wrapper?.browser?.loadURL(url)
        }.launchIn(this)

        try {
            holder.client = Cef.newClient(holder.initState)
        } catch (throwable: Throwable) {
            holder.error = throwable
        }
    }

    return holder
}

private fun <W : CefBrowserWrapper, T> Flow<T>.updateWrapperOnChange(
    holder: CefClientHolder<W>,
    scope: CoroutineScope,
    drop: Int = 1
) {
    val flow = if (drop > 0) this.drop(drop) else this

    flow.onEach {
        holder.client?.let { client ->
            holder.wrapper = holder.onCreateWrapper(client, holder.url.value)
        }
    }.launchIn(scope)
}

@Stable
private class CefClientHolder<W>(
    val url: State<String>,
    val onCreateWrapper: (CefClient, String) -> W
) {
    val initState = BrowserInitStateImpl()
    var client: CefClient? by mutableStateOf(null)
    var error: Throwable? by mutableStateOf(null)
    var wrapper: W? by mutableStateOf(null)
}