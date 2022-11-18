package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry

class NickSetFromDisplayNameCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = MinecraftClient.getInstance().networkHandler!!.playerList.first { it.profile.name == playerName }

        NicknameManager.nicknames[player.profile.id] = (player as ExtendedPlayerListEntry).originalDisplayName ?: LiteralText(player.profile.name)

        context.source.sendFeedback(LiteralText("Set nickname for ${player.profile.name} to ").append(NicknameManager.nicknames[player.profile.id]))
        NicknameManager.save()

        return 1
    }
}