package xyz.bluspring.nicknamer.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.NicknameManager;
import xyz.bluspring.nicknamer.PronounManager;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow @Final private GameProfile profile;

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (NicknameManager.INSTANCE.getNicknames().containsKey(this.profile.getId()) && !NicknameManager.INSTANCE.getDisabled().contains(this.profile.getId())) {
            cir.setReturnValue(
                    PronounManager.INSTANCE.getOrDefault(
                            this.profile.getId(),
                            NicknameManager.INSTANCE.getOrDefault(
                                    this.profile.getId(),
                                    cir.getReturnValue()
                            )
                    )
            );
        }
    }
}
