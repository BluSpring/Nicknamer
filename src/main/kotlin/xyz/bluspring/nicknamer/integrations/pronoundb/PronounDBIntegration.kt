package xyz.bluspring.nicknamer.integrations.pronoundb

import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import net.minecraft.text.Text
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.bluspring.nicknamer.config.pronouns.PronounManager
import java.net.URL

object PronounDBIntegration {
    private val logger: Logger = LoggerFactory.getLogger("Nicknamer PronounDB Integration")

    fun getPronounsFromDatabase(profile: GameProfile): List<String> {
        return try {
            val url = URL("https://pronoundb.org/api/v1/lookup?platform=minecraft&id=${profile.id}")
            val json = JsonParser.parseString(url.readText()).asJsonObject

            val pronounsTrunc = json.get("pronouns").asString

            val list = mutableListOf<String>()

            when (pronounsTrunc) {
                "unspecified" -> {
                    return emptyList()
                }
                "hh" -> {
                    list.add("he")
                    list.add("him")
                }
                "ii" -> {
                    list.add("it")
                    list.add("its")
                }
                "tt" -> {
                    list.add("they")
                    list.add("them")
                }
                "sh" -> {
                    list.add("she")
                    list.add("her")
                }
                "shh" -> {
                    list.add("she")
                    list.add("him")
                }
                else -> {
                    if (pronounsTrunc.length > 2) {
                        list.add(pronounsTrunc)
                    } else {
                        pronounsTrunc.forEach {
                            list.add(
                                when (it) {
                                    'h' -> "he"
                                    'i' -> "it"
                                    't' -> "they"
                                    's' -> "she"
                                    else -> ""
                                }
                            )
                        }
                    }
                }
            }

            list
        } catch (e: Exception) {
            logger.error("Failed to get pronouns from database")
            e.printStackTrace()

            listOf()
        }
    }
}