package xyz.bluspring.nicknamer.commands.pronouns.color

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.PronounManager
import java.awt.Color

class PronounsColorSetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val pronoun = StringArgumentType.getString(context, "pronoun")
        val color = StringArgumentType.getString(context, "color")

        val textColor = TextColor.parse(color)

        if (textColor == null) {
            context.source.sendError(LiteralText("The color given is invalid!").formatted(Formatting.RED))

            return 0
        }

        PronounManager.pronounColors[pronoun] = textColor
        context.source.sendFeedback(
            LiteralText("Set pronoun color to ")
                .append(
                    LiteralText(
                        Nicknamer.toTitleCase(pronoun.lowercase())
                    ).styled {
                        it.withColor(textColor)
                    }
                )
        )

        return 1
    }
}