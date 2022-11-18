package xyz.bluspring.nicknamer.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.Nicknamer;
import xyz.bluspring.nicknamer.config.ConfigManager;
import xyz.bluspring.nicknamer.config.nickname.NicknameManager;
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements ExtendedPlayerListEntry {
    @Shadow @Final private GameProfile profile;

    @Shadow @Nullable private Text displayName;

    @Override
    @Unique
    public Text getOriginalDisplayName() {
        return this.displayName;
    }

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!NicknameManager.INSTANCE.isDisabled(this.profile.getId())) {
            cir.setReturnValue(
                    Nicknamer.Companion.setText(
                            this.profile,
                            ConfigManager.INSTANCE.getConfig().getPlayerListFormat(),
                            cir.getReturnValue() != null ? cir.getReturnValue() : Text.literal(this.profile.getName())
                    )
            );
        }
    }
}
