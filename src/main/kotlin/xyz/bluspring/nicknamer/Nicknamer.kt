package xyz.bluspring.nicknamer

import net.fabricmc.api.ModInitializer

class Nicknamer : ModInitializer {
    override fun onInitialize() {
        NicknameManager.load()
    }
}