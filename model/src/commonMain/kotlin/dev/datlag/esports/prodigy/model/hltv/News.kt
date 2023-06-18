package dev.datlag.esports.prodigy.model.hltv

data class News(
    val link: String,
    val title: String,
    val date: String,
    val country: Country
) {

    data class Country(
        val name: String,
        val code: String
    )
}
