package xyz.bluspring.nicknamer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.config.nickname.NicknameManager;

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

        var player = networkHandler.getPlayerListEntry(cir.getReturnValue().getString());

        if (player == null)
            return;

        var nickname = NicknameManager.INSTANCE.getOrDefault(player.getProfile().getId(), (Text) cir.getReturnValue());
        cir.setReturnValue(nickname);
    }
}
