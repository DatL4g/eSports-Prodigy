package dev.datlag.esports.prodigy.network.common

import dev.datlag.esports.prodigy.model.common.scopeCatching
import it.skrape.selects.Doc
import it.skrape.selects.DocElement

fun DocElement.findFirstOrNull(selector: String) = scopeCatching {
    this.findFirst(selector)
}.getOrNull()

fun Doc.findFirstOrNull(selector: String) = scopeCatching {
    this.findFirst(selector)
}.getOrNull()