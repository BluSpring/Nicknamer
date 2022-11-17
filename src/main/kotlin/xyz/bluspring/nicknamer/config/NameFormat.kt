package xyz.bluspring.nicknamer.config

enum class NameFormat(val nickname: Boolean, val pronouns: Boolean) {
    ALL(true, true),
    NO_PRONOUNS(true, false),
    NO_NICKNAME(false, true),
    NONE(false, false);

    companion object {
        fun getNameFormat(nickname: Boolean, pronouns: Boolean): NameFormat {
            return NameFormat.values().first { it.nickname == nickname && it.pronouns == pronouns }
        }
    }
}