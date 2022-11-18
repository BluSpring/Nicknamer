package xyz.bluspring.nicknamer

import com.google.gson.JsonParser
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.util.ULocale
import com.mojang.authlib.GameProfile
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.Texts
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.bluspring.nicknamer.config.ConfigManager
import xyz.bluspring.nicknamer.config.NameFormat
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.config.pronouns.PronounManager
import java.io.File
import java.net.URL
import java.util.UUID

class Nicknamer : ModInitializer {
    override fun onInitialize() {
        if (!configDir.exists())
            File(configDir, "nicknamer").mkdirs()

        ConfigManager.load()
        NicknameManager.load()
        PronounManager.load()
    }

    companion object {
        private val playerCache = mutableMapOf<String, UUID>()
        val logger: Logger = LoggerFactory.getLogger("Nicknamer")
        val configDir = File(FabricLoader.getInstance().configDir.toFile(), "nicknamer")

        fun formatStringAsText(format: String, replacements: Map<String, Text>): Text {
            val newText = mutableListOf<Text>()

            var formedName = ""
            var isInFormatting = false
            format.forEach {
                if (it == '%') {
                    if (isInFormatting) {
                        formedName += it
                        newText.add(replacements[formedName] ?: LiteralText(formedName))
                        formedName = ""
                    } else {
                        newText.add(LiteralText(formedName))
                        formedName = "$it"
                    }

                    isInFormatting = !isInFormatting
                } else
                    formedName += it
            }

            // Add a missing bit that gets missed
            newText.add(LiteralText(formedName))

            return Texts.join(newText, LiteralText(""))
        }

        fun setText(profile: GameProfile, config: Map<NameFormat, String>, displayName: Text): Text {
            val hasPronouns = PronounManager.pronouns.contains(profile.id)
            val hasNickname = !NicknameManager.isDisabled(profile.id)

            val nameFormat = NameFormat.getNameFormat(hasNickname, hasPronouns)

            return formatStringAsText(
                config[nameFormat] ?: "%username%",
                mutableMapOf(
                    "%username%" to displayName
                ).apply {
                    if (hasNickname)
                        this["%nickname%"] = NicknameManager.nicknames[profile.id]!!

                    if (hasPronouns)
                        this["%pronouns%"] = PronounManager.getPronounsText(profile.id)
                }
            )
        }

        fun toTitleCase(str: String): String {
            return UCharacter.toTitleCase(ULocale.getDefault(), str, null, 0)
        }

        fun getPlayerUUID(name: String): UUID? {
            if (playerCache.contains(name.lowercase()))
                return playerCache[name.lowercase()]

            val client = MinecraftClient.getInstance()
            val uuid = client.networkHandler?.getPlayerListEntry(name)?.profile?.id
                ?: try {
                    val response = URL("https://api.mojang.com/users/profiles/minecraft/$name").readText()
                    val json = JsonParser.parseString(response).asJsonObject

                    val uuid = UUID.fromString(
                        json.get("id").asString.replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                            "$1-$2-$3-$4-$5"
                        )
                    )

                    uuid
                } catch (_: Exception) {
                    null
                }

            if (!playerCache.contains(name.lowercase()) && uuid != null)
                playerCache[name.lowercase()] = uuid

            return uuid
        }
    }
}