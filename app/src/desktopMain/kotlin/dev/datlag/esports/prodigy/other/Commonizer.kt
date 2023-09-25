package dev.datlag.esports.prodigy.other

import dev.datlag.esports.prodigy.common.openInBrowser
import dev.datlag.esports.prodigy.ui.browser.ApplicationDisposer

actual class Commonizer(private val disposer: ApplicationDisposer) {
    actual fun openInBrowser(url: String, error: String): Result<Any> {
        return url.openInBrowser(error)
    }

    actual fun restartApp() {
        disposer.restart()
    }
}