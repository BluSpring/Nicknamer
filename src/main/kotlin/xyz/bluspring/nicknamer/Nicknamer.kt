package xyz.bluspring.nicknamer

import com.google.gson.JsonParser
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.util.ULocale
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.bluspring.nicknamer.config.ConfigManager
import java.net.URL
import java.util.UUID

class Nicknamer : ModInitializer {
    override fun onInitialize() {
        ConfigManager.load()

        NicknameManager.load()
        PronounManager.load()
    }

    companion object {
        private val playerCache = mutableMapOf<String, UUID>()
        val logger: Logger = LoggerFactory.getLogger("Nicknamer")

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