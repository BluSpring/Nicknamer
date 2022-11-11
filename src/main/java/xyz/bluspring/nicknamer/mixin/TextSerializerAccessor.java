package xyz.bluspring.nicknamer.mixin;

import com.google.gson.Gson;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Text.Serializer.class)
public interface TextSerializerAccessor {
    // @formatter:off
    @Accessor static Gson getGSON() {
        throw new AssertionError();
    }
}