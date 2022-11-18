package xyz.bluspring.nicknamer.duck;

import net.minecraft.text.Text;

public interface ExtendedPlayerListEntry {
    default Text getOriginalDisplayName() {
        return Text.literal("that didn't work");
    }
}
