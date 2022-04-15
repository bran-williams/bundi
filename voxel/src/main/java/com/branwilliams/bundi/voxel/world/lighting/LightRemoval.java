package com.branwilliams.bundi.voxel.world.lighting;

import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector3i;

public class LightRemoval {

    private Vector3i pos;
    private VoxelChunk chunk;
    private int value;

    public LightRemoval(VoxelChunk chunk, Vector3i pos, int value) {
        this.chunk = chunk;
        this.pos = pos;
        this.value = value;
    }

    public VoxelChunk getChunk() {
        return chunk;
    }

    public void setChunk(VoxelChunk chunk) {
        this.chunk = chunk;
    }

    public Vector3i getPos() {
        return pos;
    }

    public void setPos(Vector3i pos) {
        this.pos = pos;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
