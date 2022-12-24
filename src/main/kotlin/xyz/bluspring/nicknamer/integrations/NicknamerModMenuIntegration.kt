package xyz.bluspring.nicknamer.integrations

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.config.ConfigManager

class NicknamerModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            ConfigBuilder.create().apply {
                parentScreen = it
                title = LiteralText("Nicknamer Config")

                setSavingRunnable {
                    ConfigManager.save()
                }

                getOrCreateCategory(LiteralText("General")).apply {
                    addEntry(
                        entryBuilder()
                            .startBooleanToggle(
                                LiteralText("Is enabled?"),
                                ConfigManager.config.enabled
                            )
                            .setDefaultValue(false)
                            .setSaveConsumer {
                                ConfigManager.config.enabled = it
                            }
                            .build()
                    )

                    addEntry(
                        entryBuilder()
                            .startBooleanToggle(
                                LiteralText("Display pronouns below nameplate?"),
                                ConfigManager.config.displayPronounsBelowUsername
                            )
                            .setDefaultValue(true)
                            .setSaveConsumer {
                                ConfigManager.config.displayPronounsBelowUsername = it
                            }
                            .build()
                    )
                }

                getOrCreateCategory(LiteralText("In-game Name Format")).apply {
                    setDescription(
                        listOf(
                            LiteralText("Sets what the name will look like on the nameplate."),
                            LiteralText("%username% - Username"),
                            LiteralText("%nickname% - Nickname"),
                            LiteralText("%pronouns% - Pronouns"),
                            LiteralText("%displayName% - Display Name")
                        ).toTypedArray()
                    )

                    ConfigManager.config.inGameFormat.forEach { (nameFormat, format) ->
                        addEntry(
                            entryBuilder()
                                .startTextField(LiteralText(nameFormat.displayName), format)
                                .setDefaultValue("%username% (%nickname%)")
                                .setSaveConsumer {
                                    ConfigManager.config.inGameFormat[nameFormat] = it
                                }
                                .build()
                        )
                    }
                }

                getOrCreateCategory(LiteralText("Player List Name Format")).apply {
                    setDescription(
                        listOf(
                            LiteralText("Sets what the name will look like on the player list."),
                            LiteralText("%username% - Username"),
                            LiteralText("%nickname% - Nickname"),
                            LiteralText("%pronouns% - Pronouns"),
                            LiteralText("%displayName% - Display Name")
                        ).toTypedArray()
                    )

                    ConfigManager.config.playerListFormat.forEach { (nameFormat, format) ->
                        addEntry(
                            entryBuilder()
                                .startTextField(LiteralText(nameFormat.displayName), format)
                                .setDefaultValue("%username% (%nickname%) - [%pronouns%]")
                                .setSaveConsumer {
                                    ConfigManager.config.playerListFormat[nameFormat] = it
                                }
                                .build()
                        )
                    }
                }
            }.build()
        }
    }
}