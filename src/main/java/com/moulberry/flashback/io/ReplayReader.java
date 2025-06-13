package com.moulberry.flashback.io;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.action.ActionRegistry;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ReplayReader {

    private final FriendlyByteBuf friendlyByteBuf;
    private final int replaySnapshotOffset;
    private final int replayActionsOffset;
    private RegistryAccess registryAccess;
    private ResourceLocation lastActionName = null;
    private final Int2ObjectMap<Action> actions = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<ResourceLocation> ignoredActions = new Int2ObjectOpenHashMap<>();

    public ReplayReader(ByteBuf byteBuf, RegistryAccess registryAccess) {
        this.friendlyByteBuf = new FriendlyByteBuf(byteBuf);
        this.registryAccess = registryAccess;

        int magic = this.friendlyByteBuf.readInt();
        if (magic != Flashback.MAGIC) {
            throw new RuntimeException("Invalid magic");
        }

        int actions = this.friendlyByteBuf.readVarInt();
        for (int i = 0; i < actions; i++) {
            ResourceLocation actionName = this.friendlyByteBuf.readResourceLocation();
            Action action = ActionRegistry.getAction(actionName);

            if (action == null) {
                if (actionName.getPath().endsWith("optional")) {
                    this.ignoredActions.put(i, actionName);
                } else {
                    throw new RuntimeException("Missing action: " + actionName);
                }
            } else {
                this.actions.put(i, action);
            }
        }

        int snapshotSize = this.friendlyByteBuf.readInt();
        if (snapshotSize < 0) {
            throw new RuntimeException("Invalid snapshot size: " + snapshotSize + " (0x" + Integer.toHexString(snapshotSize) + ")");
        }
        this.replaySnapshotOffset = this.friendlyByteBuf.readerIndex();
        this.friendlyByteBuf.skipBytes(snapshotSize);
        this.replayActionsOffset = this.friendlyByteBuf.readerIndex();
    }

    public void changeRegistryAccess(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
    }

    public void resetToStart() {
        this.friendlyByteBuf.readerIndex(this.replayActionsOffset);
    }



}
