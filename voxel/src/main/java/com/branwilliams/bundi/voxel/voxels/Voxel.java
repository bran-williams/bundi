package com.branwilliams.bundi.voxel.voxels;

import com.branwilliams.bundi.voxel.math.AABB;
import org.joml.Vector3f;

import static com.branwilliams.bundi.voxel.VoxelConstants.CUBE_SIZE;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class Voxel {

    public final VoxelIdentifier id;

    private boolean opaque;

    private int light;

    public Voxel(VoxelIdentifier id) {
        this(id, true);
    }

    public Voxel(VoxelIdentifier id, boolean opaque) {
        this(id, opaque, 0);
    }

    public Voxel(VoxelIdentifier id, boolean opaque, int light) {
        this.id = id;
        this.opaque = opaque;
        this.light = light;
    }

    public AABB getBoundingBox(Vector3f position) {
        return new AABB(position.x, position.y, position.z, position.x + CUBE_SIZE,
                position.y + CUBE_SIZE,
                position. z + CUBE_SIZE);
    }

    public AABB getBoundingBox(int x, int y, int z) {
        return new AABB(x, y, z, x + CUBE_SIZE,
                y + CUBE_SIZE,
                z + CUBE_SIZE);
    }

    /**
     * @return True if this voxel should render the provided face, given the adjacent voxel.
     * */
    public boolean shouldRenderFace(Voxel adjacentVoxel, VoxelFace face) {
        return Voxel.isAir(adjacentVoxel) || !adjacentVoxel.isOpaque();
    }

    public boolean emitsLight() {
        return getLight() > 0;
    }

    /**
     *
     * */
    public int getLight() {
        return light;
    }

    /**
     * @return True if this voxel is considered translucent (i.e. see-through)
     * */
    public boolean isOpaque() {
        return opaque;
    }

    /**
     * @return True if this voxel represents air (unlikely)
     * */
    public boolean isAir() {
        return isAir(this);
    }

    /**
     * @return True if some given voxel represents air (unlikely)
     * */
    public static boolean isAir(Voxel voxel) {
        return voxel == null || voxel.id.equals(VoxelIdentifiers.AIR);
    }

    @Override
    public String toString() {
        return "Voxel{" +
                "id=" + id +
                ", opaque=" + opaque +
                '}';
    }
}
