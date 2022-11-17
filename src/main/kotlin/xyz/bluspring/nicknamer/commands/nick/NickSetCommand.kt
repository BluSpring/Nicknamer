package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.NicknameManager

class NickSetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = MinecraftClient.getInstance().networkHandler!!.playerList.first { it.profile.name == playerName }

        val nickname = StringArgumentType.getString(context, "nickname")

        NicknameManager.nicknames[player.profile.id] = LiteralText(nickname)

        context.source.sendFeedback(LiteralText("Set nickname for ${player.profile.name} to ").append(NicknameManager.nicknames[player.profile.id]))
        NicknameManager.save()

        return 1
    }
}