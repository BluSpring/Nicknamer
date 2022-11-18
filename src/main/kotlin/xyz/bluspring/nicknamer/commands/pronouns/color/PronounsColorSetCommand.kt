package xyz.bluspring.nicknamer.commands.pronouns.color

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsColorSetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val pronoun = StringArgumentType.getString(context, "pronoun")
        val color = StringArgumentType.getString(context, "hex")

        val textColor = TextColor.parse(color)

        if (textColor == null) {
            context.source.sendError(Text.literal("The color given is invalid!").formatted(Formatting.RED))

            return 0
        }

        PronounManager.pronounColors[pronoun] = textColor
        context.source.sendFeedback(
            Text.literal("Set pronoun color to ")
                .append(
                    Text.literal(
                        Nicknamer.toTitleCase(pronoun.lowercase())
                    ).styled {
                        it.withColor(textColor)
                    }
                )
        )

        return 1
    }
}