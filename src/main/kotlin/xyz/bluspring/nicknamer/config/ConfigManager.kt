package xyz.bluspring.nicknamer.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import xyz.bluspring.nicknamer.Nicknamer
import java.io.File

object ConfigManager {
    val config = NicknamerConfig()
    private val file = File(Nicknamer.configDir, "config.json")

    fun load() {
        if (!file.exists())
            return

        try {
            val json = JsonParser.parseString(file.readText()).asJsonObject

            config.enabled = json.get("enabled").asBoolean
            config.inGameFormat = mutableMapOf<NameFormat, String>().apply {
                val inGameFormat = json.getAsJsonObject("inGameFormat")

                inGameFormat.entrySet().forEach { (key, element) ->
                    this[NameFormat.valueOf(key)] = element.asString
                }
            }
            config.playerListFormat = mutableMapOf<NameFormat, String>().apply {
                val playerListFormat = json.getAsJsonObject("playerListFormat")

                playerListFormat.entrySet().forEach { (key, element) ->
                    this[NameFormat.valueOf(key)] = element.asString
                }
            }
            config.displayPronounsBelowUsername = json.get("displayPronounsBelowUsername").asBoolean
        } catch (e: Exception) {
            Nicknamer.logger.error("Failed to load config!")
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            if (!file.exists()) {
                if (!Nicknamer.configDir.exists())
                    Nicknamer.configDir.mkdirs()
                file.createNewFile()
            }

            file.writeText(
                JsonObject().apply {
                    addProperty("enabled", config.enabled)

                    val inGameFormat = JsonObject()
                    config.inGameFormat.forEach { (formatType, format) ->
                        inGameFormat.addProperty(formatType.name, format)
                    }
                    add("inGameFormat", inGameFormat)

                    val playerListFormat = JsonObject()
                    config.playerListFormat.forEach { (formatType, format) ->
                        playerListFormat.addProperty(formatType.name, format)
                    }
                    add("playerListFormat", playerListFormat)

                    addProperty("displayPronounsBelowUsername", config.displayPronounsBelowUsername)
                }.toString()
            )
        } catch (e: Exception) {
            Nicknamer.logger.error("Failed to save config!")
            e.printStackTrace()
        }
    }
}