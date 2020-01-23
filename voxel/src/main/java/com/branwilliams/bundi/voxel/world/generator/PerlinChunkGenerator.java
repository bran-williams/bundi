package com.branwilliams.bundi.voxel.world.generator;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.util.noise.PerlinNoise;
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
public class PerlinChunkGenerator implements VoxelChunkGenerator {

    /** This is the threshold for a noise value to become a voxel. */
    private static final float VOXEL_GENERATION_THRESHOLD = 0.135F;

    private final PerlinNoise perlinNoise;

    private final Random random;

    public PerlinChunkGenerator() {
        this(new PerlinNoise());
    }

    public PerlinChunkGenerator(long seed) {
        this(new PerlinNoise(seed));
    }

    public PerlinChunkGenerator(PerlinNoise perlinNoise) {
        this.perlinNoise = perlinNoise;
        this.random = new Random(perlinNoise.getRandom().hashCode());
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
        Voxel[][][] kernel = new Voxel[VoxelConstants.CHUNK_X_SIZE][VoxelConstants.CHUNK_Y_SIZE][VoxelConstants.CHUNK_Z_SIZE];

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

                    // noise value at nx, ny, nz
                    float e = (float) perlinNoise.noise(nx, ny, nz);

                    // Gradient in the y-axis
                    float gradient = 1F - ((float) j / (float) VoxelConstants.CHUNK_Y_SIZE);

                    // Change the -1 ~ 1 noise value to 0 ~ 1.
                    float clampedNoiseValue = (Mathf.clamp(e, 1F) + 1F) * 0.5F;

                    Voxel voxel = determineVoxel(kernel, i, j, k, clampedNoiseValue, gradient);

                    kernel[i][j][k] = voxel;
                }
            }
        }

        return new VoxelChunk(new ChunkPos(chunkX, chunkZ), kernel);
    }

    private Voxel determineVoxel(Voxel[][][] kernel, int i, int j, int k, float noiseValue, float yGradient) {
        Voxel voxel = Voxels.air;
        if (yGradient > 0.55F) {

            voxel = random.nextFloat() > 0.5F ? Voxels.bricks : Voxels.stone;

        } else if (yGradient * noiseValue > VOXEL_GENERATION_THRESHOLD) {

            boolean isTopBlock = j >= VoxelConstants.CHUNK_Y_SIZE - 1;
            if (yGradient > 0.45F) {
                voxel = Voxels.sand;
            } else
            // top block or a block with nothing above it becomes grass.
            if (isTopBlock || Voxel.isAir(kernel[i][j + 1][k])) {
                voxel = Voxels.grass;
            } else {
                voxel = Voxels.dirt;
            }

        }

        // bottom layer becomes bedrock.
        if (j == 0) {
            voxel = Voxels.bedrock;
        }

        return voxel;
    }

}
