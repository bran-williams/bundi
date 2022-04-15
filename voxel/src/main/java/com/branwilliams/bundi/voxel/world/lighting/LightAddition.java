package com.branwilliams.bundi.voxel.world.lighting;

import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector3i;

public class LightAddition {

    private Vector3i pos;
    private VoxelChunk chunk;

    public LightAddition(VoxelChunk chunk, Vector3i pos) {
        this.chunk = chunk;
        this.pos = pos;
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
}
