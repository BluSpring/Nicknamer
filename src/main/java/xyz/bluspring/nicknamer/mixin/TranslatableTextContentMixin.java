package xyz.bluspring.nicknamer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.Nicknamer;
import xyz.bluspring.nicknamer.config.ConfigManager;
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry;

import java.util.stream.Collectors;

@Mixin(TranslatableTextContent.class)
public class TranslatableTextContentMixin {
    @Shadow @Final private String key;

    @Shadow @Final private Object[] args;

    @Inject(at = @At("RETURN"), method = "getArg", cancellable = true)
    public void detectNickname(int index, CallbackInfoReturnable<StringVisitable> cir) {
        if (cir.getReturnValue() == null || !(cir.getReturnValue() instanceof Text))
            return;

        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (networkHandler == null)
            return;

        if (
                this.key.startsWith("chat.type")
                && (this.args.length - 1) == index
        )
            return;

        var likelyPlayers = networkHandler.getPlayerList().stream().filter((entry) ->
                ((ExtendedPlayerListEntry) entry).getOriginalDisplayName() != null &&
                ((ExtendedPlayerListEntry) entry).getOriginalDisplayName().equals(cir.getReturnValue())
        ).toList();
        if (likelyPlayers.isEmpty())
            return;

        // This is probably inaccurate, but it works good enough.
        var player = likelyPlayers.get(0);

        var nickname = Nicknamer.Companion.setText(player.getProfile(), ConfigManager.INSTANCE.getConfig().getChatFormat(), (Text) cir.getReturnValue());
        cir.setReturnValue(nickname);
    }
}
