package xyz.bluspring.nicknamer.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.NicknameManager;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow @Final private GameProfile profile;

    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (NicknameManager.INSTANCE.getNicknames().containsKey(this.profile.getId()) && !NicknameManager.INSTANCE.getDisabled().contains(this.profile.getId())) {
            cir.setReturnValue(NicknameManager.INSTANCE.getNicknames().get(this.profile.getId()));
        }
    }
}
