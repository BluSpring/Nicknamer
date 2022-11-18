package xyz.bluspring.nicknamer.duck;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public interface ExtendedPlayerListEntry {
    default Text getOriginalDisplayName() {
        return new LiteralText("that didn't work");
    }
}
