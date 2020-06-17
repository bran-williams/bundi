package com.branwilliams.terrain.builder;

import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.terrain.TerrainTile;
import com.branwilliams.terrain.generator.HeightGenerator;
import com.branwilliams.terrain.generator.TerrainVertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

import static com.branwilliams.bundi.engine.util.MeshUtils.calculateBitangent;
import static com.branwilliams.bundi.engine.util.MeshUtils.calculateTangent;

/**
 * Generates terrain meshes using a height generator. <br/>
 * Created by Brandon Williams on 12/28/2017.
 */
public class TerrainTileBuilder {

    private static final Logger log = LoggerFactory.getLogger(TerrainTileBuilder.class);


    public TerrainTile buildTerrainTile(HeightGenerator heightGenerator, Material material, float amplitude,
                                        float x, float z, int size, int vertexCountX,
                                        int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        TerrainTile terrainTile = new TerrainTile(new Transformation().position(z * size, 0, x * size), material);

        loadTerrainHeightmap(terrainTile, heights, size, vertexCountX, vertexCountZ);
        return terrainTile;
    }

    public void loadTerrainHeightmap(TerrainTile terrainTile, float[][] heights, int size, int vertexCountX, int vertexCountZ) {
        loadTerrainHeightmap(terrainTile, getHeightGenerator(heights), size, vertexCountX, vertexCountZ);
    }

    public void loadTerrainHeightmap(TerrainTile terrainTile, HeightGenerator heightGenerator, float amplitude, float x, float z, int size,
                                     int vertexCountX, int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        loadTerrainHeightmap(terrainTile, heights, size, vertexCountX, vertexCountZ);
    }

    /**
     * This function will generate the vertex information for a terrain tile from a height generator.
     *
     * @param terrainTile The {@link TerrainTile} this heightmap will be generated for.
     * @param heightGenerator The function used to query height values for each vertex.
     * @param scale The scale of this terrain tile. This essentially scales the terrain vertices.
     * @param vertexCountX The number of vertices in the X direction.
     * @param vertexCountZ The number of vertices in the Z direction.
     * */
    public void loadTerrainHeightmap(TerrainTile terrainTile, BiFunction<Integer, Integer, Float> heightGenerator, int scale, int vertexCountX, int vertexCountZ) {
        int vertexCount = vertexCountX * vertexCountZ;

        TerrainVertex[] terrainVertices = new TerrainVertex[vertexCount];

        int vertexPointer = 0;

        // Iterate over the the actual vertex count so that the height generator uses the actual x,z positions instead
        // of the ones of the lower lod.
        for (int i = 0; i < vertexCountX; i++) {
            for (int j = 0; j < vertexCountZ; j++) {

                Vector3f position = new Vector3f();
                position.x = (float) i / ((float) vertexCountX - 1) * scale;
                position.y = heightGenerator.apply(i, j);
                position.z = (float) j / ((float) vertexCountZ - 1) * scale;

                Vector3f normal = calculateNormal(i, j, heightGenerator);

                Vector2f uv = new Vector2f();
                uv.x =  (float) i / ((float) vertexCountX - 1);
                uv.y = (float) j / ((float) vertexCountZ - 1);

                terrainVertices[vertexPointer] = new TerrainVertex(position, normal, uv, new Vector3f(), new Vector3f());
                vertexPointer++;
            }
        }

        // Create the indices and tangent/bitangents for this mesh.
        for (int gx = 0; gx < vertexCountZ - 1; gx++) {
            for (int gz = 0; gz < vertexCountX - 1; gz++) {
                int topLeft = (gx * vertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * vertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;

                TerrainVertex topLeftVertex = terrainVertices[topLeft];
                TerrainVertex topRightVertex = terrainVertices[topRight];
                TerrainVertex bottomLeftVertex = terrainVertices[bottomLeft];
                TerrainVertex bottomRightVertex = terrainVertices[bottomRight];

                calculateTangentBitangent(topLeftVertex, topRightVertex, bottomLeftVertex);
                calculateTangentBitangent(topRightVertex, bottomRightVertex, bottomLeftVertex);
            }
        }

        log.info("Terrain heightmap created with " + vertexCount + " vertices.");
        terrainTile.setHeightmap(terrainVertices, vertexCountX, vertexCountZ);
    }

    private static void calculateTangentBitangent(TerrainVertex vec0, TerrainVertex vec1, TerrainVertex vec2) {
        // Create vectors to make this calculation a little easier to read.
        Vector3f v0 = vec0.getPosition();
        Vector3f v1 = vec1.getPosition();
        Vector3f v2 = vec2.getPosition();

        Vector2f uv0 = vec0.getUvs();
        Vector2f uv1 = vec1.getUvs();
        Vector2f uv2 = vec2.getUvs();

        Vector3f tangent = calculateTangent(v0, v1, v2, uv0, uv1, uv2);

        vec0.setTangent(tangent);
        vec1.setTangent(new Vector3f(tangent));
        vec2.setTangent(new Vector3f(tangent));

        Vector3f bitangent = calculateBitangent(v0, v1, v2, uv0, uv1, uv2);

        vec0.setBitangent(bitangent);
        vec1.setBitangent(new Vector3f(bitangent));
        vec2.setBitangent(new Vector3f(bitangent));
    }

    private static Vector3f calculateNormal(int x, int z, BiFunction<Integer, Integer, Float> heightGenerator) {
        float heightL = heightGenerator.apply(x-1, z);
        float heightR = heightGenerator.apply(x+1, z);
        float heightD = heightGenerator.apply(x, z-1);
        float heightU = heightGenerator.apply(x, z+1);
        return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
    }

    private static BiFunction<Integer, Integer, Float> getHeightGenerator(final float[][] heights) {
        return (x, z) -> {
            x = x < 0 ? 0 : x;
            x = x >= heights.length ? heights.length - 1 : x;
            z = z < 0 ? 0 : z;
            z = z >= heights.length ? heights.length - 1 : z;
            return heights[z][x];
        };
    }
}
