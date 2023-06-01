package dev.datlag.esports.prodigy

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.useResource
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.withMainContext
import dev.datlag.esports.prodigy.model.common.*
import dev.icerock.moko.resources.AssetResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.awt.Image
import java.awt.Toolkit
import java.io.InputStream
import javax.swing.ImageIcon

object AppIO {

    fun applyTitle(title: String) {
        try {
            val toolkit = Toolkit.getDefaultToolkit()
            val awtAppClassNameField = toolkit.javaClass.getDeclaredField("awtAppClassName")
            awtAppClassNameField.trySetAccessible()
            awtAppClassNameField.set(toolkit, title)
        } catch (ignored: Throwable) { }
    }

    fun loadAppIcon(
        window: ComposeWindow,
        scope: CoroutineScope,
        vararg assets: AssetResource
    ) = scope.launchIO {
        val appIcons = assets.map { async {
            getAppImage(it)
        } }.awaitAll().filterNotNull()

        withMainContext {
            window.iconImages = appIcons
        }
    }

    private suspend fun getAppImage(asset: AssetResource): Image? = suspendCatching {
        (getResourcesAsInputStream(asset.filePath)
            ?: getResourcesAsInputStream(asset.originalPath)
            ?: asset.resourcesClassLoader.getResourceAsStream(asset.filePath))?.use {
                ImageIcon(it.readBytes()).image
        }
    }.getOrNull()

    private fun getResourcesAsInputStream(location: String): InputStream? {
        val classLoader = AppIO::class.java.classLoader ?: this::class.java.classLoader
        return scopeCatching {
            classLoader?.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            AppIO::class.java.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            this::class.java.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            useResource(location) { it }
        }.getOrNull()
    }
}