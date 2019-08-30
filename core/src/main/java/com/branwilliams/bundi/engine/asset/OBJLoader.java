package com.branwilliams.bundi.engine.asset;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OBJLoader {

    public Mesh load(File directory, String location) {
        String text = IOUtils.readFile(new File(directory, location), null);
        // Text cannot be null.
        if (text != null) {
            // The normals and texture coordinates will be sorted once this loader reaches a face. The faces
            // indicate the ordering of these elements.
            List<Vector3f> normals = new ArrayList<>();
            List<Vector2f> textureCoordinates = new ArrayList<>();
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector3f> tangents = new ArrayList<>();
            List<Vector3f> bitangents = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();
            List<Face> faces = new ArrayList<>();

            boolean hasTextureCoordinates = true;

            // A total size is kept track of in order to create a bounding box for this mesh.
            float size = 0F;

            Scanner scanner = new Scanner(text);
            String line;

            // Loads the vertices, normals, and texture coordinates. Once it reaches the faces, this loop breaks and the
            // final arrays are initialized.
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] params = line.split("\\s+");

                // Skip the first index as it is used to identify the data type.
                if (line.startsWith("vt")) {
                    textureCoordinates.add(new Vector2f(Float.parseFloat(params[1]), Float.parseFloat(params[2])));
                } else if (line.startsWith("vn")) {
                    normals.add(new Vector3f(Float.parseFloat(params[1]), Float.parseFloat(params[2]), Float.parseFloat(params[3])));
                } else if (line.startsWith("v")) {
                    Float x = Float.parseFloat(params[1]);
                    Float y = Float.parseFloat(params[2]);
                    Float z = Float.parseFloat(params[3]);
                    size = Math.max(Math.abs(x), size);
                    size = Math.max(Math.abs(y), size);
                    size = Math.max(Math.abs(z), size);

                    vertices.add(new Vector3f(x, y, z));
                    tangents.add(new Vector3f(0F, 0F, 0F));
                    bitangents.add(new Vector3f(0F, 0F, 0F));
                } else if (line.startsWith("f")) {
                    faces.add(new Face(line.substring(2)));
                    // The double forward slash indicates that the faces only have vertices and normals.
                    if (line.contains("//"))
                        hasTextureCoordinates = false;
                }
            }
            scanner.close();

            // These arrays are populated during the parsing of each face.
            float[] orderedNormals = new float[vertices.size() * 3];
            float[] orderedTextureCoordinates = null;

            if (hasTextureCoordinates)
                orderedTextureCoordinates = new float[vertices.size() * 2];

            // This parses the faces and organizes the normals and texture coordinates accordingly. It also calculates
            // the tangents for each vertex.
            for (Face face : faces) {
                face.load(vertices, indices, tangents, bitangents, normals, textureCoordinates, orderedNormals, orderedTextureCoordinates);
            }

            Mesh mesh = new Mesh();
            mesh.bind();

            float[] verticesArray = toArray(vertices);

            mesh.storeAttribute(0, verticesArray, 3);
            if (orderedTextureCoordinates != null)
                mesh.storeAttribute(1, orderedTextureCoordinates, 2);
            mesh.storeAttribute(2, orderedNormals, 3);
            mesh.storeAttribute(3, toArray(tangents), 3);
            mesh.storeAttribute(4, toArray(bitangents), 3);

            mesh.storeIndices(indices.stream().mapToInt((integer) -> integer).toArray());
            mesh.unbind();

            System.out.format("Mesh loaded from %s with %s vertices and size %s.\n", location, verticesArray.length / 3, size);
            if (faces.size() > 0) {
                Face face = faces.get(0);
                System.out.println("Normals: " + face.hasNormals());
                System.out.println("Texture coordinates: " + face.hasTextureCoordintes());
                System.out.println("Tangents and bitangents: " + face.hasTangentBitangents());
            }
            return mesh;
        } else
            throw new NullPointerException("Unable to read file!");
    }

    /**
     * Converts a given list of vectors to an array of floats.
     * */
    private float[] toArray(List<Vector3f> vectors) {
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
     * Represents a face from an obj file.
     * */
    private class Face {

        private final List<FaceInfo> faceLines = new ArrayList<>();

        private boolean hasTextureCoordinates = false;
        private boolean hasNormals = false;
        private boolean hasTangentBitangents = false;

        private Face(String line) {
            for (String vertexData : line.split("\\s+")) {
                // In the event that the provided file has extra spacing...
                if (vertexData.isEmpty())
                    continue;
                try {
                    // The double forward slash indicates that the faces only have vertices and normals.
                    if (line.contains("//")) {
                        String[] splitData = vertexData.split("//");
                        // Minus one since arrays start at zero and objs do not.
                        int vertexIndex = Integer.parseInt(splitData[0]) - 1;
                        int normalIndex = Integer.parseInt(splitData[1]) - 1;

                        faceLines.add(new FaceInfo(vertexIndex, normalIndex));
                    } else {
                        String[] splitData = vertexData.split("/");
                        // Minus one since arrays start at zero and objs do not.
                        int vertexIndex = Integer.parseInt(splitData[0]) - 1;
                        int textureCoordinateIndex = splitData.length >= 2 ? Integer.parseInt(splitData[1]) - 1 : -1;
                        int normalIndex = splitData.length >= 3 ? Integer.parseInt(splitData[2]) - 1 : -1;

                        if (textureCoordinateIndex != -1) {
                            faceLines.add(new FaceInfo(vertexIndex, normalIndex, textureCoordinateIndex));
                        } else {
                            faceLines.add(new FaceInfo(vertexIndex, normalIndex));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error reading face data '" + vertexData + "' on line: '" + line + "', Error:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        /**
         * Loads the values that this face specifies.
         * 1. Adds indices into the list of indices
         * 2. Puts the normals and texture coordinates into their appropriate order
         * 3. Creates tangents for normal mapping
         * */
        private void load(List<Vector3f> vertices, List<Integer> indices, List<Vector3f> tangents,
                          List<Vector3f> bitangents, List<Vector3f> normals, List<Vector2f> textureCoordinates,
                             float[] orderedNormals, float[] orderedTextureCoordinates) {

            for (FaceInfo faceInfo : faceLines) {
                indices.add(faceInfo.vertexIndex);
                if (faceInfo.hasNormalIndex()) {
                    Vector3f normal = normals.get(faceInfo.normalIndex);
                    orderedNormals[faceInfo.vertexIndex * 3] = normal.x;
                    orderedNormals[faceInfo.vertexIndex * 3 + 1] = normal.y;
                    orderedNormals[faceInfo.vertexIndex * 3 + 2] = normal.z;
                    hasNormals = true;
                }

                if (faceInfo.hasTextureCoordinateIndex()) {
                    Vector2f textureCoordinate = textureCoordinates.get(faceInfo.textureCoordinateIndex);
                    orderedTextureCoordinates[faceInfo.vertexIndex * 2] = textureCoordinate.x;
                    orderedTextureCoordinates[faceInfo.vertexIndex * 2 + 1] = textureCoordinate.y;
                    hasTextureCoordinates = true;
                }
            }

            // Calculates the tangent and bitangent for this face.
            if (hasTextureCoordinates) {
                Vector3f v0 = vertices.get(faceLines.get(0).vertexIndex);
                Vector3f v1 = vertices.get(faceLines.get(1).vertexIndex);
                Vector3f v2 = vertices.get(faceLines.get(2).vertexIndex);

                Vector2f uv0 = textureCoordinates.get(faceLines.get(0).textureCoordinateIndex);
                Vector2f uv1 = textureCoordinates.get(faceLines.get(1).textureCoordinateIndex);
                Vector2f uv2 = textureCoordinates.get(faceLines.get(2).textureCoordinateIndex);

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

                tangents.set(faceLines.get(0).vertexIndex, tangent);
                tangents.set(faceLines.get(1).vertexIndex, tangent);
                tangents.set(faceLines.get(2).vertexIndex, tangent);

                Vector3f bitangent = new Vector3f();
                bitangent.x = r * (-deltaUV2.x * deltaPos1.x + deltaUV1.x * deltaPos2.x);
                bitangent.y = r * (-deltaUV2.x * deltaPos1.y + deltaUV1.x * deltaPos2.y);
                bitangent.z = r * (-deltaUV2.x * deltaPos1.z + deltaUV1.x * deltaPos2.z);
                bitangent.normalize();

                bitangents.set(faceLines.get(0).vertexIndex, bitangent);
                bitangents.set(faceLines.get(1).vertexIndex, bitangent);
                bitangents.set(faceLines.get(2).vertexIndex, bitangent);
                hasTangentBitangents = true;
            }
        }

        private boolean hasNormals() {
            return hasNormals;
        }

        private boolean hasTextureCoordintes() {
            return hasTextureCoordinates;
        }

        private boolean hasTangentBitangents() {
            return hasTangentBitangents;
        }

        /**
         * Holds the data that is specified on a face line within an obj file.
         * */
        private class FaceInfo {
            Integer vertexIndex;
            Integer normalIndex;
            Integer textureCoordinateIndex;

            FaceInfo(Integer vertexIndex, Integer normalIndex) {
                this(vertexIndex, normalIndex, -1);
            }

            FaceInfo(Integer vertexIndex, Integer normalIndex, Integer textureCoordinateIndex) {
                this.vertexIndex = vertexIndex;
                this.normalIndex = normalIndex;
                this.textureCoordinateIndex = textureCoordinateIndex;
            }

            /**
             * @return True if this line contains normals.
             * */
            boolean hasNormalIndex() {
                return normalIndex != -1;
            }

            /**
             * @return True if this line contains texture coordinates.
             * */
            boolean hasTextureCoordinateIndex() {
                return textureCoordinateIndex != -1;
            }
        }
    }
}
