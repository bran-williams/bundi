package com.branwilliams.bundi.engine.util;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Brandon Williams on 9/6/2018.
 */
public enum MeshUtils {
    INSTANCE;

    /**
     * Calculates the tangents and bitangents for a given set of vertices and texture coordinates. Assuming that the
     * mesh does not have indices.
     * TODO this and Gram-Schmidt orthogonalize and calculate handedness
     * */
    public static void calculateTangentBitangents(int[] indices, float[] vertices, float[] textureCoordinates, float[] tangents,
                                                  float[] bitangents) {
        System.out.println(vertices.length / 3 + ", " + textureCoordinates.length / 2);
        if (vertices.length / 3 != textureCoordinates.length / 2) {
            throw new IllegalArgumentException("Number of vertices must equal the number of texture coordinates!");
        }

        // This operation is done with 3 vertices.
        for (int i = 0; i < indices.length; i += 3) {
            int index0 = indices[i];
            int index1 = indices[i + 1];
            int index2 = indices[i + 2];

            Vector3f v0 = new Vector3f(vertices[index0], vertices[index0 + 1], vertices[index0 + 2]);
            Vector3f v1 = new Vector3f(vertices[index1], vertices[index1 + 1], vertices[index1 + 2]);
            Vector3f v2 = new Vector3f(vertices[index2], vertices[index2 + 1], vertices[index2 + 2]);

            Vector2f uv0 = new Vector2f(textureCoordinates[index0], textureCoordinates[index0 + 1]);
            Vector2f uv1 = new Vector2f(textureCoordinates[index1], textureCoordinates[index1 + 1]);
            Vector2f uv2 = new Vector2f(textureCoordinates[index2], textureCoordinates[index2 + 1]);

            Vector3f tangent = calculateTangent(v0, v1, v2, uv0, uv1, uv2);

            tangents[index0] = tangent.x;
            tangents[index0 + 1] = tangent.y;
            tangents[index0 + 2] = tangent.z;

            tangents[index1] = tangent.x;
            tangents[index1 + 1] = tangent.y;
            tangents[index1 + 2] = tangent.z;

            tangents[index2] = tangent.x;
            tangents[index2 + 1] = tangent.y;
            tangents[index2 + 2] = tangent.z;

            Vector3f bitangent = calculateBitangent(v0, v1, v2, uv0, uv1, uv2);

            bitangents[index0] = bitangent.x;
            bitangents[index0 + 1] = bitangent.y;
            bitangents[index0 + 2] = bitangent.z;

            bitangents[index1] = bitangent.x;
            bitangents[index1 + 1] = bitangent.y;
            bitangents[index1 + 2] = bitangent.z;

            bitangents[index2] = bitangent.x;
            bitangents[index2 + 1] = bitangent.y;
            bitangents[index2 + 2] = bitangent.z;
        }
    }

    /**
     * Calculates the tangents and bitangents for a given set of vertices and texture coordinates. Assuming that the
     * mesh does not have indices.
     * */
    public static void calculateTangentBitangents(float[] vertices, float[] textureCoordinates, float[] tangents,
                                                  float[] bitangents) {
        int totalVertices = vertices.length / 3;

        if (totalVertices != textureCoordinates.length / 2) {
            throw new IllegalArgumentException("Number of vertices must equal the number of texture coordinates!");
        }

        // This operation is done with 3 vertices.
        for (int i = 0; i < totalVertices; i += 3) {

            int i0 = i * 3;
            int i1 = (i + 1) * 3;
            int i2 = (i + 2) * 3;

            Vector3f v0 = new Vector3f(vertices[i0], vertices[i0 + 1], vertices[i0 + 2]);
            Vector3f v1 = new Vector3f(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]);
            Vector3f v2 = new Vector3f(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]);

            Vector2f uv0 = new Vector2f(textureCoordinates[i * 2], textureCoordinates[i * 2 + 1]);
            Vector2f uv1 = new Vector2f(textureCoordinates[(i + 1) * 2], textureCoordinates[(i + 1) * 2 + 1]);
            Vector2f uv2 = new Vector2f(textureCoordinates[(i + 2) * 2], textureCoordinates[(i + 2) * 2 + 1]);

            Vector3f tangent = calculateTangent(v0, v1, v2, uv0, uv1, uv2);

            tangents[i0]     = tangent.x;
            tangents[i0 + 1] = tangent.y;
            tangents[i0 + 2] = tangent.z;

            tangents[i1]     = tangent.x;
            tangents[i1 + 1] = tangent.y;
            tangents[i1 + 2] = tangent.z;

            tangents[i2]     = tangent.x;
            tangents[i2 + 1] = tangent.y;
            tangents[i2 + 2] = tangent.z;

            Vector3f bitangent = calculateBitangent(v0, v1, v2, uv0, uv1, uv2);

            bitangents[i0] = bitangent.x;
            bitangents[i0] = bitangent.y;
            bitangents[i0] = bitangent.z;

            bitangents[i1]     = bitangent.x;
            bitangents[i1 + 1] = bitangent.y;
            bitangents[i1 + 2] = bitangent.z;

            bitangents[i2]     = bitangent.x;
            bitangents[i2 + 1] = bitangent.y;
            bitangents[i2 + 2] = bitangent.z;
        }
    }

    public static void calculateTangentBitangent(float[] vertices, float[] textureCoords, float[] tangents,
                                                  float[] bitangents, int v0Index, int v1Index, int v2Index) {
        // Create vectors to make this calculation a little easier to read.
        Vector3f v0 = new Vector3f(vertices[v0Index * 3], vertices[v0Index * 3 + 1], vertices[v0Index * 3 + 2]);
        Vector3f v1 = new Vector3f(vertices[v1Index * 3], vertices[v1Index * 3 + 1], vertices[v1Index * 3 + 2]);
        Vector3f v2 = new Vector3f(vertices[v2Index * 3], vertices[v2Index * 3 + 1], vertices[v2Index * 3 + 2]);

        Vector2f uv0 = new Vector2f(textureCoords[v0Index * 2], textureCoords[v0Index * 2 + 1]);
        Vector2f uv1 = new Vector2f(textureCoords[v1Index * 2], textureCoords[v1Index * 2 + 1]);
        Vector2f uv2 = new Vector2f(textureCoords[v2Index * 2], textureCoords[v2Index * 2 + 1]);

        Vector3f tangent = calculateTangent(v0, v1, v2, uv0, uv1, uv2);

        tangents[v0Index * 3] = tangent.x;
        tangents[v0Index * 3 + 1] = tangent.y;
        tangents[v0Index * 3 + 2] = tangent.z;

        tangents[v1Index * 3] = tangent.x;
        tangents[v1Index * 3 + 1] = tangent.y;
        tangents[v1Index * 3 + 2] = tangent.z;

        tangents[v2Index * 3] = tangent.x;
        tangents[v2Index * 3 + 1] = tangent.y;
        tangents[v2Index * 3 + 2] = tangent.z;

        Vector3f bitangent = calculateBitangent(v0, v1, v2, uv0, uv1, uv2);

        bitangents[v0Index * 3] = bitangent.x;
        bitangents[v0Index * 3 + 1] = bitangent.y;
        bitangents[v0Index * 3 + 2] = bitangent.z;

        bitangents[v1Index * 3] = bitangent.x;
        bitangents[v1Index * 3 + 1] = bitangent.y;
        bitangents[v1Index * 3 + 2] = bitangent.z;

        bitangents[v2Index * 3] = bitangent.x;
        bitangents[v2Index * 3 + 1] = bitangent.y;
        bitangents[v2Index * 3 + 2] = bitangent.z;
    }

    /**
     * Calculates the tangent for a given set of vectors and uvs.
     * */
    public static Vector3f calculateTangent(Vector3f v0, Vector3f v1, Vector3f v2, Vector2f uv0,
                                                               Vector2f uv1, Vector2f uv2) {
        Vector3f deltaPos1 = new Vector3f(v1).sub(v0);
        Vector3f deltaPos2 = new Vector3f(v2).sub(v0);

        // UV delta
        Vector2f deltaUV1 = new Vector2f(uv1).sub(uv0);
        Vector2f deltaUV2 = new Vector2f(uv2).sub(uv0);

        float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);

        Vector3f tangent = new Vector3f();
        tangent.x = r * deltaUV2.y * deltaPos1.x - deltaUV1.y * deltaPos2.x;
        tangent.y = r * deltaUV2.y * deltaPos1.y - deltaUV1.y * deltaPos2.y;
        tangent.z = r * deltaUV2.y * deltaPos1.z - deltaUV1.y * deltaPos2.z;
        tangent.normalize();

        return tangent;
    }

    /**
     * Calculates the bitangent for a given set of vectors and uvs.
     * */
    public static Vector3f calculateBitangent(Vector3f v0, Vector3f v1, Vector3f v2, Vector2f uv0,
                                     Vector2f uv1, Vector2f uv2) {
        Vector3f deltaPos1 = new Vector3f(v1).sub(v0);
        Vector3f deltaPos2 = new Vector3f(v2).sub(v0);

        Vector2f deltaUV1 = new Vector2f(uv1).sub(uv0);
        Vector2f deltaUV2 = new Vector2f(uv2).sub(uv0);

        float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);

        Vector3f bitangent = new Vector3f();
        bitangent.x = r * (-deltaUV2.x * deltaPos1.x + deltaUV1.x * deltaPos2.x);
        bitangent.y = r * (-deltaUV2.x * deltaPos1.y + deltaUV1.x * deltaPos2.y);
        bitangent.z = r * (-deltaUV2.x * deltaPos1.z + deltaUV1.x * deltaPos2.z);
        bitangent.normalize();

        return bitangent;
    }

    /**
     * Formula for the normal of a triangle from <a
     * href="https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal">Khronos - Calculating a Surface
     * Normal</a>
     *
     * @return The normal for a triangle made by the three points.
     *
     * */
    public static Vector3f calculateNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
//        Vector3f p1p2 = p2.sub(p1, new Vector3f());
//        Vector3f p3p2 = p3.sub(p1, new Vector3f());
//        Vector3f normal = p1p2.cross(p3p2, new Vector3f());
//        return normal;
        Vector3f normal = new Vector3f();
        Vector3f u = new Vector3f(p2).sub(p1);
        Vector3f v = new Vector3f(p3).sub(p1);

        normal.x = (u.y * v.z) - (u.z * v.y);
        normal.y = (u.z * v.x) - (u.x * v.z);
        normal.z = (u.x * v.y) - (u.y * v.x);
        return normal;
    }

    /**
     * Converts an array of {@link org.joml.Vector4f} into an array of floats. e.g.
     * <pre>
     * Vector4f[] myVectors = { new Vector4f(1, 2, 3, 4), new Vector4f(5, 6, 7, 8) };
     * float[] myValues = toArray4f(myVectors); // = { 1, 2, 3, 4, 5, 6, 7, 8 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, z0, w0, x1 y1, z1, w1, ... xN, yN,
     * zN, wN.
     * */
    public static float[] toArray4f(Vector4f... vectors) {
        float[] array = new float[vectors.length * 4];
        for (int i = 0; i < vectors.length; i++) {
            Vector4f vector = vectors[i];
            array[i * 4] = vector.x;
            array[i * 4 + 1] = vector.y;
            array[i * 4 + 2] = vector.z;
            array[i * 4 + 3] = vector.w;
        }
        return array;
    }

    /**
     * Converts a list of {@link org.joml.Vector4f} into an array of floats. e.g.
     * <pre>
     * List<Vector4f> myVectors = new ArrayList();
     * myVectors.add(new Vector4f(1, 2, 3, 4));
     * myVectors.add(new Vector4f(5, 6, 7, 8));
     * float[] myValues = toArray4f(myVectors); // = { 1, 2, 3, 4, 5, 6, 7, 8 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, z0, w0, x1 y1, z1, w1, ... xN, yN,
     * zN, wN.
     * */
    public static float[] toArray4f(List<Vector4f> vectors) {
        float[] array = new float[vectors.size() * 4];
        for (int i = 0; i < vectors.size(); i++) {
            Vector4f vector = vectors.get(i);
            array[i * 4] = vector.x;
            array[i * 4 + 1] = vector.y;
            array[i * 4 + 2] = vector.z;
            array[i * 4 + 3] = vector.w;

        }
        return array;
    }

    /**
     * Converts an array of {@link org.joml.Vector3f} into an array of floats. e.g.
     * <pre>
     * Vector3f[] myVectors = { new Vector3f(1, 2, 3), new Vector3f(4, 5, 6) };
     * float[] myValues = toArray3f(myVectors); // = { 1, 2, 3, 4, 5, 6 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, z0, x1 y1, z1, ... xN, yN, zN.
     * */
    public static float[] toArray3f(Vector3f... vectors) {
        float[] array = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            Vector3f vector = vectors[i];
            array[i * 3] = vector.x;
            array[i * 3 + 1] = vector.y;
            array[i * 3 + 2] = vector.z;
        }
        return array;
    }

    /**
     * Converts a list of {@link org.joml.Vector3f} into an array of floats. e.g.
     * <pre>
     * List<Vector3f> myVectors = new ArrayList();
     * myVectors.add(new Vector3f(1, 2, 3));
     * myVectors.add(new Vector3f(4, 5, 6));
     * float[] myValues = toArray3f(myVectors); // = { 1, 2, 3, 4, 5, 6 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, z0, x1 y1, z1, ... xN, yN, zN.
     * */
    public static float[] toArray3f(List<Vector3f> vectors) {
        float[] array = new float[vectors.size() * 3];
        for (int i = 0; i < vectors.size(); i++) {
            Vector3f vector = vectors.get(i);
            array[i * 3] = vector.x;
            array[i * 3 + 1] = vector.y;
            array[i * 3 + 2] = vector.z;
        }
        return array;
    }

    /**
     * Converts an array of {@link org.joml.Vector2f} into an array of floats. e.g.
     * <pre>
     * Vector2f[] myVectors = { new Vector2f(1, 2), new Vector2f(3, 4) };
     * float[] myValues = toArray2f(myVectors); // = { 1, 2, 3, 4 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, x1 y1, ... xN, yN.
     * */
    public static float[] toArray2f(Vector2f... vectors) {
        float[] array = new float[vectors.length * 2];
        for (int i = 0; i < vectors.length; i++) {
            Vector2f vector = vectors[i];
            array[i * 2] = vector.x;
            array[i * 2 + 1] = vector.y;
        }
        return array;
    }

    /**
     * Converts a list of {@link org.joml.Vector2f} into an array of floats. e.g.
     * <pre>
     * List<Vector2f> myVectors = new ArrayList();
     * myVectors.add(new Vector2f(1, 2));
     * myVectors.add(new Vector2f(3, 4));
     * float[] myValues = toArray2f(myVectors); // = { 1, 2, 3, 4 }
     * </pre>
     *
     * @param vectors The vectors to convert to an array.
     * @return An array of floats where for N vectors the values are ordered: x0, y0, x1 y1, ... xN, yN.
     * */
    public static float[] toArray2f(List<Vector2f> vectors) {
        float[] array = new float[vectors.size() * 2];
        for (int i = 0; i < vectors.size(); i++) {
            Vector2f vector = vectors.get(i);
            array[i * 2] = vector.x;
            array[i * 2 + 1] = vector.y;
        }
        return array;
    }

    public static int[] toArrayi(List<Integer> list) {
        int[] res = list.stream().mapToInt((Integer i) -> i).toArray();

        return res;
    }

    public static float[] toArrayf(List<Float> list) {
        float[] res = new float[list.size()];

        int i = 0;
        for (float f : list) {
            res[i++] = f;
        }

        return res;
    }

    public static float[] arrayOfSize(int size, float value) {
        float[] array = new float[size];
        Arrays.fill(array, value);
        return array;
    }
}
