package com.branwilliams.bundi.engine.model;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.material.MaterialElement;
import com.branwilliams.bundi.engine.material.MaterialElementType;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.InputStream;
import java.util.Map;

import static com.branwilliams.bundi.engine.util.MeshUtils.toArrayf;
import static com.branwilliams.bundi.engine.util.MeshUtils.toArrayi;
import static org.lwjgl.assimp.Assimp.*;

/**
 * Static model loading implemented using assimp. This borrows heavily from the lwjgl game engine book.
 * Created by Brandon Williams on 10/30/2018.
 */
public class ModelLoader {

    private static final Vector4f DEFAULT_AMBIENT = new Vector4f(1);

    private static final Vector4f DEFAULT_DIFFUSE = new Vector4f(1);

    private static final Vector4f DEFAULT_SPECULAR = new Vector4f(1);

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Path assetDirectory;

    private final TextureLoader textureLoader;

    public ModelLoader(EngineContext context, TextureLoader textureLoader) {
        this(context.getAssetDirectory(), textureLoader);
    }


    public ModelLoader(Path assetDirectory, TextureLoader textureLoader) {
        this.assetDirectory = assetDirectory;
        this.textureLoader = textureLoader;
    }

    public Model load(String location, String textureLocation, VertexFormat vertexFormat) throws Exception {
        location = assetDirectory.resolve(location).toString();

        AIScene scene = aiImportFile(location, aiProcess_CalcTangentSpace |
                aiProcess_Triangulate |
                aiProcess_GenSmoothNormals |
                aiProcess_SplitLargeMeshes |
                aiProcess_ConvertToLeftHanded |
                aiProcess_SortByPType |
                aiProcess_PreTransformVertices); //aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);

        if (scene == null) {
            throw new IOException(String.format("Unable to load model from location %s!", location));
        }
        log.info(String.format("Creating model from location %s", location));

        int	numMaterials = scene.mNumMaterials();
        PointerBuffer aiMaterials =	scene.mMaterials();
        log.debug("Materials #: " + numMaterials);

        Material[] materials = new Material[numMaterials];

        for	(int i = 0;	i <	numMaterials; i++) {
            AIMaterial aiMaterial =	AIMaterial.create(aiMaterials.get(i));
            log.debug("Creating material " + i);
            processMaterial(scene, aiMaterial, (materials[i] = new Material()), textureLocation);
        }

        Map<Material, List<Mesh>> modelData = new HashMap<>();

        int	numMeshes =	scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        log.debug("Meshes #: " + numMaterials);

        for	(int i = 0;	i <	numMeshes; i++)	{
            AIMesh aiMesh =	AIMesh.create(aiMeshes.get(i));
            Mesh mesh =	processMesh(aiMesh,	vertexFormat);

            int materialIdx = aiMesh.mMaterialIndex();
            modelData.computeIfAbsent(materials[materialIdx], (k) -> new ArrayList<>()).add(mesh);
        }
        aiReleaseImport(scene);

        return new Model(modelData);
    }

    private void processMaterial(AIScene scene, AIMaterial aiMaterial, Material material, String textureLocation) throws IOException {
        AIColor4D color = AIColor4D.create();
        AIString path = AIString.calloc();
        MaterialFormat materialFormat = new MaterialFormat();

        int textureIndex = 0;

        // Create diffuse texture
        aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE,0, path, (IntBuffer) null, null, null, null, null,null);
        String diffusePath = path.dataString();
        if (diffusePath != null && diffusePath.length() > 0) {
            log.info("Material diffuse created from path " + diffusePath);

            Texture diffuseTexture = createTexture(scene, aiMaterial, textureLocation, diffusePath);

            material.setTexture(textureIndex, diffuseTexture);
            materialFormat.addElement(MaterialElement.DIFFUSE, MaterialElementType.SAMPLER_2D, "diffuse");

            textureIndex++;
        } else {
            log.info("No diffuse texture found. Trying to get the diffuse color vector!.");

            int result = aiGetMaterialColor(aiMaterial,	AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if (result == 0) {
                Vector4f diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
                log.info("Diffuse vector: " + diffuse);

                material.setProperty("diffuse", diffuse);
                materialFormat.addElement(MaterialElement.DIFFUSE, MaterialElementType.VEC4, "diffuse");
            }
        }

        path.free();
        path = AIString.calloc();

        // Create color texture
        aiGetMaterialTexture(aiMaterial, aiTextureType_SPECULAR,0, path, (IntBuffer) null, null, null, null, null,null);
        String specularPath = path.dataString();
        if (specularPath != null && specularPath.length() > 0) {
            log.info("Material specular created from path " + diffusePath);

            Texture specularTexture = createTexture(scene, aiMaterial, textureLocation, diffusePath);

            material.setTexture(textureIndex, specularTexture);
            materialFormat.addElement(MaterialElement.SPECULAR, MaterialElementType.SAMPLER_2D, "specular");

            textureIndex++;
        } else {
            log.info("No specular texture found. Trying to get the specular color vector!.");

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
            if (result == 0) {
                Vector4f specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
                log.info("Specular vector: " + specular);

                material.setProperty("specular", specular);
                materialFormat.addElement(MaterialElement.SPECULAR, MaterialElementType.VEC4, "specular");
            }

        }

        path.free();
        path = AIString.calloc();


        // Create normal texture
        aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS,0, path, (IntBuffer) null, null, null, null, null,null);
        String normalPath = path.dataString();
        if (normalPath != null && normalPath.length() > 0)	{
            log.info("Material normal created from path " + normalPath);

            Texture normal = createTexture(scene, aiMaterial, textureLocation, normalPath);

            material.setTexture(textureIndex, normal);
            materialFormat.addElement(MaterialElement.NORMAL, MaterialElementType.SAMPLER_2D, "normal");
            textureIndex++;

        } else {
            log.info("No normal texture.");
        }
        path.free();

//        Vector4f ambient = DEFAULT_COLOUR;
//        int	result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
//        if (result == 0) {
//            ambient	= new Vector4f(color.r(), color.g(), color.b(), color.a());
//            log.info("Ambient: " + ambient);
//        }
        material.setMaterialFormat(materialFormat);
    }

    /**
     *
     * */
    private Texture createTexture(AIScene scene, AIMaterial aiMaterial, String textureLocation, String path) throws IOException {
        if (path.startsWith("*")) {
            int index = Integer.parseInt(path.substring(1));
            AITexture aiTexture = AITexture.create(scene.mTextures().get(index));
            ByteBuffer data = createBuffer(aiTexture);
            if (data == null)
                return null;
            else {
                Texture texture = new Texture(data, aiTexture.mWidth(), aiTexture.mHeight(), true);
                return texture;
            }
        } else {
            TextureData textureData = textureLoader.loadTexture(Paths.get(textureLocation, path));
//            textureData = TextureUtils.flipVertical(textureData);
            return new Texture(textureData, true);
        }
    }

    /**
     * Creates a ByteBuffer from the provided AITexture object.
     *
     * @param texture The AITexture to create a buffer from.
     * */
    private ByteBuffer createBuffer(AITexture texture) {
        ByteBuffer buffer = null;
        // Image is compressed and pcData is a pointer to a buffer of it in memory. mWidth is the size of that buffer.
        if (texture.mHeight() == 0) {
            String textureFormat = texture.achFormatHintString();
            log.debug("texture size is: " + texture.mWidth());
            log.debug("Address is: " + texture.address() + AITexture.PCDATA);
            log.debug("Texture type is: " + textureFormat);

            if (textureFormat.equalsIgnoreCase("png")) {
//                ByteBuffer rawPNGBuffer = memByteBuffer(texture.address() + AITexture.PCDATA, texture.mWidth());
//                try {
//                    if (rawPNGBuffer != null) {
//                        PNGDecoder decoder = new PNGDecoder(new EmbeddedPNGInputStream(rawPNGBuffer));
//                        buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
//                        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
//                        log.debug("Read png file.");
//                    } else {
//                        log.debug("Unable to find raw png buffer.");
//                    }
//                } catch (IOException e) {
//                    log.debug("Something went wrong! M: " + e.getMessage());
//                    e.printStackTrace();
//                }
            }
        } else {
            log.debug("texture size is " + texture.mWidth() + ", " + texture.mHeight());
            buffer = MemoryUtil.memAlloc(texture.mWidth() * texture.mHeight() * 4);
            AITexel.Buffer textureBuffer = texture.pcData(texture.mWidth() * texture.mHeight());
            for (AITexel texel : textureBuffer) {
                buffer.put(texel.r());
                buffer.put(texel.g());
                buffer.put(texel.b());
                buffer.put(texel.a());
            }
        }
        if (buffer != null)
            buffer.flip();
        return buffer;
    }

    /**
     * Creates a mesh object given the AIMesh to process.
     *
     * @param aiMesh AIMesh to process into a mesh object.
     * */
    private static Mesh processMesh(AIMesh aiMesh) {

        int elementIndex = 0;
        List<VertexElements> vertexElements = new ArrayList<>();

        Mesh mesh = new Mesh();
        mesh.bind();

        List<Float> vertices = new ArrayList<>();
        processVertices(aiMesh, vertices);

        if (vertices.size() > 0) {
            float[] verticesArray = toArrayf(vertices);
            // Compute the radius of this mesh.
            for (int i = 0; i < verticesArray.length; i++) {
                if (Math.abs(verticesArray[i]) > mesh.getRadius()) {
                    mesh.setRadius(Math.abs(verticesArray[i]));
                }
            }

            mesh.storeAttribute(elementIndex, verticesArray, VertexElements.POSITION.getSize());

            vertexElements.add(VertexElements.POSITION);
            elementIndex++;
        }

//        if (vertexFormat.hasElement(VertexElement.COLOR)) {
//        }

        List<Float> uvs = new ArrayList<>();
        processUVs(aiMesh, uvs);

        if (uvs.size() > 0) {
            float[] uvsArray = toArrayf(uvs);
            mesh.storeAttribute(elementIndex, uvsArray, VertexElements.UV.getSize());
            vertexElements.add(VertexElements.UV);
            elementIndex++;
        }

        List<Float> normals = new ArrayList<>();
        processNormals(aiMesh, normals);

        if (normals.size() > 0) {
            float[] normalsArray = toArrayf(normals);
            mesh.storeAttribute(elementIndex, normalsArray, VertexElements.NORMAL.getSize());
            vertexElements.add(VertexElements.NORMAL);
            elementIndex++;
        }

        List<Float> tangents = new ArrayList<>();
        processTangents(aiMesh, tangents);

        if (tangents.size() > 0) {
            float[] tangentsArray = toArrayf(tangents);
            mesh.storeAttribute(elementIndex, tangentsArray, VertexElements.TANGENT.getSize());
            vertexElements.add(VertexElements.TANGENT);
            elementIndex++;
        }

        List<Float> bitangents = new ArrayList<>();
        processBitangents(aiMesh, bitangents);

        if (bitangents.size() > 0) {
            float[] bitangentsArray = toArrayf(bitangents);
            mesh.storeAttribute(elementIndex, bitangentsArray, VertexElements.BITANGENT.getSize());
            vertexElements.add(VertexElements.BITANGENT);
            elementIndex++;
        }


        List<Integer> indices = new ArrayList<>();
        processIndices(aiMesh, indices);
        int[] indicesArray = toArrayi(indices);
        mesh.storeIndices(indicesArray);

        mesh.unbind();

        VertexFormat vertexFormat = new VertexFormat(vertexElements);
        return mesh;
    }

    /**
     * Creates a mesh object given the AIMesh to process.
     *
     * @param aiMesh AIMesh to process into a mesh object.
     * @param vertexFormat The {@link VertexFormat} this mesh should be processed into.
     * */
    private static Mesh processMesh(AIMesh aiMesh, VertexFormat vertexFormat) {

        Mesh mesh = new Mesh();
        mesh.bind();

        List<Float> vertices;
        if (vertexFormat.hasElement(VertexElements.POSITION)) {
            vertices = new ArrayList<>();
            processVertices(aiMesh, vertices);

            if (vertices.size() <= 0) {
                mesh.initializeAttribute(vertexFormat.getElementIndex(VertexElements.POSITION),
                        VertexElements.POSITION.getSize(), VertexElements.POSITION.getSize());
            } else {
                float[] verticesArray = toArrayf(vertices);

                // Compute the radius of this mesh.
                for (int i = 0; i < verticesArray.length; i++) {
                    if (Math.abs(verticesArray[i]) > mesh.getRadius()) {
                        mesh.setRadius(Math.abs(verticesArray[i]));
                    }
                }

                mesh.storeAttribute(vertexFormat.getElementIndex(VertexElements.POSITION), verticesArray,
                        VertexElements.POSITION.getSize());
            }
        }

//        if (vertexFormat.hasElement(VertexElement.COLOR)) {
//        }

        if (vertexFormat.hasElement(VertexElements.UV)) {
            List<Float> uvs = new ArrayList<>();
            processUVs(aiMesh, uvs);

            if (uvs.size() <= 0) {
                mesh.initializeAttribute(vertexFormat.getElementIndex(VertexElements.UV),
                        VertexElements.UV.getSize(), VertexElements.UV.getSize());
            } else {
                float[] uvsArray = toArrayf(uvs);
                mesh.storeAttribute(vertexFormat.getElementIndex(VertexElements.UV), uvsArray,
                        VertexElements.UV.getSize());
            }
        }

        if (vertexFormat.hasElement(VertexElements.NORMAL)) {
            List<Float> normals = new ArrayList<>();
            processNormals(aiMesh, normals);

            if (normals.size() <= 0) {
                mesh.initializeAttribute(vertexFormat.getElementIndex(VertexElements.NORMAL),
                        VertexElements.NORMAL.getSize(), VertexElements.NORMAL.getSize());
            } else {
                float[] normalsArray = toArrayf(normals);
                mesh.storeAttribute(vertexFormat.getElementIndex(VertexElements.NORMAL), normalsArray,
                        VertexElements.NORMAL.getSize());
            }
        }

        if (vertexFormat.hasElement(VertexElements.TANGENT)) {
            List<Float> tangents = new ArrayList<>();
            processTangents(aiMesh, tangents);

            if (tangents.size() <= 0) {
                mesh.initializeAttribute(vertexFormat.getElementIndex(VertexElements.TANGENT),
                        VertexElements.TANGENT.getSize(), VertexElements.TANGENT.getSize());
            } else {
                float[] tangentsArray = toArrayf(tangents);
                mesh.storeAttribute(vertexFormat.getElementIndex(VertexElements.TANGENT), tangentsArray,
                        VertexElements.TANGENT.getSize());
            }
        }

        if (vertexFormat.hasElement(VertexElements.BITANGENT)) {
            List<Float> bitangents = new ArrayList<>();
            processBitangents(aiMesh, bitangents);

            if (bitangents.size() <= 0) {
                mesh.initializeAttribute(vertexFormat.getElementIndex(VertexElements.BITANGENT),
                        VertexElements.BITANGENT.getSize(), VertexElements.BITANGENT.getSize());
            } else {
                float[] bitangentsArray = toArrayf(bitangents);
                mesh.storeAttribute(vertexFormat.getElementIndex(VertexElements.BITANGENT), bitangentsArray,
                        VertexElements.BITANGENT.getSize());
            }
        }

        List<Integer> indices = new ArrayList<>();
        processIndices(aiMesh, indices);
        int[] indicesArray = toArrayi(indices);
        mesh.storeIndices(indicesArray);

        mesh.unbind();
        return mesh;
    }

    /**
     * Processes the vertices of a mesh.
     *
     * @param aiMesh AIMesh to process vertices for.
     * @param vertices List of vertices to add processed vertices to.
     */
    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();

        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    /**
     * Processes the texture coordinates of a mesh.
     *
     * @param aiMesh AIMesh to process texture coordinates for.
     * @param textures List of texture coordinates to add
     * processed coordinates to.
     */
    private static void processUVs(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords(0);
        int uvCount = aiTextures != null ? aiTextures.remaining() : 0;

        for (int i = 0; i < uvCount; i++) {
            AIVector3D uv = aiTextures.get();
            textures.add(uv.x());
//            textures.add(uv.y());
            textures.add(uv.y());
        }
    }

    /**
     * Processes the normals of a mesh.
     *
     * @param aiMesh AIMesh to process normals for.
     * @param normals List of normals to add processed normals to.
     */
    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();

        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiVertex = aiNormals.get();
            normals.add(aiVertex.x());
            normals.add(aiVertex.y());
            normals.add(aiVertex.z());
        }
    }

    /**
     * Processes the tangents of a mesh.
     *
     * @param aiMesh AIMesh to process normals for.
     * @param tangents List of tangents to add processed tangents to.
     */
    private static void processTangents(AIMesh aiMesh, List<Float> tangents) {
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();

        while (aiTangents != null && aiTangents.remaining() > 0) {
            AIVector3D aiVertex = aiTangents.get();
            tangents.add(aiVertex.x());
            tangents.add(aiVertex.y());
            tangents.add(aiVertex.z());
        }
    }

    /**
     * Processes the tangents of a mesh.
     *
     * @param aiMesh AIMesh to process normals for.
     * @param bitangents List of tangents to add processed tangents to.
     */
    private static void processBitangents(AIMesh aiMesh, List<Float> bitangents) {
        AIVector3D.Buffer aiBitangents = aiMesh.mBitangents();

        while (aiBitangents != null && aiBitangents.remaining() > 0) {
            AIVector3D aiVertex = aiBitangents.get();
            bitangents.add(aiVertex.x());
            bitangents.add(aiVertex.y());
            bitangents.add(aiVertex.z());
        }
    }

    /**
     * Processes the indices of a mesh.
     *
     * @param aiMesh AIMesh to process indices for.
     * @param indices List of indices to add processed indices to.
     */
    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();

        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get();
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    /**
     * A failed attempt at loading the embedded textures via PNGDecoder.
     * */
    public class EmbeddedPNGInputStream extends InputStream {

        private byte[] signature = {(byte) 137, 80, 78, 71, 13, 10, 26, 10 };

        private ByteBuffer buf;

        private int signatureIndex = 0;

        public EmbeddedPNGInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            if (signatureIndex < signature.length) {
                return signature[signatureIndex++] & 0xFF;
            } else
                return buf.get() & 0xFF;
        }

        public int read(byte[] bytes, int off, int len)
                throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }

            len = Math.min(len, buf.remaining());

            if (signatureIndex < signature.length) {
                int i = 0;
                while (signatureIndex < len) {
                    bytes[off + i] = signature[signatureIndex];
                    signatureIndex++;
                    i++;
                }
            } else {
                buf.get(bytes, off, len);
            }
            return len;
        }
    }
}
