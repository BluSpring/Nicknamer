package xyz.bluspring.nicknamer.players

import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import xyz.bluspring.nicknamer.config.nickname.NicknameManager
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry

open class NicknamerPlayer(val profile: GameProfile) {
    val displayName: Text
        get() {
            return NicknameManager.getOrDefault(profile.id, LiteralText(profile.name))
        }

    val originalDisplayName: Text?
        get() {
            val entry = MinecraftClient.getInstance().networkHandler?.getPlayerListEntry(profile.id) ?: return null

            return (entry as ExtendedPlayerListEntry).originalDisplayName
        }
}