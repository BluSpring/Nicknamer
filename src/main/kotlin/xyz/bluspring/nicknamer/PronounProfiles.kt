package xyz.bluspring.nicknamer

data class PronounProfiles(
    val profiles: MutableMap<String, MutableList<String>>,
    var currentProfile: String
)
