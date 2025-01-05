package xyz.bluspring.nicknamer.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.bluspring.nicknamer.Nicknamer;
import xyz.bluspring.nicknamer.config.ConfigManager;
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry;

@Mixin(value = TranslatableTextContent.class, priority = 1100)
public class TranslatableTextContentMixin {
    @Shadow @Final private String key;

    @Shadow @Final private Object[] args;

    @ModifyReturnValue(at = @At("RETURN"), method = "getArg")
    public StringVisitable detectNickname(StringVisitable original, @Local(argsOnly = true) int index) {
        if (original == null || !(original instanceof Text))
            return original;

        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (networkHandler == null)
            return original;

        if (
                this.key.startsWith("chat.type")
                && (this.args.length - 1) == index
        )
            return original;

        var likelyPlayers = networkHandler.getPlayerList().stream().filter((entry) ->
                (((ExtendedPlayerListEntry) entry).getOriginalDisplayName() != null &&
                ((ExtendedPlayerListEntry) entry).getOriginalDisplayName().equals(original)) ||
                    entry.getProfile().getName().equals(original.getString())
        ).toList();
        if (likelyPlayers.isEmpty())
            return original;

        // This is probably inaccurate, but it works good enough.
        var player = likelyPlayers.get(0);

        return Nicknamer.Companion.setText(player.getProfile(), ConfigManager.INSTANCE.getConfig().getChatFormat(), (Text) original);
    }
}
