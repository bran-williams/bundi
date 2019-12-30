package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.util.HttpUtils;
import com.branwilliams.mcskin.model.McProfile;
import com.branwilliams.mcskin.model.McProfileData;
import com.branwilliams.mcskin.model.McProfileProperty;
import com.branwilliams.mcskin.model.McProfileTextures;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Base64;
import java.util.function.Consumer;

/**
 * https://wiki.vg/Mojang_API
 * @author Brandon
 * @since November 24, 2019
 */
public class McApi {

    private final Gson gson;

    public McApi() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public McProfileTextures getProfileTextures(McProfileData profileData) {
        McProfileProperty textureProperty = profileData.getProperty("textures");
        if (textureProperty != null) {
            String decodedProperty = new String(Base64.getDecoder().decode(textureProperty.getValue()));
            McProfileTextures textures = gson.fromJson(decodedProperty, McProfileTextures.class);
            return textures;
        }
        return null;
    }

    public void findProfile(String username, Consumer<McProfile> profileConsumer) {
        HttpUtils.get("https://api.mojang.com/users/profiles/minecraft/" + username,
                (response) -> {
            McProfile profile = gson.fromJson(response, McProfile.class);
            profileConsumer.accept(profile);
        });
    }

    public void getProfileData(McProfile profile, Consumer<McProfileData> profileDataConsumer) {
        HttpUtils.get("https://sessionserver.mojang.com/session/minecraft/profile/" + profile.getId(),
                (response) -> {
            McProfileData profileData = gson.fromJson(response, McProfileData.class);
            profileDataConsumer.accept(profileData);
        });
    }

}
