package com.branwilliams.terrain.generator;

import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.terrain.TerrainTile;
import org.joml.Vector2f;
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




    public TerrainTile generateTerrainTile(HeightGenerator heightGenerator, Material material, float amplitude,
                                           float x, float z, int size, int vertexCountX,
                                           int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        TerrainTile terrainTile = new TerrainTile(new Transformation().position(z * size, 0, x * size), material);

        Mesh terrainMesh = generateTerrainMesh(terrainTile, heights, size, vertexCountX, vertexCountZ);
        terrainTile.setMesh(terrainMesh);

        return terrainTile;
    }

    public Mesh generateTerrainMesh(TerrainTile terrainTile, float[][] heights, int size, int vertexCountX, int vertexCountZ) {
        return loadTerrainMesh(terrainTile, getHeightGenerator(heights), size, vertexCountX, vertexCountZ);
    }

    public Mesh generateTerrainMesh(TerrainTile terrainTile, HeightGenerator heightGenerator, float amplitude, float x, float z, int size,
                                    int vertexCountX, int vertexCountZ) {
        float[][] heights = heightGenerator.generateHeight(x, z, vertexCountX, vertexCountZ, amplitude);
        return generateTerrainMesh(terrainTile, heights, size, vertexCountX, vertexCountZ);
    }

//    /**
//     * Generates a flat terrain mesh.
//     * */
//    public Mesh generateFlatTerrain(float amplitude, int size, int vertexCountX, int vertexCountZ) {
//        Mesh mesh = loadTerrainMesh((x, z) -> amplitude, size, vertexCountX, vertexCountZ);
//        return mesh;
//    }

    /**
     * Thank you to ThinMatrix for this lovely terrain loading code. This may only be temporary.
     * */
    public Mesh loadTerrainMesh(TerrainTile terrainTile, BiFunction<Integer, Integer, Float> heightGenerator, int size, int vertexCountX, int vertexCountZ) {
        int increment = 1;
        int actualVertexCountX = (vertexCountX - 1) / (increment) + 1;
        int actualVertexCountZ = (vertexCountZ - 1) / (increment) + 1;

        int count = actualVertexCountX * actualVertexCountZ;

        TerrainVertex[] terrainVertices = new TerrainVertex[count];
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

                Vector3f position = new Vector3f();
                position.x = (float) i / ((float) vertexCountX - 1) * size;
                position.y = heightGenerator.apply(i, j);
                position.z = (float) j / ((float) vertexCountZ - 1) * size;
                vertices[vertexPointer * 3] = position.x;
                vertices[vertexPointer * 3 + 1] = position.y;
                vertices[vertexPointer * 3 + 2] = position.z;

                Vector3f normal = new Vector3f();
                if (this.normals) {
                    normal = calculateNormal(i, j, heightGenerator);
                    normals[vertexPointer * 3] = normal.x;
                    normals[vertexPointer * 3 + 1] = normal.y;
                    normals[vertexPointer * 3 + 2] = normal.z;
                }

                Vector2f uv = new Vector2f();
                uv.x =  (float) i / ((float) vertexCountX - 1);
                uv.y = (float) j / ((float) vertexCountZ - 1);
                textureCoords[vertexPointer * 2] = uv.x;
                textureCoords[vertexPointer * 2 + 1] = uv.y;

                terrainVertices[vertexPointer] = new TerrainVertex(position, normal, uv, new Vector3f(), new Vector3f());
                vertexPointer++;
            }
        }

        // Create the indices and tangent/bitangents for this mesh.
        int pointer = 0;
        for (int gx = 0; gx < actualVertexCountZ - 1; gx++) {
            for (int gz = 0; gz < actualVertexCountX - 1; gz++) {
                int topLeft = (gx * actualVertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * actualVertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;
                if (this.normals && this.tangentBitangent) {
//                    TerrainVertex topLeftVertex = terrainVertices[topLeft];
//                    TerrainVertex topRightVertex = terrainVertices[topRight];
//                    TerrainVertex bottomLeftVertex = terrainVertices[bottomLeft];
//                    TerrainVertex bottomRightVertex = terrainVertices[bottomRight];
//                    Vector3f tangent = MeshUtils.calculateTangent(topLeftVertex.position, bottomLeftVertex.position, topRightVertex.position,
//                            topLeftVertex.uvs, bottomLeftVertex.uvs, topRightVertex.uvs);
                    MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topLeft, topRight, bottomLeft);
                    MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topRight, bottomRight, bottomLeft);
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

        terrainTile.setHeightmap(terrainVertices, vertexCountX, vertexCountZ);
        return mesh;
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

    private static Vector3f calculateNormal(int x, int z, BiFunction<Integer, Integer, Float> heightGenerator) {
        float heightL = heightGenerator.apply(x-1, z);
        float heightR = heightGenerator.apply(x+1, z);
        float heightD = heightGenerator.apply(x, z-1);
        float heightU = heightGenerator.apply(x, z+1);
        return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
    }

    public static class TerrainVertex {

        private Vector3f position;

        private Vector3f normal;

        private Vector2f uvs;

        private Vector3f tangent;

        private Vector3f bitangent;

        public TerrainVertex(Vector3f position, Vector3f normal, Vector2f uvs, Vector3f tangent, Vector3f bitangent) {
            this.position = position;
            this.normal = normal;
            this.uvs = uvs;
            this.tangent = tangent;
            this.bitangent = bitangent;
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }

        public Vector3f getNormal() {
            return normal;
        }

        public void setNormal(Vector3f normal) {
            this.normal = normal;
        }

        public Vector2f getUvs() {
            return uvs;
        }

        public void setUvs(Vector2f uvs) {
            this.uvs = uvs;
        }

        public Vector3f getTangent() {
            return tangent;
        }

        public void setTangent(Vector3f tangent) {
            this.tangent = tangent;
        }

        public Vector3f getBitangent() {
            return bitangent;
        }

        public void setBitangent(Vector3f bitangent) {
            this.bitangent = bitangent;
        }
    }
}
