package com.branwilliams.terrain.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.terrain.TerrainTile;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brandon
 * @since September 13, 2019
 */
public class TerrainMeshBuilder {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Mesh buildTerrainMesh(TerrainTile terrainTile) {
        return rebuildTerrainMesh(new Mesh(), terrainTile);
    }

    public Mesh rebuildTerrainMesh(Mesh mesh, TerrainTile terrainTile) {
        float[][] heightmap = terrainTile.getHeightmap();
        int vertexCountX = heightmap.length;
        int vertexCountZ = heightmap[0].length;
        int vertexCount = vertexCountX * vertexCountZ;


        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] textureCoords = new float[vertexCount * 2];
        float[] tangents = new float[vertexCount * 3];
        float[] bitangents = new float[vertexCount * 3];

        int[] indices = new int[6 * (vertexCountX - 1) * (vertexCountZ - 1)];

        int vertexPointer = 0;
        for (int i = 0; i < vertexCountX; i++) {
            for (int j = 0; j < vertexCountZ; j++) {
                vertices[vertexPointer * 3] = (float) i / ((float) vertexCountX - 1) * terrainTile.getSize();
                vertices[vertexPointer * 3 + 1] = heightmap[i][j];
                vertices[vertexPointer * 3 + 2] = (float) j / ((float) vertexCountZ - 1) * terrainTile.getSize();

                Vector3f normal = calculateNormal(i, j, heightmap);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                textureCoords[vertexPointer * 2] = (float) i / ((float) vertexCountX - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) j / ((float) vertexCountZ - 1);
                vertexPointer++;
            }
        }

        // Create the indices and tangent/bitangents for this mesh.
        int idxPointer = 0;
        for (int gx = 0; gx < vertexCountZ - 1; gx++){
            for (int gz = 0; gz < vertexCountX - 1; gz++){
                int topLeft = (gx * vertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * vertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;
                MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topLeft, bottomLeft, topRight);
                MeshUtils.calculateTangentBitangent(vertices, textureCoords, tangents, bitangents, topRight, bottomLeft, bottomRight);

                indices[idxPointer++] = topLeft;
                indices[idxPointer++] = topRight;
                indices[idxPointer++] = bottomLeft;
                indices[idxPointer++] = topRight;
                indices[idxPointer++] = bottomRight;
                indices[idxPointer++] = bottomLeft;
            }
        }

        mesh.bind();
        mesh.storeAttribute(0, vertices, 3);
        mesh.storeAttribute(1, textureCoords, 2);
        mesh.storeAttribute(2, normals, 3);
        mesh.storeAttribute(3, tangents, 3);
        mesh.storeAttribute(4, bitangents, 3);
        mesh.storeIndices(indices);
        mesh.unbind();

        log.info("Terrain mesh created with " + vertexCount + " vertices.");

        return mesh;
    }

    private static Vector3f calculateNormal(int x, int z, float[][] heights) {
        float heightL = getHeight(x - 1, z, heights);
        float heightR = getHeight(x + 1, z, heights);
        float heightD = getHeight(x, z - 1, heights);
        float heightU = getHeight(x, z + 1, heights);
        return new Vector3f(heightL - heightR, 2F, heightD - heightU).normalize();
    }

    private static float getHeight(int x, int z, float[][] heights) {
       x = x < 0 ? 0 : x;
       x = x >= heights.length ? heights.length - 1 : x;
       z = z < 0 ? 0 : z;
       z = z >= heights.length ? heights.length - 1 : z;

       return heights[x][z];
    }

}
