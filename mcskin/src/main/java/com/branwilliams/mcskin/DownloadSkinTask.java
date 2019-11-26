package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.util.HttpUtil;
import com.branwilliams.mcskin.model.McProfile;
import com.branwilliams.mcskin.model.McProfileData;
import com.branwilliams.mcskin.model.McProfileTextures;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class DownloadSkinTask implements Runnable {

    private static final long REQUEST_DELAY = 1100L;

    private final String username;

    private final McApi mcApi;

    private final Path tempDirectory;

    private final Consumer<String> skinConsumer;

    public DownloadSkinTask(String username, McApi mcApi, Path tempDirectory, Consumer<String> skinConsumer) {
        this.username = username;
        this.mcApi = mcApi;
        this.tempDirectory = tempDirectory;
        this.skinConsumer = skinConsumer;
    }

    @Override
    public void run() {
        mcApi.findProfile(username, this::loadProfileData);
    }

    private void loadProfileData(McProfile profile) {
        mcApi.getProfileData(profile, this::downloadTexture);
        sleep();
    }

    private void downloadTexture(McProfileData profileData) {
        McProfileTextures textures = mcApi.getProfileTextures(profileData);
        sleep();

        File file = new File(tempDirectory.toFile(), profileData.getId() + ".png");
        if (file.exists()) {
            skinConsumer.accept(file.getAbsolutePath());
        } else {
            String downloadUrl = (String) textures.getTextures().get("SKIN").get("url");
            String fileURL = HttpUtil.downloadImage(tempDirectory, profileData.getId(), downloadUrl, true);
            sleep();
            skinConsumer.accept(fileURL);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(REQUEST_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
