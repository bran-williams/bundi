package com.branwilliams.terrain.generator;

/**
 * Created by Brandon Williams on 10/29/2018.
 */
public interface HeightGenerator {

    /**
     * Creates the heights of a terrain mesh for the given position and size.
     *
     * @return An array of the heights for a terrain mesh.
     * @param x The x position of the mesh.
     * @param z The z position of the mesh.
     * @param vertexCountX The number of vertices of the mesh in the x axis.
     * @param vertexCountZ The number of vertices of the mesh in the z axis.
     * @param amplitude The amplitude of the mesh.
     * */
    float[][] generateHeight(float x, float z, int vertexCountX, int vertexCountZ, float amplitude);

}
