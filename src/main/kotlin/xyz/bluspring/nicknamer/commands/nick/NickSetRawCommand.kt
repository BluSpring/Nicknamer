package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.NicknameManager

class NickSetRawCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = MinecraftClient.getInstance().networkHandler!!.playerList.first { it.profile.name == playerName }

        val nickname = context.getArgument("nickname", Text::class.java)

        NicknameManager.nicknames[player.profile.id] = nickname

        context.source.sendFeedback(LiteralText("Set nickname for ${player.profile.name} to ").append(NicknameManager.nicknames[player.profile.id]))
        NicknameManager.save()

        return 1
    }
}