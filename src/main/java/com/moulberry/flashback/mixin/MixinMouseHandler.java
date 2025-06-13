package com.moulberry.flashback.mixin;

import com.moulberry.flashback.Flashback;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {



    @Inject(method = {"onPress", "onScroll", "onMove"}, at = @At("HEAD"), cancellable = true)
    public void onUseMouse(CallbackInfo ci) {
        if (Flashback.isExporting()) {
            ci.cancel();
        }
    }

}
