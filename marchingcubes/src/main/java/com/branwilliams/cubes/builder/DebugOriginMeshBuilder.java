package com.branwilliams.cubes.builder;

import com.branwilliams.cubes.DebugOriginMesh;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since February 04, 2020
 */
public interface DebugOriginMeshBuilder {

    DebugOriginMesh buildMesh(Vector3f origin, Vector3f x, Vector3f y, Vector3f z);

    DebugOriginMesh rebuildMesh(DebugOriginMesh mesh, Vector3f origin, Vector3f x, Vector3f y, Vector3f z);

}
