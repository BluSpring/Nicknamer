package xyz.bluspring.nicknamer.commands.pronouns.profile

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsProfileGetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val playerUUID = Nicknamer.getPlayerUUID(playerName)

        if (playerUUID == null) {
            context.source.sendError(Text.literal("Could not find player $playerName!"))

            return 0
        }

        if (!PronounManager.pronounProfiles.contains(playerUUID)) {
            context.source.sendError(Text.literal("Pronouns profile does not exist!"))

            return 0
        }

        val profiles = PronounManager.pronounProfiles[playerUUID]!!
        val profile = profiles.profiles[profiles.currentProfile]!!

        context.source.sendFeedback(
            Text.literal("Pronouns profile ${profiles.currentProfile} has the pronouns ")
                .append(PronounManager.getPronounsText(profile))
        )

        return 1
    }
}