package xyz.bluspring.nicknamer.config

enum class NameFormat(val nickname: Boolean, val pronouns: Boolean, val displayName: String) {
    ALL(true, true, "Pronouns & Nicknames Visible"),
    NO_PRONOUNS(true, false, "Nicknames Visible"),
    NO_NICKNAME(false, true, "Pronouns Visible"),
    NONE(false, false, "None Visible");

    companion object {
        fun getNameFormat(nickname: Boolean, pronouns: Boolean): NameFormat {
            return NameFormat.values().first { it.nickname == nickname && it.pronouns == pronouns }
        }
    }
}