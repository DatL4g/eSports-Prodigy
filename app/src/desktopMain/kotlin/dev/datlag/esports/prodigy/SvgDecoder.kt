package dev.datlag.esports.prodigy

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.res.loadSvgPainter
import io.kamel.core.config.ResourceConfig
import io.kamel.core.decoder.Decoder
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import org.apache.batik.transcoder.Transcoder
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.reflect.KClass

object SvgDecoder : Decoder<Painter> {

    override val outputKClass: KClass<Painter>
        get() = Painter::class

    override suspend fun decode(channel: ByteReadChannel, resourceConfig: ResourceConfig): Painter {
        val t: Transcoder = PNGTranscoder()
        val input = TranscoderInput(channel.toInputStream())
        val outputStream = ByteArrayOutputStream()
        outputStream.use {
            val output = TranscoderOutput(it)

            t.transcode(input, output)
        }

        val painter = ImageIO.read(outputStream.toByteArray().inputStream()).toPainter()
        return if (painter.intrinsicSize.isEmpty()) {
            loadSvgPainter(
                channel.toInputStream(),
                resourceConfig.density
            )
        } else {
            painter
        }
    }
}