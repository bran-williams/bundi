package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.terrain.TerrainTile;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

/**
 * Generates terrain meshes using a height generator. <br/>
 * Created by Brandon Williams on 12/28/2017.
 */
public class TerrainGenerator {

    private static final Logger log = LoggerFactory.getLogger(TerrainGenerator.class);

    private boolean normals;

    private boolean tangentBitangent;

    public TerrainGenerator() {
        this(true, true);
    }

    public TerrainGenerator(boolean normals, boolean tangentBitangent) {
        this.normals = normals;
        this.tangentBitangent = tangentBitangent;
    }

    public Mesh generateTerrain(HeightGenerator heightGenerator, float amplitude, float x, float z, int size,
                                int vertexCountX, int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        return generateTerrain(heights, size, vertexCountX, vertexCountZ);
    }

    public Mesh generateTerrain(float[][] heights, int size, int vertexCountX, int vertexCountZ) {
        return loadTerrainMesh(getHeightGenerator(heights), size, vertexCountX, vertexCountZ);
    }

    public TerrainTile generateTerrainTile(HeightGenerator heightGenerator, Material material, float amplitude,
                                           float x, float z, int size, int vertexCountX,
                                           int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        Mesh terrainMesh = generateTerrain(heights, size, vertexCountX, vertexCountZ);
        return new TerrainTile(heights, new Transformation().position(z * size, 0, x * size),
                terrainMesh, material);
    }


    private BiFunction<Integer, Integer, Float> getHeightGenerator(final float[][] heights) {
        return (x, z) -> {
            x = x < 0 ? 0 : x;
            x = x >= heights.length ? heights.length - 1 : x;
            z = z < 0 ? 0 : z;
            z = z >= heights.length ? heights.length - 1 : z;
            return heights[z][x];
        };
    }

    /**
     * Generates a flat terrain mesh.
     * */
    public Mesh generateFlatTerrain(float amplitude, int size, int vertexCountX, int vertexCountZ) {
        Mesh mesh = loadTerrainMesh((x, z) -> amplitude, size, vertexCountX, vertexCountZ);
        return mesh;
    }

    /**
     * Thank you to ThinMatrix for this lovely terrain loading code. This may only be temporary.
     * */
    public Mesh loadTerrainMesh(BiFunction<Integer, Integer, Float> heightGenerator, int size, int vertexCountX, int vertexCountZ) {
        int increment = 1;
        int actualVertexCountX = (vertexCountX - 1) / (increment) + 1;
        int actualVertexCountZ = (vertexCountZ - 1) / (increment) + 1;

        int count = actualVertexCountX * actualVertexCountZ;
        log.info("Terrain mesh created with " + count + " vertices.");

        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        float[] tangents = new float[count * 3];
        float[] bitangents = new float[count * 3];

        int[] indices = new int[6 * (actualVertexCountX - 1) * (actualVertexCountZ - 1)];

        int vertexPointer = 0;

        // Iterate over the the actual vertex count so that the height generator uses the actual x,z positions instead
        // of the ones of the lower lod.
        for (int i = 0; i < vertexCountX; i+=increment) {
            for (int j = 0; j < vertexCountZ; j+=increment) {
                vertices[vertexPointer * 3] = (float) i / ((float) vertexCountX - 1) * size;
                vertices[vertexPointer * 3 + 1] = heightGenerator.apply(i, j);
                vertices[vertexPointer * 3 + 2] = (float) j / ((float) vertexCountZ - 1) * size;

                if (this.normals) {
                    Vector3f normal = calculateNormal(i, j, heightGenerator);
                    normals[vertexPointer * 3] = normal.x;
                    normals[vertexPointer * 3 + 1] = normal.y;
                    normals[vertexPointer * 3 + 2] = normal.z;
                }
                textureCoords[vertexPointer * 2] = (float) i / ((float) vertexCountX - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) j / ((float) vertexCountZ - 1);
                vertexPointer++;
            }
        }

        // Create the indices and tangent/bitangents for this mesh.
        int pointer = 0;
        for (int gx = 0; gx < actualVertexCountZ - 1; gx++){
            for (int gz = 0; gz < actualVertexCountX - 1; gz++){
                int topLeft = (gx * actualVertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * actualVertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;
                if (this.normals && this.tangentBitangent) {
                    MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topLeft, bottomLeft, topRight);
                    MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topRight, bottomLeft, bottomRight);
                }
                indices[pointer++] = topLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomRight;
                indices[pointer++] = bottomLeft;

                // original order.
//                indices[pointer++] = topLeft;
//                indices[pointer++] = bottomLeft;
//                indices[pointer++] = topRight;
//                indices[pointer++] = topRight;
//                indices[pointer++] = bottomLeft;
//                indices[pointer++] = bottomRight;
            }
        }

        Mesh mesh = new Mesh();
        mesh.bind();
        mesh.storeAttribute(0, vertices, 3);
        mesh.storeAttribute(1, textureCoords, 2);
        if (this.normals) {
            mesh.storeAttribute(2, normals, 3);
        }
        if (this.normals && this.tangentBitangent) {
            mesh.storeAttribute(3, tangents, 3);
            mesh.storeAttribute(4, bitangents, 3);
        }
        mesh.storeIndices(indices);
        mesh.unbind();
        return mesh;
    }

    private static Vector3f calculateNormal(int x, int z, BiFunction<Integer, Integer, Float> heightGenerator) {
        float heightL = heightGenerator.apply(x-1, z);
        float heightR = heightGenerator.apply(x+1, z);
        float heightD = heightGenerator.apply(x, z-1);
        float heightU = heightGenerator.apply(x, z+1);
        return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
    }
}
