package xyz.bluspring.nicknamer.commands.pronouns.profile

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager
import xyz.bluspring.nicknamer.config.pronouns.PronounProfiles

class PronounsProfileCreateCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val playerUUID = Nicknamer.getPlayerUUID(playerName)

        if (playerUUID == null) {
            context.source.sendError(LiteralText("Could not find player $playerName!"))

            return 0
        }

        val profileName = StringArgumentType.getString(context, "profile")
        val profiles = PronounManager.pronounProfiles[playerUUID] ?: PronounProfiles(mutableMapOf(), profileName)

        profiles.profiles[profileName] = PronounManager.pronouns[playerUUID] ?: mutableListOf()
        profiles.currentProfile = profileName

        PronounManager.pronounProfiles[playerUUID] = profiles

        context.source.sendFeedback(
            LiteralText("Created pronouns profile $profileName. It has been auto-selected, and run \"/pronounsc set <pronouns>\" to set the pronouns for the profile.")
        )

        return 1
    }
}