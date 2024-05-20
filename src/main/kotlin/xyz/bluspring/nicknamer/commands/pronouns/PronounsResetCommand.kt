package xyz.bluspring.nicknamer.commands.pronouns

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.config.pronouns.PronounManager

class PronounsResetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val playerUUID = Nicknamer.getPlayerUUID(playerName)

        if (playerUUID == null) {
            context.source.sendError(Text.literal("Could not find player $playerName!"))

            return 0
        }

        PronounManager.pronouns.remove(playerUUID)

        val profiles = PronounManager.pronounProfiles[playerUUID]
        profiles?.profiles?.remove(profiles.currentProfile)

        PronounManager.save()

        context.source.sendFeedback(
            Text.literal("Successfully set $playerName's pronouns to ")
                .append(PronounManager.getPronounsText(playerUUID))
        )

        return 1
    }
}