package xyz.bluspring.nicknamer.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.NicknameManager

class NickToggleCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "entity")
        val player = MinecraftClient.getInstance().networkHandler!!.playerList.first { it.profile.name == playerName }

        val toggle = NicknameManager.disabled.contains(player.profile.id)

        if (toggle)
            NicknameManager.disabled.remove(player.profile.id)
        else
            NicknameManager.disabled.add(player.profile.id)

        context.source.sendFeedback(
            LiteralText(if (toggle) "Enabled" else "Disabled").formatted(if (toggle) Formatting.GREEN else Formatting.RED)
                .append(LiteralText(" nicknames for ${player.profile.name}!"))
        )
        NicknameManager.save()

        return 1
    }
}