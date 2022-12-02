package xyz.bluspring.nicknamer.config.pronouns

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.text.Texts
import net.minecraft.util.math.MathHelper
import xyz.bluspring.nicknamer.Nicknamer
import java.awt.Color
import java.io.File
import java.util.*
import kotlin.random.Random

object PronounManager {
    private val file = File(Nicknamer.configDir, "pronouns.json")
    val pronouns = mutableMapOf<UUID, MutableList<String>>()
    val pronounColors = mutableMapOf<String, TextColor>()
    // This is something designed specifically for systems and genderfluid people.
    // However, this is also a pretty useful tool for RPs.
    val pronounProfiles = mutableMapOf<UUID, PronounProfiles>()

    fun save() {
        if (!file.exists()) {
            if (!Nicknamer.configDir.exists())
                Nicknamer.configDir.mkdirs()

            file.createNewFile()
        }

        val json = JsonObject()

        json.add("pronouns", JsonObject().apply {
            pronouns.forEach { (uuid, list) ->
                add(uuid.toString(), JsonArray().apply {
                    list.forEach { pronoun ->
                        add(pronoun)
                    }
                })
            }
        })

        json.add("pronounColors", JsonObject().apply {
            pronounColors.forEach { (pronoun, color) ->
                addProperty(pronoun, color.rgb)
            }
        })

        json.add("pronounProfiles", JsonObject().apply {
            pronounProfiles.forEach { (player, profile) ->
                add(player.toString(), JsonObject().apply {
                    val profiles = JsonObject()

                    profile.profiles.forEach { (profileName, pronouns) ->
                        profiles.add(profileName, JsonArray().apply {
                            pronouns.forEach {
                                add(it)
                            }
                        })
                    }

                    add("profiles", profiles)
                    addProperty("currentProfile", profile.currentProfile)
                })
            }
        })

        file.writeText(json.toString())
    }

    fun load() {
        if (!file.exists())
            return

        val json = JsonParser.parseString(file.readText()).asJsonObject

        json.getAsJsonObject("pronouns").apply {
            entrySet().forEach { (uuid, pronounsArray) ->
                pronouns[UUID.fromString(uuid)] = pronounsArray.asJsonArray.map {
                    it.asString
                }.toMutableList()
            }
        }

        json.getAsJsonObject("pronounColors").apply {
            entrySet().forEach { (pronoun, color) ->
                pronounColors[pronoun] = TextColor.fromRgb(color.asInt)
            }
        }

        json.getAsJsonObject("pronounProfiles").apply {
            entrySet().forEach { (uuid, profiles) ->
                pronounProfiles[UUID.fromString(uuid)] = PronounProfiles(
                    mutableMapOf<String, MutableList<String>>().apply {
                        profiles.asJsonObject.getAsJsonObject("profiles").entrySet().forEach {
                            this[it.key] = mutableListOf<String>().apply {
                                it.value.asJsonArray.forEach { pronoun ->
                                    add(pronoun.asString)
                                }
                            }
                        }
                    },
                    profiles.asJsonObject.get("currentProfile").asString
                )
            }
        }

        Nicknamer.logger.info("Loaded ${pronouns.size} player pronouns, ${pronounColors.size} pronoun colours, ${pronounProfiles.size} profiles")
    }

    fun getOrDefault(id: UUID, name: Text): Text {
        val pronounList = pronouns[id] ?: return name

        return Text.literal("")
            .append(name)
            .append(" [")
            .append(
                getPronounsText(pronounList)
            )
            .append("]")
    }

    fun getPronounsText(id: UUID): Text {
        val pronounList = pronouns[id] ?: return Text.empty()
        return getPronounsText(pronounList)
    }

    fun getPronounsText(list: List<String>): Text {
        return Texts.join(
            list.map { pronoun ->
                Text.literal(
                    Nicknamer.toTitleCase(pronoun)
                ).styled {
                    it.withColor(getOrCreatePronounColor(pronoun))
                }
            },
            Text.literal("/")
        )
    }

    fun getComplementaryPronounColor(pronoun: String): TextColor {
        val mainPronounColor = getOrCreatePronounColor(pronoun)
        val colorConverted = Color(mainPronounColor.rgb)

        val hsb = Color.RGBtoHSB(colorConverted.red, colorConverted.green, colorConverted.blue, null)

        val color = Color.getHSBColor(
            hsb[0],
            MathHelper.clamp(hsb[1] + 0.06F, 0F, 1F),
            MathHelper.clamp(hsb[2] - 0.2F, 0F, 1F)
        )

        return TextColor.fromRgb(color.rgb)
    }

    fun getOrCreatePronounColor(pronoun: String): TextColor {
        if (!pronounColors.contains(pronoun.lowercase())) {
            // Generate random colours for unknown pronouns
            val color = Color.getHSBColor(
                Random.nextFloat(),
                MathHelper.clamp(Random.nextFloat(), 0.56F, 0.71F),
                MathHelper.clamp(Random.nextFloat(), 0.42F, 0.78F)
            )

            val textColor = TextColor.fromRgb(color.rgb)
            pronounColors[pronoun.lowercase()] = textColor
            save()
        }

        return pronounColors[pronoun.lowercase()]!!
    }
}