package xyz.bluspring.nicknamer.integrations

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.config.ConfigManager

class NicknamerModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            ConfigBuilder.create().apply {
                parentScreen = it
                title = Text.literal("Nicknamer Config")

                setSavingRunnable {
                    ConfigManager.save()
                }

                getOrCreateCategory(Text.literal("General")).apply {
                    addEntry(
                        entryBuilder()
                            .startBooleanToggle(
                                Text.literal("Is enabled?"),
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
                                Text.literal("Display pronouns below nameplate?"),
                                ConfigManager.config.displayPronounsBelowUsername
                            )
                            .setDefaultValue(true)
                            .setSaveConsumer {
                                ConfigManager.config.displayPronounsBelowUsername = it
                            }
                            .build()
                    )
                }

                getOrCreateCategory(Text.literal("In-game Name Format")).apply {
                    setDescription(
                        listOf(
                            Text.literal("Sets what the name will look like on the nameplate."),
                            Text.literal("%username% - Username"),
                            Text.literal("%nickname% - Nickname"),
                            Text.literal("%pronouns% - Pronouns"),
                            Text.literal("%displayName% - Display Name")
                        ).toTypedArray()
                    )

                    ConfigManager.config.inGameFormat.forEach { (nameFormat, format) ->
                        addEntry(
                            entryBuilder()
                                .startTextField(Text.literal(nameFormat.displayName), format)
                                .setDefaultValue("%username% (%nickname%)")
                                .setSaveConsumer {
                                    ConfigManager.config.inGameFormat[nameFormat] = it
                                }
                                .build()
                        )
                    }
                }

                getOrCreateCategory(Text.literal("Player List Name Format")).apply {
                    setDescription(
                        listOf(
                            Text.literal("Sets what the name will look like on the player list."),
                            Text.literal("%username% - Username"),
                            Text.literal("%nickname% - Nickname"),
                            Text.literal("%pronouns% - Pronouns"),
                            Text.literal("%displayName% - Display Name")
                        ).toTypedArray()
                    )

                    ConfigManager.config.playerListFormat.forEach { (nameFormat, format) ->
                        addEntry(
                            entryBuilder()
                                .startTextField(Text.literal(nameFormat.displayName), format)
                                .setDefaultValue("%username% (%nickname%) - [%pronouns%]")
                                .setSaveConsumer {
                                    ConfigManager.config.playerListFormat[nameFormat] = it
                                }
                                .build()
                        )
                    }
                }

                getOrCreateCategory(Text.literal("Chat Format")).apply {
                    setDescription(
                        listOf(
                            Text.literal("Sets what the name will look like in the chat."),
                            Text.literal("%username% - Username"),
                            Text.literal("%nickname% - Nickname"),
                            Text.literal("%pronouns% - Pronouns"),
                            Text.literal("%displayName% - Display Name")
                        ).toTypedArray()
                    )

                    ConfigManager.config.chatFormat.forEach { (nameFormat, format) ->
                        addEntry(
                            entryBuilder()
                                .startTextField(Text.literal(nameFormat.displayName), format)
                                .setDefaultValue("%username% (%nickname%) - [%pronouns%]")
                                .setSaveConsumer {
                                    ConfigManager.config.chatFormat[nameFormat] = it
                                }
                                .build()
                        )
                    }
                }
            }.build()
        }
    }
}