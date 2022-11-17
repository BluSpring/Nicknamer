package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class NickGetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = MinecraftClient.getInstance().networkHandler!!.playerList.first { it.profile.name == playerName }

        context.source.sendFeedback(LiteralText("Nickname for ${player.profile.name} is ").append(player.displayName ?: Text.of(player.profile.name)))

        return 1
    }
}