package com.branwilliams.bundi.voxel.render;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Renderer;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.render.mesh.builder.VoxelMeshBuilder;
import com.branwilliams.bundi.voxel.render.mesh.builder.VoxelMeshBuilderImpl;
import com.branwilliams.bundi.voxel.scene.VoxelMainMenuScene;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.Voxels;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class VoxelMainMenuRenderer implements Renderer, WindowListener {

    private final Voxel[] voxels = {
            Voxels.grass,
            Voxels.sand,
            Voxels.bricks,
            Voxels.bedrock,
            Voxels.diamond_block,
            Voxels.gold_block,
            Voxels.iron_block
    };

    private final VoxelMainMenuScene scene;

    private Projection orthoProjection;

    private DynamicShaderProgram dynamicShaderProgram;

    private Projection voxelProjection;

    private DynamicShaderProgram voxelShaderProgram;

    private VoxelMeshBuilder voxelMeshBuilder;

    private Mesh voxelMesh;

    private Transformable voxelTransform = new Transformation().position(0, 0, -3)
            .rotateFromEuler(0.5F, 0F, 0F);

    public VoxelMainMenuRenderer(VoxelMainMenuScene scene) {
        this.scene = scene;
        scene.addWindowListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        glClearColor(0F, 0.3F, 0.7F, 1F);
        glEnable(GL_CULL_FACE);

        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        try {
            this.orthoProjection = new Projection(window);
            dynamicShaderProgram = new DynamicShaderProgram();

            this.voxelProjection = new Projection(window, 70, 0.001f, 1000f);
            voxelShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV);
            voxelMeshBuilder = new VoxelMeshBuilderImpl(scene.getVoxelRegistry(), scene.getTexturePack());
            float halfSize = 0.5F;
            voxelMesh = voxelMeshBuilder.buildVoxelMesh(randomVoxel(), -halfSize, halfSize, -halfSize, halfSize,
                    -halfSize, halfSize);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Engine engine, Window window, double deltaTime) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (scene.getGuiScreen() != null) {
            dynamicShaderProgram.bind();
            dynamicShaderProgram.setProjectionMatrix(orthoProjection);
            dynamicShaderProgram.setModelMatrix(Transformable.empty());
            scene.getGuiScreen().render();
            ShaderProgram.unbind();
        }

        voxelTransform.rotateFromEuler(0, (float) deltaTime, 0);
        voxelShaderProgram.bind();
        voxelShaderProgram.setProjectionMatrix(voxelProjection);
        voxelShaderProgram.setModelMatrix(voxelTransform);
        MeshRenderer.render(voxelMesh, scene.getTexturePack().getMaterial());
        ShaderProgram.unbind();

    }

    @Override
    public void destroy() {
        dynamicShaderProgram.destroy();
        voxelShaderProgram.destroy();
        voxelMesh.destroy();
    }

    @Override
    public String getName() {
        return "VoxelMainMenuRenderer";
    }

    @Override
    public void resize(Window window, int width, int height) {
        orthoProjection.update();
        voxelProjection.update();
    }

    private Voxel randomVoxel() {
        return voxels[(int) (Math.random() * voxels.length)];
    }
}
