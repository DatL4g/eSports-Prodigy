package dev.datlag.esports.prodigy.other

import dev.datlag.esports.prodigy.common.openInBrowser

actual class Commonizer {
    actual fun openInBrowser(url: String, error: String): Result<Any> {
        return url.openInBrowser(error)
    }

}