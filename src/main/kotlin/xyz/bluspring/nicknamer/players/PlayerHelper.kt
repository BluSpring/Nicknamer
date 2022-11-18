package xyz.bluspring.nicknamer.players

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import xyz.bluspring.nicknamer.Nicknamer

object PlayerHelper {
    fun getPlayer(name: String): NicknamerPlayer {
        val entry = MinecraftClient.getInstance().networkHandler?.getPlayerListEntry(name)

        return if (entry == null) {
            val uuid = Nicknamer.getPlayerUUID(name)

            NicknamerPlayer(
                GameProfile(
                    uuid, name
                )
            )
        } else {
            NicknamerPlayer(entry.profile)
        }
    }
}