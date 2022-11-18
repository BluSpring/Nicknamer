package xyz.bluspring.nicknamer.commands.pronouns.color

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsColorComplimentCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val pronoun = StringArgumentType.getString(context, "pronoun")
        val complimenting = StringArgumentType.getString(context, "complimentingPronoun")

        if (!PronounManager.pronounColors.contains(complimenting.lowercase())) {
            context.source.sendError(LiteralText("Compliment pronoun $complimenting is not registered!").formatted(Formatting.RED))

            return 0
        }

        val color = PronounManager.getComplementaryPronounColor(complimenting)

        PronounManager.pronounColors[pronoun.lowercase()] = color

        context.source.sendFeedback(
            LiteralText("Set pronoun color to ")
                .append(
                    LiteralText(
                        Nicknamer.toTitleCase(pronoun.lowercase())
                    ).styled {
                        it.withColor(color)
                    }
                )
        )

        return 1
    }
}