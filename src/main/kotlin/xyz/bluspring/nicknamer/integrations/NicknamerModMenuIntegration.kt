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
                            .startTextField(
                                LiteralText("In-game Nickname Format"),
                                ConfigManager.config.inGameFormat
                            )
                            .setTooltip(
                                LiteralText("Sets what the name will look like on the nameplate."),
                                LiteralText("%username% - Username"),
                                LiteralText("%nickname% - Nickname"),
                                LiteralText("%pronouns% - Pronouns")
                            )
                            .setDefaultValue("%username% (%nickname%)")
                            .setSaveConsumer {
                                ConfigManager.config.inGameFormat = it
                            }
                            .build()
                    )

                    addEntry(
                        entryBuilder()
                            .startTextField(
                                LiteralText("Player List Name Format"),
                                ConfigManager.config.playerListFormat
                            )
                            .setTooltip(
                                LiteralText("Sets what the name will look like in the player list."),
                                LiteralText("%username% - Username"),
                                LiteralText("%nickname% - Nickname"),
                                LiteralText("%pronouns% - Pronouns")
                            )
                            .setDefaultValue("%username% (%nickname%) - [%pronouns%]")
                            .setSaveConsumer {
                                ConfigManager.config.playerListFormat = it
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
            }.build()
        }
    }
}