package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.players.PlayerHelper

class NickSetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = PlayerHelper.getPlayer(playerName)

        val nickname = StringArgumentType.getString(context, "nickname")

        NicknameManager.nicknames[player.profile.id] = Text.literal(nickname)

        context.source.sendFeedback(Text.literal("Set nickname for ${player.profile.name} to ").append(NicknameManager.nicknames[player.profile.id]))
        NicknameManager.save()

        return 1
    }
}