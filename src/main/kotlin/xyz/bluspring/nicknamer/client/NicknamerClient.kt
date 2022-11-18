package xyz.bluspring.nicknamer.client

import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.command.argument.TextArgumentType
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager
import xyz.bluspring.nicknamer.commands.nick.*
import xyz.bluspring.nicknamer.commands.pronouns.*
import xyz.bluspring.nicknamer.commands.pronouns.color.*
import xyz.bluspring.nicknamer.commands.pronouns.profile.*
import xyz.bluspring.nicknamer.config.nickname.NicknameManager

class NicknamerClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerCommands()

        ClientPlayConnectionEvents.JOIN.register { handler, _, client ->
            val name: String = if (handler.connection.isLocal)
                handler.world.server?.singlePlayerName ?: "unknown"
            else
                handler.connection.address.toString()

            Nicknamer.nicknameManager = NicknameManager(name)
        }
    }

    private fun registerCommands() {
        val dispatcher = ClientCommandManager.DISPATCHER

        dispatcher.register(
            ClientCommandManager
                .literal("nickc")
                .then(
                    ClientCommandManager.argument(
                        "player",
                        StringArgumentType.string()
                    )
                        .suggests { ctx, builder ->
                            builder.apply {
                                ctx.source.playerNames.forEach {
                                    suggest(it)
                                }
                            }.buildFuture()
                        }
                        .then(
                            ClientCommandManager
                                .literal("set")
                                .then(
                                    ClientCommandManager.argument(
                                        "nickname",
                                        StringArgumentType.greedyString()
                                    )
                                        .executes(NickSetCommand())
                                )
                        )
                        .then(
                            ClientCommandManager
                                .literal("setraw")
                                .then(
                                    ClientCommandManager.argument(
                                        "nickname",
                                        TextArgumentType.text()
                                    )
                                        .executes(NickSetRawCommand())
                                )
                        )
                        .then(
                            ClientCommandManager
                                .literal("setfromdisplayname")
                                .executes(NickSetFromDisplayNameCommand())
                        )
                        .then(
                            ClientCommandManager
                                .literal("get")
                                .executes(NickGetCommand())
                        )
                        .then(
                            ClientCommandManager
                                .literal("reset")
                                .executes(NickResetCommand())
                        )
                        .then(
                            ClientCommandManager
                                .literal("toggle")
                                .executes(NickToggleCommand())
                        )
                )
        )

        dispatcher.register(
            ClientCommandManager
                .literal("pronounsc")
                .then(
                    ClientCommandManager
                        .literal("color")
                        .then(
                            ClientCommandManager
                                .argument(
                                    "pronoun",
                                    StringArgumentType.string()
                                )
                                .suggests { _, builder ->
                                    builder.apply {
                                        PronounManager.pronounColors.keys.forEach {
                                            suggest(it)
                                        }
                                    }.buildFuture()
                                }
                                .then(
                                    ClientCommandManager
                                        .literal("compliments")
                                        .then(
                                            ClientCommandManager
                                                .argument(
                                                    "complimentingPronoun",
                                                    StringArgumentType.string()
                                                )
                                                .suggests { _, builder ->
                                                    builder.apply {
                                                        PronounManager.pronounColors.keys.forEach {
                                                            suggest(it)
                                                        }
                                                    }.buildFuture()
                                                }
                                                .executes(PronounsColorComplimentCommand())
                                        )
                                )
                                .then(
                                    ClientCommandManager
                                        .literal("set")
                                        .then(
                                            ClientCommandManager
                                                .argument(
                                                    "hex",
                                                    StringArgumentType.string()
                                                )
                                                .executes(PronounsColorSetCommand())
                                        )
                                )
                                .then(
                                    ClientCommandManager
                                        .literal("get")
                                        .executes(PronounsColorGetCommand())
                                )
                        )
                )
                .then(
                    ClientCommandManager
                        .literal("player")
                        .then(
                            ClientCommandManager
                                .argument(
                                    "player",
                                    StringArgumentType.string()
                                )
                                .suggests { ctx, builder ->
                                    builder.apply {
                                        ctx.source.playerNames.forEach {
                                            suggest(it)
                                        }
                                    }.buildFuture()
                                }
                                .then(
                                    ClientCommandManager
                                        .literal("get")
                                        .executes(PronounsGetCommand())
                                )
                                .then(
                                    ClientCommandManager
                                        .literal("set")
                                        .then(
                                            ClientCommandManager
                                                .argument(
                                                    "pronouns",
                                                    StringArgumentType.greedyString()
                                                )
                                                .executes(PronounsSetCommand())
                                        )
                                )
                                .then(
                                    ClientCommandManager
                                        .literal("profile")
                                        .then(
                                            ClientCommandManager
                                                .literal("set")
                                                .then(
                                                    ClientCommandManager
                                                        .argument(
                                                            "profile",
                                                            StringArgumentType.string()
                                                        )
                                                        .suggests { ctx, builder ->
                                                            builder.apply {
                                                                val playerName = StringArgumentType.getString(ctx, "player")
                                                                val player = ctx.source.client.networkHandler?.getPlayerListEntry(playerName) ?: return@apply

                                                                val profiles = PronounManager.pronounProfiles[player.profile.id] ?: return@apply

                                                                profiles.profiles.forEach { (profileName, _) ->
                                                                    suggest(profileName)
                                                                }
                                                            }.buildFuture()
                                                        }
                                                        .executes(PronounsProfileSetCommand())
                                                )
                                        )
                                        .then(
                                            ClientCommandManager
                                                .literal("get")
                                                .then(
                                                    ClientCommandManager
                                                        .argument("profile", StringArgumentType.string())
                                                        .executes(PronounsProfileGetNameCommand())
                                                )
                                                .executes(PronounsProfileGetCommand())
                                        )
                                        .then(
                                            ClientCommandManager
                                                .literal("create")
                                                .then(
                                                    ClientCommandManager
                                                        .argument("profile", StringArgumentType.string())
                                                        .executes(PronounsProfileCreateCommand())
                                                )
                                        )
                                        .then(
                                            ClientCommandManager
                                                .literal("delete")
                                                .then(
                                                    ClientCommandManager
                                                        .argument("profile", StringArgumentType.string())
                                                        .suggests { ctx, builder ->
                                                            builder.apply {
                                                                val playerName = StringArgumentType.getString(ctx, "player")
                                                                val player = ctx.source.client.networkHandler?.getPlayerListEntry(playerName) ?: return@apply

                                                                val profiles = PronounManager.pronounProfiles[player.profile.id] ?: return@apply

                                                                profiles.profiles.forEach { (profileName, _) ->
                                                                    suggest(profileName)
                                                                }
                                                            }.buildFuture()
                                                        }
                                                        .executes(PronounsProfileDeleteCommand())
                                                )
                                        )
                                        .then(
                                            ClientCommandManager
                                                .literal("list")
                                                .executes(PronounsProfileListCommand())
                                        )
                                )
                        )
                )
        )
    }
}