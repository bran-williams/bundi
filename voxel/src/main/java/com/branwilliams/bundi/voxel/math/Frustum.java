package com.branwilliams.bundi.voxel.math;

import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Projection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Represents the viewing frustum given a projection matrix and view matrix. This can be used to check if shapes are
 * outside the frustum for culling purposes.
 *
 * @author Brandon
 * @since August 12, 2019
 */
public class Frustum {

    private static final int NUM_PLANES = 6;

    private final Matrix4f projectionViewMatrix;

    private final Vector4f[] frustumPlanes;

    public Frustum() {
        this.projectionViewMatrix = new Matrix4f();

        frustumPlanes = new Vector4f[NUM_PLANES];
        for (int i = 0; i < NUM_PLANES; i++) {
            frustumPlanes[i] = new Vector4f();
        }
    }

    public void update(Projection projection, Camera camera) {
        this.update(projection.toProjectionMatrix(), camera.toViewMatrix());
    }

    public void update(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        // Calculate projection view matrix
        projectionViewMatrix.set(projectionMatrix);
        projectionViewMatrix.mul(viewMatrix);
        // Get frustum planes
        for (int i = 0; i < NUM_PLANES; i++) {
            projectionViewMatrix.frustumPlane(i, frustumPlanes[i]);
        }
    }

    public boolean insideFrustum(Vector3f position, float boundingRadius) {
        return insideFrustum(position.x, position.y, position.z, boundingRadius);
    }

    /**
     * Performs a visibility check given the position and radius within this frustum.
     * */
    public boolean insideFrustum(float x, float y, float z, float boundingRadius) {
        for (int i = 0; i < NUM_PLANES; i++) {
            Vector4f plane = frustumPlanes[i];

            if (plane.x * x + plane.y * y + plane.z * z + plane.w <= -boundingRadius) {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs a visibility check of a given AABB within this frustum.
     * */
    public boolean insideFrustumAABB(AABB aabb) {
        return insideFrustumAABB(aabb.getMinX(), aabb.getMaxX(), aabb.getMinY(), aabb.getMaxY(), aabb.getMinZ(), aabb.getMaxZ());
    }

    /**
     * Performs a visibility check of a given AABB within this frustum.
     * */
    public boolean insideFrustumAABB(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        Vector3f[] corners = {
                new Vector3f(minX, minY, minZ),
                new Vector3f(minX, minY, maxZ),
                new Vector3f(minX, maxY, minZ),
                new Vector3f(minX, maxY, maxZ),

                new Vector3f(maxX, minY, minZ),
                new Vector3f(maxX, minY, maxZ),
                new Vector3f(maxX, maxY, minZ),
                new Vector3f(maxX, maxY, maxZ),
        };

        for (int i = 0; i < NUM_PLANES; i++) {
            Vector4f plane = frustumPlanes[i];

            int inCount = 8;
            for (Vector3f corner : corners) {
                float side = plane.dot(corner.x, corner.y, corner.z, 1F);
                if (side < 0F)
                    inCount--;
            }

            if (inCount <= 0)
                return false;
        }

        return true;
    }
}
