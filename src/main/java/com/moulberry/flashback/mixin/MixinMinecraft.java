package com.moulberry.flashback.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.systems.RenderSystem;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.FreezeSlowdownFormula;
import com.moulberry.flashback.configuration.FlashbackConfig;
import com.moulberry.flashback.ext.MinecraftExt;
import com.moulberry.flashback.visuals.AccurateEntityPositionHandler;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements MinecraftExt {

    @Shadow
    public abstract void disconnect();

    @Shadow
    @Final
    private AtomicReference<StoringChunkProgressListener> progressListener;

    @Shadow
    @Final
    private YggdrasilAuthenticationService authenticationService;

    @Shadow
    @Final
    public File gameDirectory;

    @Shadow
    private @Nullable IntegratedServer singleplayerServer;

    @Shadow
    private boolean isLocalServer;

    @Shadow
    public abstract void updateReportEnvironment(ReportEnvironment reportEnvironment);

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow
    private @Nullable Overlay overlay;

    @Shadow
    protected abstract void runTick(boolean bl);

    @Shadow
    protected abstract void handleDelayedCrash();

    @Shadow
    public abstract User getUser();

    @Shadow
    private @Nullable Connection pendingConnection;

    @Shadow
    @Final
    private Queue<Runnable> progressTasks;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public Entity cameraEntity;

    @Shadow
    @Final
    public DeltaTracker.Timer deltaTracker;

    @Shadow
    public long clientTickCount;

    @Shadow @Final public Options options;

    @Shadow @Final public LevelRenderer levelRenderer;

    @Shadow
    protected abstract float getTickTargetMillis(float f);


    @Unique
    private boolean inReplayLast = false;

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (Flashback.RECORDER != null) {
            Flashback.RECORDER.endTick(false);
        }
    }

    @Unique
    private int serverTickFreezeDelayStart = -1;
    @Unique
    private double clientTickFreezeDelayStart = -1;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    public void disconnectHead(Screen screen, boolean isTransferring, CallbackInfo ci) {
        try {
            if (Flashback.getConfig().automaticallyFinish && Flashback.RECORDER != null && !isTransferring) {
                Flashback.finishRecordingReplay();
            }
        } catch (Exception e) {
            Flashback.LOGGER.error("Failed to finish replay on disconnect", e);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("RETURN"))
    public void disconnectReturn(Screen screen, boolean bl, CallbackInfo ci) {
        Flashback.updateIsInReplay();
    }

    @Unique
    private final DeltaTracker.Timer localPlayerTimer = new DeltaTracker.Timer(20.0f, 0, FloatUnaryOperator.identity());

    @Override
    public boolean flashback$overridingLocalPlayerTimer() {
        return !Flashback.isExporting() && this.level != null && this.player != null && !this.player.isPassenger() && !this.player.isRemoved() && Math.round(this.getTickTargetMillis(50)) != 50;
    }

    @Override
    public float flashback$getLocalPlayerPartialTick(float originalPartialTick) {
        if (this.cameraEntity != this.player || !this.flashback$overridingLocalPlayerTimer()) {
            return originalPartialTick;
        }
        return this.localPlayerTimer.getGameTimeDeltaPartialTick(true);
    }

    @Unique
    private final AtomicBoolean applyKeyframes = new AtomicBoolean(false);

    @Override
    public void flashback$applyKeyframes() {
        this.applyKeyframes.set(true);
    }


}
