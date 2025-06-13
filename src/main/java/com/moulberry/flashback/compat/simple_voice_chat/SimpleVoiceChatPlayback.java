package com.moulberry.flashback.compat.simple_voice_chat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.packet.FlashbackVoiceChatSound;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.audiochannel.ClientEntityAudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.ClientLocationalAudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.ClientStaticAudioChannel;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import net.minecraft.world.phys.Vec3;

import java.time.Duration;
import java.util.UUID;

public class SimpleVoiceChatPlayback {

    private static final Cache<UUID, ClientStaticAudioChannel> staticAudioChannelCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(1)).build();
    private static final Cache<UUID, ClientLocationalAudioChannel> locationAudioChannelCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(1)).build();
    private static final Cache<UUID, ClientEntityAudioChannel> entityAudioChannelCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(1)).build();

    public static void cleanUp() {
        staticAudioChannelCache.cleanUp();
        locationAudioChannelCache.cleanUp();
        entityAudioChannelCache.cleanUp();
    }

}
