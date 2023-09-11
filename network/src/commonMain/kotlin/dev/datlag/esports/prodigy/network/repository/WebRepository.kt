package dev.datlag.esports.prodigy.network.repository

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.SubscriptExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html.HtmlRenderer.HtmlRendererExtension
import com.vladsch.flexmark.html2md.converter.ExtensionConversion
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.DataSet
import com.vladsch.flexmark.util.data.MutableDataSet
import dev.datlag.esports.prodigy.network.common.findFirstOrNull
import io.ktor.client.*
import it.skrape.core.document
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WebRepository() {

    private val options = MutableDataSet().apply {
        set(Parser.EXTENSIONS, listOf(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            SubscriptExtension.create()
        ))
        set(Parser.HTML_BLOCK_DEEP_PARSER, true)
        set(Parser.HTML_BLOCK_DEEP_PARSE_NON_BLOCK, true)
        set(Parser.HTML_BLOCK_START_ONLY_ON_BLOCK_TAGS, false)
        set(Parser.HTML_BLOCK_DEEP_PARSE_MARKDOWN_INTERRUPTS_CLOSED, true)
    }
    private val htmlToMarkdownConverter = FlexmarkHtmlConverter.builder(options).build()
    private val parser = Parser.builder(options).build()

    fun skrapeAsMarkdown(url: String): Flow<String?> = flow {
        emit(skrape(BrowserFetcher) {
            request {
                this.url = url
            }
            response {
                if (this.responseStatus.code !in 200..204) {
                    null
                } else {
                    val contentText = document.findFirstOrNull(".newsitem")?.text ?: document.wholeText
                    htmlToMarkdownConverter.convert(contentText)
                }
            }
        })
    }
}