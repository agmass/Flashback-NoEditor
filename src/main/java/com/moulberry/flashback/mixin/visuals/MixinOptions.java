package com.moulberry.flashback.mixin.visuals;

import com.moulberry.flashback.ext.OptionsExt;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class MixinOptions implements OptionsExt {

    @Shadow
    @Final
    private OptionInstance<Integer> fov;
    @Unique
    private OptionInstance<Integer> cachedFovOptionInstance = null;


    @Override
    public int flashback$getOriginalFov() {
        return this.fov.get();
    }
}
