package com.moulberry.flashback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.moulberry.flashback.serialization.QuaterniondTypeAdapater;
import com.moulberry.flashback.serialization.QuaternionfTypeAdapater;
import com.moulberry.flashback.serialization.Vector3dTypeAdapater;
import com.moulberry.flashback.serialization.Vector3fTypeAdapater;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class FlashbackGson {

    public static Gson PRETTY = build().setPrettyPrinting().create();
    public static Gson COMPRESSED = build().create();

    private static GsonBuilder build() {
        return new GsonBuilder()
            .registerTypeAdapter(Vector3f.class, new Vector3fTypeAdapater())
            .registerTypeAdapter(Quaternionf.class, new QuaternionfTypeAdapater())
            .registerTypeAdapter(Vector3d.class, new Vector3dTypeAdapater())
            .registerTypeAdapter(Quaterniond.class, new QuaterniondTypeAdapater())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
    }

}
