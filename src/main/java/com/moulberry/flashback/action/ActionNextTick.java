package com.moulberry.flashback.action;

import com.moulberry.flashback.Flashback;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ActionNextTick implements Action {

    private static final ResourceLocation NAME = Flashback.createResourceLocation("action/next_tick");
    public static final ActionNextTick INSTANCE = new ActionNextTick();
    private ActionNextTick() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }


}
