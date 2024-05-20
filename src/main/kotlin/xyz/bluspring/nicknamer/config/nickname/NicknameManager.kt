package xyz.bluspring.nicknamer.config.nickname

import com.google.gson.*
import com.mojang.serialization.JsonOps
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.ConfigManager
import java.io.File
import java.util.*

object NicknameManager/*(val pathName: String)*/ {
    //private val file = File(FabricLoader.getInstance().configDir.toFile(), "nicknamer/$pathName/nicknames.json")
    private val file = File(Nicknamer.configDir, "nicknames.json")
    val nicknames = mutableMapOf<UUID, Text>()
    val disabled = mutableSetOf<UUID>()

    private val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .create()

    fun save() {
        if (!file.exists()) {
            if (!Nicknamer.configDir.exists())
                Nicknamer.configDir.mkdirs()

            file.createNewFile()
        }

        val json = JsonObject()

        json.add("nicknames", JsonObject().apply {
            nicknames.forEach { (uuid, text) ->
                addProperty(uuid.toString(), GSON.toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow(::JsonParseException)))
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
                nicknames[UUID.fromString(uuid)] = TextCodecs.CODEC.parse(
                    JsonOps.INSTANCE,
                    JsonParser.parseString(text.asString)
                ).getOrThrow(::JsonParseException) ?: Text.of("no load")
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

        return Text.literal("")
            .append(name)
            .append(" (")
            .append(nickname)
            .append(")")
    }
}