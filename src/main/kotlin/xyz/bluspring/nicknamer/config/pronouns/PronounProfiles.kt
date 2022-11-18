package xyz.bluspring.nicknamer.config.pronouns

data class PronounProfiles(
    val profiles: MutableMap<String, MutableList<String>>,
    var currentProfile: String
)
