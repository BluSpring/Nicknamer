package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.players.PlayerHelper

class NickResetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = PlayerHelper.getPlayer(playerName)

        NicknameManager.nicknames.remove(player.profile.id)

        context.source.sendFeedback(LiteralText("Reset nickname for ${player.profile.name}!"))
        NicknameManager.save()

        return 1
    }
}