package com.moulberry.flashback.mixin.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedSet;

@IfModLoaded(value = "sodium", aliases = "embeddium")
@Pseudo
@Mixin(targets = {"net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer", "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer"})
public class MixinSodiumWorldRenderer {


}
