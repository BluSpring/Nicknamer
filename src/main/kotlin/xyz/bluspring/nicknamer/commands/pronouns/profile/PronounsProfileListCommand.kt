package xyz.bluspring.nicknamer.commands.pronouns.profile

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.PronounManager

class PronounsProfileListCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val playerUUID = Nicknamer.getPlayerUUID(playerName)

        if (playerUUID == null) {
            context.source.sendError(LiteralText("Could not find player $playerName!"))

            return 0
        }

        if (!PronounManager.pronounProfiles.contains(playerUUID)) {
            context.source.sendError(LiteralText("Player $playerName does not have any pronouns profiles!"))

            return 0
        }

        val profiles = PronounManager.pronounProfiles[playerUUID]!!

        context.source.sendFeedback(LiteralText("$playerName has the pronouns profiles:"))
        profiles.profiles.forEach { (profileName, pronouns) ->
            context.source.sendFeedback(
                LiteralText("- ")
                    .append(profileName)
                    .append(" : ")
                    .append(PronounManager.getPronounsText(pronouns))
            )
        }
        context.source.sendFeedback(LiteralText("Currently selected profile: ${profiles.currentProfile}"))

        return 1
    }
}