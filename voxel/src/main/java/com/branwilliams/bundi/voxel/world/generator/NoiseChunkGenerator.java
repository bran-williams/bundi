package com.branwilliams.bundi.voxel.world.generator;

import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.noise.Noise;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.voxels.Voxels;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

import java.util.Random;

/**
 * Created by Brandon Williams on 11/15/2018.
 */
public class NoiseChunkGenerator implements VoxelChunkGenerator {

    /** This is the threshold for a noise value to become a voxel. */
    private static final float TOPSOIL_GENERATION_THRESHOLD = 0.135F;
    private static final float CAVE_GENERATION_THRESHOLD = 0.7F;

    private final Noise caveNoise;

    private final Noise topsoilNoise;

    private final Random random;

    public NoiseChunkGenerator(Noise caveNoise, Noise topsoilNoise) {
        this.caveNoise = caveNoise;
        this.topsoilNoise = topsoilNoise;
        this.random = new Random(caveNoise.hashCode());
    }

    /**
     * Generates a kernel for a single chunk.
     * */
    @Override
    public VoxelChunk generateChunk(VoxelRegistry voxelRegistry, int chunkX, int chunkZ) {
        float noiseX = chunkX;
        float noiseY = 0;
        float noiseZ = chunkZ;

        float noiseScale = 1F / (Mathf.average(VoxelConstants.CHUNK_X_SIZE, VoxelConstants.CHUNK_Z_SIZE));
        Grid3i<Voxel> kernel = new Grid3i<>(Voxel[]::new, VoxelConstants.CHUNK_X_SIZE, VoxelConstants.CHUNK_Y_SIZE,
                VoxelConstants.CHUNK_Z_SIZE);

        // Ensure the edges of each tile matches.
        noiseX = noiseX - noiseX * (1F / VoxelConstants.CHUNK_X_SIZE);
        noiseY = noiseY - noiseY * (1F / VoxelConstants.CHUNK_Y_SIZE);
        noiseZ = noiseZ - noiseZ * (1F / VoxelConstants.CHUNK_Z_SIZE);

        for (int i = 0; i < VoxelConstants.CHUNK_X_SIZE; i++) {
            float nx = (noiseX * VoxelConstants.CHUNK_X_SIZE + i + 1) * noiseScale;

            for (int j = VoxelConstants.CHUNK_Y_SIZE - 1; j >= 0; j--) {
                float ny = (noiseY * VoxelConstants.CHUNK_Y_SIZE + j + 1) * noiseScale;

                for (int k = 0; k < VoxelConstants.CHUNK_Z_SIZE; k++) {
                    float nz = (noiseZ * VoxelConstants.CHUNK_Z_SIZE + k + 1) * noiseScale;

                    Voxel voxel = determineVoxel(kernel, nx, ny, nz, i, j, k);

                    kernel.setValue(voxel, i, j, k);
                }
            }
        }

        return new VoxelChunk(new ChunkPos(chunkX, chunkZ), kernel);
    }

    private float clampNoise(float noise) {
        // Change the -1 ~ 1 noise value to 0 ~ 1.
        return (Mathf.clamp(noise, 1F) + 1F) * 0.5F;
    }


    private Voxel determineVoxel(Grid3i<Voxel> kernel, float nx, float ny, float nz, int kernelX, int kernelY,
                                 int kernelZ) {
        // Gradient in the y-axis
        float gradient = 1F - ((float) kernelY / (float) VoxelConstants.CHUNK_Y_SIZE);

        Voxel voxel = Voxels.air;
        if (kernelY <= 60) {
            voxel = generateCaves(kernel, nx, ny, nz, kernelX, kernelY, kernelZ);
        } else if (gradient * clampNoise((float) topsoilNoise.noise(nx, ny, nz)) > TOPSOIL_GENERATION_THRESHOLD) {
            voxel = generateTopsoil(kernel, nx, ny, nz, kernelX, kernelY, kernelZ);
        }

        // bottom layer becomes bedrock.
        if (kernelY == 0) {
            voxel = Voxels.bedrock;
        }

        return voxel;
    }

    private Voxel generateCaves(Grid3i<Voxel> kernel, float nx, float ny, float nz, int kernelX, int kernelY,
                                  int kernelZ) {
        return clampNoise((float) caveNoise.noise(nx, ny, nz)) > CAVE_GENERATION_THRESHOLD ? Voxels.air : Voxels.stone;
    }

    private Voxel generateTopsoil(Grid3i<Voxel> kernel, float nx, float ny, float nz, int kernelX, int kernelY,
                                  int kernelZ) {
        // top block or a block with nothing above it becomes grass.
        if (kernelY >= VoxelConstants.CHUNK_Y_SIZE - 1 || Voxel.isAir(kernel.getValue(kernelX, kernelY + 1,
                kernelZ))) {
            return Voxels.grass;
        } else {
            return Voxels.dirt;
        }
    }

}
