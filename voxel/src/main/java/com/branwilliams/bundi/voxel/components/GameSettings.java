package com.branwilliams.bundi.voxel.components;

import com.branwilliams.bundi.engine.util.Mathf;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class GameSettings {

    private static final int MIN_BLOCK_LIGHT = 1;

    private static final int MAX_BLOCK_LIGHT = 15;

    private int chunkRenderDistance;

    private int minBlockLight = MIN_BLOCK_LIGHT;

    private float musicVolume;

    private boolean playMusic;

    private boolean vsync;

    private boolean fullscreen;

    public int getChunkRenderDistance() {
        return chunkRenderDistance;
    }

    public void setChunkRenderDistance(int chunkRenderDistance) {
        this.chunkRenderDistance = chunkRenderDistance;
    }

    public int getMinBlockLight() {
        return minBlockLight;
    }

    public void setMinBlockLight(int minBlockLight) {
        this.minBlockLight = Mathf.clamp(minBlockLight, MIN_BLOCK_LIGHT, MAX_BLOCK_LIGHT);
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public boolean isPlayMusic() {
        return playMusic;
    }

    public void setPlayMusic(boolean playMusic) {
        this.playMusic = playMusic;
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
}
