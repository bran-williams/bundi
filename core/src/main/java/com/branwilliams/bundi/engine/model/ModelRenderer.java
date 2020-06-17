package com.branwilliams.bundi.engine.model;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Brandon
 * @since September 17, 2019
 */
public enum ModelRenderer {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(ModelRenderer.class);

    /**
     *
     * */
    public static void renderModel(Model model) {
//        log.info("model rendering:");
        for (Material material : model.getData().keySet()) {
            bindMaterial(material);
//            log.info("Binding material=" + material);
            for (Mesh mesh : model.getData().get(material)) {
                renderMesh(mesh);
            }

            unbindMaterial(material);
        }
    }

    /**
     * Sets up in the information required to render this mesh with the given material.
     * */
    public static void bindMaterial(Material material) {
        if (material != null && material.hasTextures()) {
            Texture[] textures = material.getTextures();
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] != null) {
                    glActiveTexture(GL_TEXTURE0 + i);
                    textures[i].bind();
                }
            }
        }
    }

    /**
     * Renders the provided render batch.
     * */
    public static void renderMesh(Mesh mesh) {
        mesh.bind();
        mesh.enable();

        if (mesh.hasIndices()) {
            mesh.getEbo().bind();
        }

        if (mesh.hasIndices()) {
            glDrawElements(mesh.getRenderMode(), mesh.getIndiceCount(), GL_UNSIGNED_INT, 0);
        } else {
            if (mesh.getVertexCount() <= 0) {
                log.error("Mesh has a vertex count of zero: " + mesh);
            } else {
                glDrawArrays(mesh.getRenderMode(), 0, mesh.getVertexCount());
            }
        }


        if (mesh.hasIndices()) {
            mesh.getEbo().unbind();
        }

        mesh.disable();
        mesh.unbind();
    }


    /**
     * Undoes everything the setup function did to enable this mesh to be rendered.
     * */
    public static void unbindMaterial(Material material) {
        if (material != null && material.hasTextures()) {
            Texture[] textures = material.getTextures();
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] != null) {
                    glActiveTexture(GL_TEXTURE0 + i);
                    Texture.unbind(textures[i]);
                }
            }
        }
    }
}
