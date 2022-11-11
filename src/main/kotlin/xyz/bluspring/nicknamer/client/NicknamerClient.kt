package xyz.bluspring.nicknamer.client

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.Suggestions
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.TextArgumentType
import xyz.bluspring.nicknamer.commands.*

class NicknamerClient : ClientModInitializer {
    override fun onInitializeClient() {
        val dispatcher = ClientCommandManager.DISPATCHER

        dispatcher.register(
            ClientCommandManager
                .literal("nickc")
                .then(
                    ClientCommandManager.argument(
                        "entity",
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
    }
}