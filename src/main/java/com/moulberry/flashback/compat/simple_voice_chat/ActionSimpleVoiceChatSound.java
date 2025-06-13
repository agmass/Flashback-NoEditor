package com.moulberry.flashback.compat.simple_voice_chat;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.action.Action;
import com.moulberry.flashback.packet.FlashbackVoiceChatSound;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ActionSimpleVoiceChatSound implements Action {
    private static final ResourceLocation NAME = Flashback.createResourceLocation("action/simple_voice_chat_sound_optional");
    public static final ActionSimpleVoiceChatSound INSTANCE = new ActionSimpleVoiceChatSound();
    private ActionSimpleVoiceChatSound() {
    }

    @Override
    public ResourceLocation name() {
        return NAME;
    }

}
