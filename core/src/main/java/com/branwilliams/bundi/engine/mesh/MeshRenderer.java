package com.branwilliams.bundi.engine.mesh;

import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.texture.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Renders meshes.
 * */
public enum MeshRenderer {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(MeshRenderer.class);

    /**
     * Sets up in the information required to render this mesh with the given material.
     * */
    public static void bind(Mesh mesh, Material material) {
        mesh.bind();
        mesh.enable();

        if (material != null && material.hasTextures()) {
            Texture[] textures = material.getTextures();
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] != null) {
                    glActiveTexture(GL_TEXTURE0 + i);
                    textures[i].bind();
                }
            }
        }
        if (mesh.hasIndices()) {
            mesh.getEbo().bind();
        }
    }

    /**
     * Binds and renders a given mesh with the given material.
     * */
    public static void render(Mesh mesh, Material material) {
        bind(mesh, material);
        render(mesh);
        unbind(mesh, material);
    }

    /**
     * Renders the provided render batch.
     * */
    public static void render(Mesh mesh) {
        if (mesh.hasIndices()) {
            glDrawElements(mesh.getRenderMode(), mesh.getIndiceCount(), GL_UNSIGNED_INT, 0);
        } else {
            if (mesh.getVertexCount() <= 0) {
                log.error("Mesh has a vertex count of zero: " + mesh);
            } else {
                glDrawArrays(mesh.getRenderMode(), 0, mesh.getVertexCount());
            }
        }
    }

    /**
     * Undoes everything the setup function did to enable this mesh to be rendered.
     * */
    public static void unbind(Mesh mesh, Material material) {
        if (material != null && material.hasTextures()) {
            Texture[] textures = material.getTextures();
            for (int i = 0; i < textures.length; i++) {
                if (textures[i] != null) {
                    glActiveTexture(GL_TEXTURE0 + i);
                    Texture.unbind(textures[i]);
                }
            }
        }

        if (mesh.hasIndices()) {
            mesh.getEbo().unbind();
        }

        mesh.disable();
        mesh.unbind();

    }


}