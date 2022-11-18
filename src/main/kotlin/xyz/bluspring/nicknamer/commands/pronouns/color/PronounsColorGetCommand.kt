package xyz.bluspring.nicknamer.commands.pronouns.color

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsColorGetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val pronoun = StringArgumentType.getString(context, "pronoun")

        if (!PronounManager.pronounColors.contains(pronoun.lowercase())) {
            context.source.sendError(LiteralText("The pronoun \"${Nicknamer.toTitleCase(pronoun.lowercase())}\" is not registered!").formatted(Formatting.RED))

            return 0
        }

        context.source.sendFeedback(
            LiteralText("Pronoun registered as ")
                .append(
                    LiteralText(Nicknamer.toTitleCase(pronoun.lowercase())).styled {
                        it.withColor(PronounManager.getOrCreatePronounColor(pronoun))
                    }
                )
        )

        return 1
    }
}