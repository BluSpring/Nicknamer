package xyz.bluspring.nicknamer.config

data class NicknamerConfig(
    var enabled: Boolean = true,

    var playerListFormat: MutableMap<NameFormat, String> = mutableMapOf(
        NameFormat.ALL to "%username% (%nickname%) - [%pronouns%]",
        NameFormat.NO_PRONOUNS to "%username% (%nickname%)",
        NameFormat.NO_NICKNAME to "%username% - [%pronouns%]",
        NameFormat.NONE to "%username%"
    ),

    var inGameFormat: MutableMap<NameFormat, String> = mutableMapOf(
        NameFormat.ALL to "%username% (%nickname%)",
        NameFormat.NO_PRONOUNS to "%username% (%nickname%)",
        NameFormat.NO_NICKNAME to "%username%",
        NameFormat.NONE to "%username%",
    ),

    var chatFormat: MutableMap<NameFormat, String> = mutableMapOf(
        NameFormat.ALL to "%nickname% [%pronouns%]",
        NameFormat.NO_PRONOUNS to "%nickname%",
        NameFormat.NO_NICKNAME to "%username% [%pronouns%]",
        NameFormat.NONE to "%username%",
    ),

    var displayPronounsBelowUsername: Boolean = true
)
