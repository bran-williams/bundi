package com.branwilliams.cubes.builder;

import com.branwilliams.bundi.engine.shader.dynamic.VertexElement;
import com.branwilliams.cubes.DebugOriginMesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArray3f;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArray4f;

/**
 * @author Brandon
 * @since February 04, 2020
 */
public class DebugOriginMeshBuilderImpl implements DebugOriginMeshBuilder {

    public DebugOriginMesh buildMesh(Vector3f origin, float extents) {
        return buildMesh(origin,
                new Vector3f(extents, 0, 0), // x
                new Vector3f(0, extents, 0), // y
                new Vector3f(0, 0, extents)); // z
    }

    @Override
    public DebugOriginMesh buildMesh(Vector3f origin, Vector3f x, Vector3f y, Vector3f z) {
        DebugOriginMesh debugOriginMesh = new DebugOriginMesh();
        debugOriginMesh.init();
        return rebuildMesh(debugOriginMesh, origin, x, y, z);
    }

    @Override
    public DebugOriginMesh rebuildMesh(DebugOriginMesh mesh, Vector3f origin, Vector3f x, Vector3f y, Vector3f z) {
        mesh.setOrigin(origin);
        Vector3f[] vertices = {
                origin,
                origin.add(x, new Vector3f()),
                origin,
                origin.add(y, new Vector3f()),
                origin,
                origin.add(z, new Vector3f())
        };

        Vector4f[] colors = {
                mesh.getxAxisColor(),
                mesh.getxAxisColor(),

                mesh.getyAxisColor(),
                mesh.getyAxisColor(),

                mesh.getzAxisColor(),
                mesh.getzAxisColor()
        };

        mesh.getMesh().bind();
        mesh.getMesh().storeAttribute(0, toArray3f(vertices), VertexElement.POSITION.size);
        mesh.getMesh().storeAttribute(1, toArray4f(colors), VertexElement.COLOR.size);
        mesh.getMesh().setVertexCount(vertices.length);
        mesh.getMesh().unbind();

        return mesh;
    }
}
