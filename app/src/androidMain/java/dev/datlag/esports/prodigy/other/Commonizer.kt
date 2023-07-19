package dev.datlag.esports.prodigy.other

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import dev.datlag.esports.prodigy.model.common.scopeCatching

actual class Commonizer(private val context: Context) {
    actual fun openInBrowser(url: String, error: String): Result<Any> {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        val result = scopeCatching {
            ContextCompat.startActivity(context, browserIntent, null)
        }

        if (result.isSuccess) {
            return result
        }

        val newIntent = browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return scopeCatching {
            ContextCompat.startActivity(context, newIntent, null)
        }
    }

}