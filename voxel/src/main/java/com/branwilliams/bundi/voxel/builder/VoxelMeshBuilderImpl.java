package com.branwilliams.bundi.voxel.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static com.branwilliams.bundi.engine.util.MeshUtils.*;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelMeshBuilderImpl implements VoxelMeshBuilder {

    private VoxelRegistry voxelRegistry;

    private VoxelTexturePack voxelTexturePack;

    public VoxelMeshBuilderImpl(VoxelRegistry voxelRegistry, VoxelTexturePack voxelTexturePack) {
        this.voxelRegistry = voxelRegistry;
        this.voxelTexturePack = voxelTexturePack;
    }

    @Override
    public Mesh rebuildVoxelMesh(Voxel voxel, VoxelFace[] faces, float minX, float maxX, float minY, float maxY,
                               float minZ, float maxZ, Mesh mesh) {

        VoxelProperties voxelProperties = voxelRegistry.getVoxelProperties(voxel.id);

        List<Float> vertices = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int index = 0;

        for (VoxelFace face : faces) {
            index = addVoxelFace(index, voxelProperties, face, vertices, uvs, indices, minX, minY, minZ, maxX, maxY,
                    maxZ);
        }

        mesh.setVertexFormat(VertexFormat.POSITION_UV);

        mesh.bind();
        mesh.storeAttribute(0, toArrayf(vertices), VertexElements.POSITION.getSize());
        mesh.storeAttribute(1, toArrayf(uvs), VertexElements.UV.getSize());
        mesh.storeIndices(toArrayi(indices));
        mesh.unbind();

        return mesh;
    }

    private int addVoxelFace(int index, VoxelProperties voxelProperties, VoxelFace face,
                             List<Float> vertices,
                             List<Float> uvs,
                             List<Integer> indices,
                             float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {

        vertices.addAll(VoxelFace.positions(face, minX, minY, minZ, maxX, maxY, maxZ));

        Vector4f textureCoordinates = voxelTexturePack.getTextureCoordinates(voxelProperties.getTexturePath(face));
        uvs.addAll(VoxelFace.createFaceUVs(textureCoordinates));

        indices.add(index);
        indices.add(index + 1);
        indices.add(index + 2);
        indices.add(index + 2);
        indices.add(index + 3);
        indices.add(index);
        index += 4;

        return index;
    }

}
