package com.branwilliams.bundi.voxel.components;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class GameSettings {

    private int chunkRenderDistance;

    private float musicVolume;

    private boolean playMusic;

    private boolean vsync;

    public GameSettings(int chunkRenderDistance) {
        this.chunkRenderDistance = chunkRenderDistance;
    }

    public int getChunkRenderDistance() {
        return chunkRenderDistance;
    }

    public void setChunkRenderDistance(int chunkRenderDistance) {
        this.chunkRenderDistance = chunkRenderDistance;
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
}
