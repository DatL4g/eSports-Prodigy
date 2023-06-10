package dev.datlag.esports.prodigy.module

import dev.datlag.esports.prodigy.AppIO
import dev.datlag.esports.prodigy.datastore.CryptoManager
import dev.datlag.esports.prodigy.model.UUID
import dev.datlag.esports.prodigy.model.common.deleteSafely
import dev.datlag.esports.prodigy.model.common.scopeCatching
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import org.kodein.di.*
import java.io.File
import kotlin.io.encoding.Base64

actual object PlatformModule {

    private const val NAME = "PlatformModuleDesktop"

    actual val di = DI.Module(NAME) {
        bindSingleton("UserEncryptionFile") {
            AppIO.getFileInUserDataDir("UserSettings.enc")
        }
        bindSingleton("UserSettingsFile") {
            AppIO.getFileInUserDataDir("UserSettings.pb")
        }

        bindSingleton {
            val uuidFile = AppIO.getFileInUserDataDir(".uuid")
            val json = Json {
                ignoreUnknownKeys = true
            }

            scopeCatching {
                uuidFile.inputStream().use {
                    json.decodeFromStream<UUID>(it)
                }
            }.getOrNull() ?: UUID.generate().also { id ->
                instance<File>("UserEncryptionFile").deleteSafely()
                instance<File>("UserSettingsFile").deleteSafely()

                uuidFile.outputStream().use { stream ->
                    json.encodeToStream(id, stream)
                }
            }
        }

        bindSingleton("UserSettingsCrypto") {
            val uuid: UUID = instance()

            CryptoManager(
                "user",
                instance("UserEncryptionFile"),
                uuid.commonDecoded,
                uuid.userDecoded
            )
        }
    }
}