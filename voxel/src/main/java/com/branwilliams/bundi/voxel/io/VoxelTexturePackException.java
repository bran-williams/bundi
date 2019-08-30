package com.branwilliams.bundi.voxel.io;

/**
 * @author Brandon
 * @since August 18, 2019
 */
public class VoxelTexturePackException extends Throwable {

    public VoxelTexturePackException(String message) {
        super(message);
    }

    public VoxelTexturePackException(String message, Throwable cause) {
        super(message, cause);
    }

    public VoxelTexturePackException(Throwable cause) {
        super(cause);
    }

    public VoxelTexturePackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
