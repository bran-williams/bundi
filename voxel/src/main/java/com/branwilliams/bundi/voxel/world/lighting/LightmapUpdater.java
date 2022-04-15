package com.branwilliams.bundi.voxel.world.lighting;

import com.branwilliams.bundi.voxel.util.LightUtils;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class LightmapUpdater {

    @FunctionalInterface
    interface LightFastFloodFillConsumer {
        void consume(VoxelWorld world, float x, float y, float z, int light);
    }

    private static final int MAX_ITERATIONS = 1_000_000;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Queue<LightAddition> lightAdditionQueue;

    private Queue<LightRemoval> lightRemovalQueue;

    private int iterations;

    public LightmapUpdater() {
        this.lightAdditionQueue = new LinkedList<>();
        this.lightRemovalQueue = new LinkedList<>();
    }

    public void propagateLightRemovals(VoxelWorld world) {
        this.iterations = 0;
        while (!lightRemovalQueue.isEmpty()) {
            LightRemoval lightRemoval = lightRemovalQueue.poll();
            VoxelChunk chunk = lightRemoval.getChunk();
            Vector3i pos = lightRemoval.getPos();
            int light = lightRemoval.getValue();

            float realX = chunk.chunkPos.getWorldX() + pos.x;
            float realZ = chunk.chunkPos.getWorldZ() + pos.z;
            propagateLightFastFloodFill(this::propagateLightRemoval, world, realX, pos.y, realZ, light);
        }
    }

    public void propagateLightAdditions(VoxelWorld world) {
        this.iterations = 0;
        while (!lightAdditionQueue.isEmpty()) {
            LightAddition lightAddition = lightAdditionQueue.poll();
            VoxelChunk chunk = lightAddition.getChunk();
            Vector3i pos = lightAddition.getPos();
            int light = chunk.getLight(pos.x, pos.y, pos.z);
            float realX = chunk.chunkPos.getWorldX() + pos.x;
            float realZ = chunk.chunkPos.getWorldZ() + pos.z;
            propagateLightFastFloodFill(this::propagateLightAddition, world, realX, pos.y, realZ, light);
        }
    }

    protected void propagateLightRemoval(VoxelWorld world, float x, float y, float z, int lightToRemove) {
        int neighborLight = world.getChunks().getLightAtPosition(x, y, z);

        if (LightUtils.hasLight(neighborLight)) {
            if (LightUtils.isAnyChannelBrighterThan(lightToRemove, neighborLight)) {
                int lightAfterRemoval = LightUtils.removeLight(lightToRemove, neighborLight);
                world.getChunks().setLightAtPosition(lightAfterRemoval, x, y, z);
                addLightRemovalUpdate(new LightRemoval(world.getChunks().getChunkAtPosition(x, z), toKernelPos(x, y, z),
                        LightUtils.getRemovedChannels(lightToRemove, neighborLight)));
                neighborLight = lightAfterRemoval;
            }

            Voxel voxel = world.getChunks().getVoxelAtPosition(x, y, z);
            if (voxel.emitsLight()) {
                world.getChunks().setLightAtPosition(voxel.getLight(), x, y, z);
            }

            if (voxel.emitsLight() || (LightUtils.hasLight(neighborLight)
                    && LightUtils.isAnyChannelBrighterOrEqualTo(neighborLight, lightToRemove))) {
                addLightAdditionUpdate(new LightAddition(world.getChunks().getChunkAtPosition(x, z),
                        toKernelPos(x, y, z)));
            }
        }

        checkIterations();
    }

    protected void propagateLightAddition(VoxelWorld world, float x, float y, float z, int light) {
        Voxel voxel = world.getChunks().getVoxelAtPosition(x, y, z);
        int neighborLight = world.getChunks().getLightAtPosition(x, y, z);

        if (!voxel.isOpaque() && LightUtils.isAnyChannelBrighterThan(light,
                LightUtils.addRGBToLight(neighborLight, 2, 2, 2))) {
            int newLight = LightUtils.maxLight(LightUtils.decrementByAmount(light, 1), neighborLight);
            world.getChunks().setLightAtPosition(newLight, x, y, z);
            addLightAdditionUpdate(new LightAddition(world.getChunks().getChunkAtPosition(x, z), toKernelPos(x, y, z)));
        }

        checkIterations();
    }

    /**
     * A lil thang to prevent us from crashing! BFS can blow up sometimes.
     * */
    private void checkIterations() {
        iterations++;
        if (iterations > MAX_ITERATIONS) {
            log.info("Light update had over {} iterations. Too many updates!", MAX_ITERATIONS);
            lightRemovalQueue.clear();
            lightAdditionQueue.clear();
        }
    }

    protected void propagateLightFastFloodFill(LightFastFloodFillConsumer consumer, VoxelWorld world, float x, float y,
                                               float z, int light) {
        consumer.consume(world, x - 1, y, z, light);
        consumer.consume(world, x + 1, y, z, light);
        consumer.consume(world, x, y - 1, z, light);
        consumer.consume(world, x, y + 1, z, light);
        consumer.consume(world, x, y, z - 1, light);
        consumer.consume(world, x, y, z + 1, light);
    }

    protected Vector3i toKernelPos(float x, float y, float z) {
        return new Vector3i(ChunkPos.toKernelX((int) x), (int) y, ChunkPos.toKernelZ((int) z));
    }

    public void addLightAdditionUpdate(VoxelWorld world, float x, float y, float z) {
        addLightAdditionUpdate(world.getChunks().getChunkAtPosition(x, z), x, y, z);
    }

    public void addLightAdditionUpdate(VoxelChunk chunk, float x, float y, float z) {
        addLightAdditionUpdate(new LightAddition(chunk, toKernelPos(x, y, z)));
    }

    public void addLightAdditionUpdate(LightAddition lightAddition) {
        lightAdditionQueue.add(lightAddition);
    }

    public void addLightRemovalUpdate(VoxelWorld world, float x, float y, float z, int light) {
        addLightRemovalUpdate(world.getChunks().getChunkAtPosition(x, z), x, y, z, light);
    }

    public void addLightRemovalUpdate(VoxelChunk chunk, float x, float y, float z, int light) {
        addLightRemovalUpdate(new LightRemoval(chunk, toKernelPos(x, y, z), light));
    }

    public void addLightRemovalUpdate(LightRemoval lightRemoval) {
        lightRemovalQueue.add(lightRemoval);
    }

}
