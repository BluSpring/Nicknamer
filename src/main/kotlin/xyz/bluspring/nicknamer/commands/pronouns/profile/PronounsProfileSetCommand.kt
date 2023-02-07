package xyz.bluspring.nicknamer.commands.pronouns.profile

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsProfileSetCommand<T : FabricClientCommandSource> : Command<T> {
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

        val profileName = StringArgumentType.getString(context, "profile")

        if (!PronounManager.pronounProfiles.contains(playerUUID) || !PronounManager.pronounProfiles[playerUUID]!!.profiles.contains(profileName)) {
            context.source.sendError(Text.literal("Pronouns profile $profileName does not exist!"))

            return 0
        }

        val profiles = PronounManager.pronounProfiles[playerUUID]!!
        val profile = profiles.profiles[profileName]!!

        profiles.currentProfile = profileName

        PronounManager.pronouns[playerUUID] = profile

        PronounManager.save()

        context.source.sendFeedback(
            Text.literal("Successfully set $playerName's pronouns profile to $profileName with the pronouns ")
                .append(PronounManager.getPronounsText(profile))
        )

        return 1
    }
}