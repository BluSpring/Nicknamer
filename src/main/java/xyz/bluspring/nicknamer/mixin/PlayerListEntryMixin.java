package xyz.bluspring.nicknamer.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.nicknamer.Nicknamer;
import xyz.bluspring.nicknamer.config.ConfigManager;
import xyz.bluspring.nicknamer.config.nickname.NicknameManager;
import xyz.bluspring.nicknamer.config.pronouns.PronounManager;
import xyz.bluspring.nicknamer.duck.ExtendedPlayerListEntry;
import xyz.bluspring.nicknamer.integrations.pronoundb.PronounDBIntegration;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements ExtendedPlayerListEntry {
    @Shadow @Final private GameProfile profile;

    @Shadow @Nullable private Text displayName;

    @Override
    @Unique
    public Text getOriginalDisplayName() {
        return this.displayName;
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void tryLoadPronounDB(PlayerListS2CPacket.Entry playerListPacketEntry, SignatureVerifier servicesSignatureVerifier, boolean secureChatEnforced, CallbackInfo ci) {
        // Don't overwrite pronouns
        if (PronounManager.INSTANCE.getPronouns().containsKey(playerListPacketEntry.getProfile().getId()))
            return;

        var pronounList = PronounDBIntegration.INSTANCE.getPronounsFromDatabase(playerListPacketEntry.getProfile());

        if (!pronounList.isEmpty())
            PronounManager.INSTANCE.getPronouns().put(playerListPacketEntry.getProfile().getId(), pronounList);
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
