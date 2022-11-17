package xyz.bluspring.nicknamer.config

data class NicknamerConfig(
    var enabled: Boolean = true,
    var playerListFormat: String = "%username% (%nickname%) - [%pronouns%]",
    var inGameFormat: String = "%username% (%nickname%)",
    var displayPronounsBelowUsername: Boolean = true
)
