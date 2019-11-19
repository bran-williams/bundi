package com.branwilliams.terrain.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.MeshUtils;
import com.branwilliams.terrain.TerrainTile;
import com.branwilliams.terrain.generator.TerrainVertex;
import org.joml.Vector2f;
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
        int vertexCountX = terrainTile.getWidth();
        int vertexCountZ = terrainTile.getDepth();
        int vertexCount = vertexCountX * vertexCountZ;

        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] textureCoords = new float[vertexCount * 2];
        float[] tangents = new float[vertexCount * 3];
        float[] bitangents = new float[vertexCount * 3];

        int[] indices = new int[6 * (vertexCountX - 1) * (vertexCountZ - 1)];

        int vertexPointer = 0;
        for (TerrainVertex vertex : terrainTile.getHeightmap()) {
            vertices[vertexPointer * 3] = vertex.getPosition().x;
            vertices[vertexPointer * 3 + 1] = vertex.getPosition().y;
            vertices[vertexPointer * 3 + 2] = vertex.getPosition().z;

            normals[vertexPointer * 3] = vertex.getNormal().x;
            normals[vertexPointer * 3 + 1] = vertex.getNormal().y;
            normals[vertexPointer * 3 + 2] = vertex.getNormal().z;

            textureCoords[vertexPointer * 2] = vertex.getUvs().x;
            textureCoords[vertexPointer * 2 + 1] = vertex.getUvs().y;

            tangents[vertexPointer * 3] = vertex.getTangent().x;
            tangents[vertexPointer * 3 + 1] = vertex.getTangent().y;
            tangents[vertexPointer * 3 + 2] = vertex.getTangent().z;

            bitangents[vertexPointer * 3] = vertex.getBitangent().x;
            bitangents[vertexPointer * 3 + 1] = vertex.getBitangent().y;
            bitangents[vertexPointer * 3 + 2] = vertex.getBitangent().z;
            vertexPointer++;
        }

        // Create the indices and tangent/bitangents for this mesh.
        int idxPointer = 0;
        for (int gx = 0; gx < vertexCountZ - 1; gx++){
            for (int gz = 0; gz < vertexCountX - 1; gz++){
                int topLeft = (gx * vertexCountX) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * vertexCountZ) + gz;
                int bottomRight = bottomLeft + 1;

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
        terrainTile.setMesh(mesh);
        return mesh;
    }

}
