package dev.datlag.esports.prodigy.model.hltv

data class Team(
    val id: Number,
    val name: String,
    val logo: String?,
    val socials: Socials,
    val country: Country,
    val rank: Number?,
    val players: List<Player>,
    val rankingDevelopment: List<Number>,
    val news: List<Article>
) {

    data class Socials(
        val facebook: String?,
        val twitter: String?,
        val instagram: String?
    )

    data class Player(
        val type: Type,
        val id: Number,
        val name: String,
        val timeOnTeam: String,
        val mapsPlayed: Number
    ) {

        sealed class Type(val label: String) {
            object COACH : Type("Coach")
            object STARTER : Type("Starter")
            object SUBSTITUTE : Type("Substitute")
            object BENCHED : Type("Benched")

            companion object {
                fun byLabel(value: String): Type {
                    return when {
                        value.equals(COACH.label, true) -> COACH
                        value.equals(STARTER.label, true) -> STARTER
                        value.equals(SUBSTITUTE.label, true) -> SUBSTITUTE
                        else -> BENCHED
                    }
                }
            }
        }
    }
}
