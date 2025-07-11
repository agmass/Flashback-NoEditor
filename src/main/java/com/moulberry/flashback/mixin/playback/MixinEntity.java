package com.moulberry.flashback.mixin.playback;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.flashback.Flashback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public abstract boolean isInvisible();

    @Shadow
    private Level level;

    @Shadow
    public abstract boolean isInvisibleTo(Player player);

    @Shadow
    public abstract UUID getUUID();

    // Force entities to be able to ride players on servers
    @WrapOperation(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    public boolean startRiding_isClientSide(Level instance, Operation<Boolean> original) {
        if (Flashback.isInReplay()) {
            return true; // Always pretend we're clientside so mounting players is allowed
        }
        return original.call(instance);
    }



}
