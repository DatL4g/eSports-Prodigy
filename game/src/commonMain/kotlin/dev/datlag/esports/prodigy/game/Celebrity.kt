package dev.datlag.esports.prodigy.game

sealed class Celebrity(
    val steamID: String,
    val nicknames: List<String>
) {

    object OHNEPIXEL : Celebrity(
        steamID = "76561198045277210",
        nicknames = listOf(
            "Ohne",
            "Mark",
        )
    )
    object PAPAPLATTE : Celebrity(
        steamID = "76561198013760707",
        nicknames = listOf(
            "Kevin",
            "Quentin",
            "Papakacke",
            "Lattensep",
            "Lattendaddy",
        )
    )
    object TRILLUXE : Celebrity(
        steamID = "76561198021323440",
        nicknames = listOf(
            "Trill",
            "Lennart",
        )
    )

    companion object {
        fun valueOf(steamID: String): Celebrity? = when (steamID) {
            OHNEPIXEL.steamID -> OHNEPIXEL
            PAPAPLATTE.steamID -> PAPAPLATTE
            TRILLUXE.steamID -> TRILLUXE
            else -> null
        }
    }
}