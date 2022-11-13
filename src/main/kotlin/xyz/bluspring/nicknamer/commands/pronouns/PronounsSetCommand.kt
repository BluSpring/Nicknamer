package xyz.bluspring.nicknamer.commands.pronouns

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.text.LiteralText
import xyz.bluspring.nicknamer.Nicknamer
import xyz.bluspring.nicknamer.PronounManager

class PronounsSetCommand<T : FabricClientCommandSource> : Command<T> {
    override fun run(context: CommandContext<T>): Int {
        val playerName = StringArgumentType.getString(context, "player")
        val playerUUID = Nicknamer.getPlayerUUID(playerName)

        if (playerUUID == null) {
            context.source.sendError(LiteralText("Could not find player $playerName!"))

            return 0
        }

        val pronouns = StringArgumentType.getString(context, "pronouns")
        PronounManager.pronouns[playerUUID] = pronouns.split("/").toMutableList()

        val profiles = PronounManager.pronounProfiles[playerUUID]
        if (profiles != null) {
            profiles.profiles[profiles.currentProfile] = PronounManager.pronouns[playerUUID]!!
        }

        context.source.sendFeedback(
            LiteralText("Successfully set $playerName's pronouns to ")
                .append(PronounManager.getPronounsText(playerUUID))
        )

        return 1
    }
}