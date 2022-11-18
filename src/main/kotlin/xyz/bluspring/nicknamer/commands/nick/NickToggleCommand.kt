package xyz.bluspring.nicknamer.commands.nick

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.players.PlayerHelper

class NickToggleCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val player = PlayerHelper.getPlayer(playerName)

        val toggle = NicknameManager.disabled.contains(player.profile.id)

        if (toggle)
            NicknameManager.disabled.remove(player.profile.id)
        else
            NicknameManager.disabled.add(player.profile.id)

        context.source.sendFeedback(
            Text.literal(if (toggle) "Enabled" else "Disabled").formatted(if (toggle) Formatting.GREEN else Formatting.RED)
                .append(Text.literal(" nicknames for ${player.profile.name}!"))
        )
        NicknameManager.save()

        return 1
    }
}