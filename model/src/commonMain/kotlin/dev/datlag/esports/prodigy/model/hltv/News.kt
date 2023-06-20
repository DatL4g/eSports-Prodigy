package dev.datlag.esports.prodigy.model.hltv

data class News(
    val link: String,
    val title: String,
    val date: Long,
    val country: Country
)
