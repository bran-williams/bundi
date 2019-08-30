package com.branwilliams.bundi.voxel.components;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class GameSettings {

    private int chunkRenderDistance;

    public GameSettings(int chunkRenderDistance) {
        this.chunkRenderDistance = chunkRenderDistance;
    }

    public int getChunkRenderDistance() {
        return chunkRenderDistance;
    }

    public void setChunkRenderDistance(int chunkRenderDistance) {
        this.chunkRenderDistance = chunkRenderDistance;
    }
}
