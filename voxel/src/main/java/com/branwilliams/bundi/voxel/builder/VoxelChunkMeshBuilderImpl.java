package com.branwilliams.bundi.voxel.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.branwilliams.bundi.engine.util.MeshUtils.*;
import static com.branwilliams.bundi.voxel.VoxelConstants.CUBE_SIZE;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelChunkMeshBuilderImpl implements VoxelChunkMeshBuilder {

    private VoxelRegistry voxelRegistry;

    private VoxelTexturePack voxelTexturePack;

    public VoxelChunkMeshBuilderImpl(VoxelRegistry voxelRegistry, VoxelTexturePack voxelTexturePack) {
        this.voxelRegistry = voxelRegistry;
        this.voxelTexturePack = voxelTexturePack;
    }

    /**
     *
     * */
    @Override
    public boolean rebuildChunkMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk, ChunkMesh mesh) {
        List<ChunkMeshVertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int x = 0; x < VoxelConstants.CHUNK_X_SIZE; x++) {
            for (int y = 0; y < VoxelConstants.CHUNK_Y_SIZE; y++) {
                for (int z = 0; z < VoxelConstants.CHUNK_Z_SIZE; z++) {
                    Voxel voxel = voxelChunk.getVoxelAtPosition(x, y, z);

                    if (!Voxel.isAir(voxel)) {
                        AABB voxelAABB = voxel.getBoundingBox(Mathf.floor(x * CUBE_SIZE),
                                Mathf.floor(y * CUBE_SIZE),
                                Mathf.floor(z * CUBE_SIZE));

                        float minX = voxelAABB.getMinX();
                        float maxX = voxelAABB.getMaxX();
                        float minY = voxelAABB.getMinY();
                        float maxY = voxelAABB.getMaxY();
                        float minZ = voxelAABB.getMinZ();
                        float maxZ = voxelAABB.getMaxZ();
                        createVoxelFaces(vertices, indices, voxelWorld, voxelChunk, x, y, z,
                                minX, minY, minZ, maxX, maxY, maxZ);
                    }
                }
            }
        }

        createTangents(vertices, indices);


        mesh.setMeshData(vertices, indices);

//        mesh.setVertexFormat(VertexFormat.POSITION_UV_NORMAL_TANGENT);
//        mesh.bind();
//        mesh.storeAttribute(0, toArray3f(vertices.stream().map((v) -> v.vertex).collect(Collectors.toList())), VertexElements.POSITION.getSize());
//        mesh.storeAttribute(1, toArray2f(vertices.stream().map((v) -> v.uv).collect(Collectors.toList())), VertexElements.UV.getSize());
//        mesh.storeAttribute(2, toArray3f(vertices.stream().map((v) -> v.normal).collect(Collectors.toList())), VertexElements.NORMAL.getSize());
//        mesh.storeAttribute(3, toArray3f(vertices.stream().map((v) -> v.tangent).collect(Collectors.toList())), VertexElements.TANGENT.getSize());
//        mesh.storeIndices(toArrayi(indices));
//        mesh.unbind();

        return vertices.size() > 0;
    }

    /**
     * Creates the tangents for each mesh vertex.
     * */
    private void createTangents(List<ChunkMeshVertex> vertices, List<Integer> indices) {
        for (int i = 0; i < indices.size(); i += 3) {
            ChunkMeshVertex v0 = vertices.get(indices.get(i));
            ChunkMeshVertex v1 = vertices.get(indices.get(i + 1));
            ChunkMeshVertex v2 = vertices.get(indices.get(i + 2));

            Vector3f edge1 = new Vector3f(v1.vertex).sub(v0.vertex);
            Vector3f edge2 = new Vector3f(v2.vertex).sub(v0.vertex);

            float deltaU1 = v1.uv.x - v0.uv.x;
            float deltaV1 = v1.uv.y - v0.uv.y;
            float deltaU2 = v2.uv.x - v0.uv.x;
            float deltaV2 = v2.uv.y - v0.uv.y;

            float f = 1F / (deltaU1 * deltaV2 - deltaU2 * deltaV1);

            Vector3f tangent = new Vector3f();
            tangent.x = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
            tangent.y = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
            tangent.z = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);
            tangent.normalize();

            v0.tangent.add(tangent);
            v1.tangent.add(tangent);
            v2.tangent.add(tangent);
        }

        for (ChunkMeshVertex vertex : vertices) {
            vertex.tangent.normalize();
        }
    }

    /**
     * Creates each visible face of each voxel.
     * */
    private void createVoxelFaces(List<ChunkMeshVertex> vertices, List<Integer> indices,
                                  VoxelWorld voxelWorld, VoxelChunk voxelChunk,
                                  int x, int y, int z,
                                  float minX, float minY, float minZ,
                                  float maxX, float maxY, float maxZ) {

        Voxel voxel = voxelChunk.getVoxelAtPosition(x, y, z);

        int index = vertices.size();

        for (VoxelFace face : VoxelFace.values()) {
            if (shouldRenderFace(voxelWorld, voxelChunk, voxel, x, y, z, face)) {
                ChunkMeshVertex v0 = new ChunkMeshVertex();
                ChunkMeshVertex v1 = new ChunkMeshVertex();
                ChunkMeshVertex v2 = new ChunkMeshVertex();
                ChunkMeshVertex v3 = new ChunkMeshVertex();

                VoxelFace.createPositions(face, v0, v1, v2, v3, minX, minY, minZ, maxX, maxY, maxZ);
                VoxelFace.createNormals(face, v0, v1, v2, v3);

                Vector4f textureCoordinates = voxelTexturePack.getTextureCoordinates(voxel, face);
                VoxelFace.createFaceUVs(textureCoordinates, v0, v1, v2, v3);

                vertices.add(v0);
                vertices.add(v1);
                vertices.add(v2);
                vertices.add(v3);

                indices.add(index);
                indices.add(index + 1);
                indices.add(index + 2);
                indices.add(index + 2);
                indices.add(index + 3);
                indices.add(index);
                index += 4;
            }
        }
    }

    /**
     * Determines if a provided voxel should render a provided face. This simply finds the adjacent voxel and invokes
     * the 'shouldRenderFace' function defined by each voxel.
     * */
    private boolean shouldRenderFace(VoxelWorld voxelWorld, VoxelChunk voxelChunk, Voxel voxel,
                                     int x, int y, int z, VoxelFace face) {
        Voxel adjacentVoxel;

        if (!voxelChunk.withinChunk(x, y, z, face)) {
            float realX = voxelChunk.chunkPos.getRealX() + x;
            float realY = y;
            float realZ = voxelChunk.chunkPos.getRealZ() + z;
            adjacentVoxel = voxelWorld.getChunks().getVoxelFacingPosition(realX, realY, realZ, face);
        } else {
            adjacentVoxel = voxelChunk.getVoxelFacingPosition(x, y, z, face);
        }

        return voxel.shouldRenderFace(adjacentVoxel, face);
    }

}
