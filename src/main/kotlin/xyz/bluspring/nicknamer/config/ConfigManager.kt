package xyz.bluspring.nicknamer.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import xyz.bluspring.nicknamer.Nicknamer
import java.io.File

object ConfigManager {
    val config = NicknamerConfig()
    private val file = File(FabricLoader.getInstance().configDir.toFile(), "nicknamer_config.json")

    fun load() {
        if (!file.exists())
            return

        try {
            val json = JsonParser.parseString(file.readText()).asJsonObject

            config.enabled = json.get("enabled").asBoolean
            config.inGameFormat = json.get("inGameFormat").asString
            config.playerListFormat = json.get("playerListFormat").asString
            config.displayPronounsBelowUsername = json.get("displayPronounsBelowUsername").asBoolean
        } catch (e: Exception) {
            Nicknamer.logger.error("Failed to load config!")
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            if (!file.exists())
                file.createNewFile()

            file.writeText(
                JsonObject().apply {
                    addProperty("enabled", config.enabled)
                    addProperty("inGameFormat", config.inGameFormat)
                    addProperty("playerListFormat", config.playerListFormat)
                    addProperty("displayPronounsBelowUsername", config.displayPronounsBelowUsername)
                }.toString()
            )
        } catch (e: Exception) {
            Nicknamer.logger.error("Failed to save config!")
            e.printStackTrace()
        }
    }
}