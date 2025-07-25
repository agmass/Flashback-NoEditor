package com.moulberry.flashback.mixin.compat.bobby;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.record.FlashbackMeta;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import de.johni0702.minecraft.bobby.ChunkSerializer;
import de.johni0702.minecraft.bobby.FakeChunkManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@IfModLoaded("bobby")
@Pseudo
@Mixin(value = FakeChunkManager.class, remap = false)
public class MixinBobbyFakeChunkManager {

    @Inject(method = "save", at = @At("HEAD"), require = 0, cancellable = true)
    public void save(LevelChunk chunk, CallbackInfoReturnable<Supplier<LevelChunk>> cir) {
        if (Flashback.isInReplay()) {
            Pair<LevelChunk, Supplier<LevelChunk>> copy = ChunkSerializer.shallowCopy(chunk);
            cir.setReturnValue(copy.getRight());
        }
    }

}
