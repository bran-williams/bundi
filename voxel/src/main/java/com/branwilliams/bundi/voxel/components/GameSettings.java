package com.branwilliams.bundi.voxel.components;

import com.branwilliams.bundi.engine.util.Mathf;

import static com.branwilliams.bundi.voxel.VoxelConstants.*;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class GameSettings {

    private int chunkRenderDistance;

    private int minBlockLight = DEFAULT_LIGHT;

    private float musicVolume;

    private boolean playMusic;

    private boolean vsync;

    private boolean fullscreen;

    private float ambientOcclusionPower = 1F;

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
        this.minBlockLight = Mathf.clamp(minBlockLight, MIN_LIGHT, MAX_LIGHT);
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

    public float getAmbientOcclusionPower() {
        return ambientOcclusionPower;
    }

    public void setAmbientOcclusionPower(float ambientOcclusionPower) {
        this.ambientOcclusionPower = Mathf.clamp(ambientOcclusionPower, MIN_AO, MAX_AO);
    }
}
