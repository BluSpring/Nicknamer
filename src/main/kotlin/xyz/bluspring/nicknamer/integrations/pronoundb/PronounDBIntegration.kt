package xyz.bluspring.nicknamer.integrations.pronoundb

import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.CompletableFuture

object PronounDBIntegration {
    private val logger: Logger = LoggerFactory.getLogger("Nicknamer PronounDB Integration")

    fun getPronounsFromDatabase(profile: GameProfile): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync {
            try {
                val url = URL("https://pronoundb.org/api/v1/lookup?platform=minecraft&id=${profile.id}")
                val json = JsonParser.parseString(url.readText()).asJsonObject

                val pronounsTrunc = json.get("pronouns").asString

                val list = mutableListOf<String>()

                when (pronounsTrunc) {
                    "unspecified" -> {
                        return@supplyAsync emptyList()
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

                emptyList()
            }
        }
    }
}