package xyz.bluspring.nicknamer

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.config.ConfigManager
import java.io.File
import java.util.*

object NicknameManager {
    private val file = File(FabricLoader.getInstance().configDir.toFile(), "nicknamer.json")
    val nicknames = mutableMapOf<UUID, Text>()
    val disabled = mutableSetOf<UUID>()

    fun save() {
        if (!file.exists()) {
            if (!FabricLoader.getInstance().configDir.toFile().exists())
                FabricLoader.getInstance().configDir.toFile().mkdirs()

            file.createNewFile()
        }

        val json = JsonObject()

        json.add("nicknames", JsonObject().apply {
            nicknames.forEach { (uuid, text) ->
                addProperty(uuid.toString(), Text.Serializer.toJson(text))
            }
        })

        json.add("disabled", JsonArray().apply {
            disabled.forEach {
                add(it.toString())
            }
        })

        file.writeText(json.toString())
    }

    fun load() {
        if (!file.exists())
            return

        val json = JsonParser.parseString(file.readText()).asJsonObject

        json.getAsJsonObject("nicknames").apply {
            entrySet().forEach { (uuid, text) ->
                nicknames[UUID.fromString(uuid)] = Text.Serializer.fromJson(text.asString) ?: Text.of("no load")
            }
        }

        json.getAsJsonArray("disabled").onEach {
            disabled.add(UUID.fromString(it.asString))
        }
    }

    fun isDisabled(uuid: UUID): Boolean {
        if (!ConfigManager.config.enabled)
            return true

        if (!nicknames.contains(uuid))
            return true

        return disabled.contains(uuid)
    }

    fun getOrDefault(id: UUID, name: Text): Text {
        val nickname = nicknames[id] ?: return name

        return LiteralText("")
            .append(name)
            .append(" (")
            .append(nickname)
            .append(")")
    }
}