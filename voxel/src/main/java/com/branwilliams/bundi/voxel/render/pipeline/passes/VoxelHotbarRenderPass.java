package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.voxel.scene.VoxelScene;
import com.branwilliams.bundi.voxel.inventory.Item;
import com.branwilliams.bundi.voxel.inventory.VoxelItem;
import com.branwilliams.bundi.voxel.render.pipeline.VoxelRenderContext;
import com.branwilliams.bundi.voxel.voxels.*;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Brandon
 * @since August 10, 2019
 */
public class VoxelHotbarRenderPass extends RenderPass<VoxelRenderContext> {

    private static final int HOTBAR_ITEM_SIZE = 48;

    private static final int HOTBAR_PADDING = 3;

    private final Transformable selectionTransform = new Transformation();

    private final Vector4f selectionColor = new Vector4f(1F, 1F, 1F, 1F);

    private final VoxelScene scene;

    private DynamicShaderProgram shapeShaderProgram;

    private DynamicShaderProgram texturedShaderProgram;

    private DynamicVAO selection;

    private DynamicVAO texturedVao;

    public VoxelHotbarRenderPass(VoxelScene scene) {
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shapeShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION);
            texturedShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        selection = new DynamicVAO(VertexFormat.POSITION);
        buildSelection(-HOTBAR_PADDING, -HOTBAR_PADDING, HOTBAR_ITEM_SIZE + HOTBAR_PADDING,
                HOTBAR_ITEM_SIZE + HOTBAR_PADDING);

        texturedVao = new DynamicVAO(VertexFormat.POSITION_UV);
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        if (scene.getGuiScreen() != null) {
            return;
        }

        drawPlayerHotbar(renderContext, window);
    }

    private void drawPlayerHotbar(VoxelRenderContext renderContext, Window window) {
        float size = HOTBAR_ITEM_SIZE;
        float padding = 4F + HOTBAR_PADDING;
        List<Item> items = scene.getPlayerState().getInventory().getItems();

        float x =  window.getWidth() * 0.5F - (items.size() * (size + padding)) * 0.5F;
        float y = window.getHeight() - size - padding;

        texturedVao.begin();
        for (Item item : scene.getPlayerState().getInventory().getItems()) {
            drawItem(item, x, y, size);
            x += size + padding;
        }
        texturedVao.compile();

        x = window.getWidth() * 0.5F - (items.size() * (size + padding)) * 0.5F + (size + padding)
                * scene.getPlayerState().getInventory().getHeldIndex();
        shapeShaderProgram.bind();
        shapeShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        shapeShaderProgram.setModelMatrix(selectionTransform.position(x, y, 0));
        shapeShaderProgram.setColor(selectionColor);
        selection.draw(GL_TRIANGLES);

        texturedShaderProgram.bind();
        texturedShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        texturedShaderProgram.setModelMatrix(Transformable.empty());

        glActiveTexture(GL_TEXTURE0);
        scene.getTexturePack().getDiffuseTextureAtlas().bind();
        texturedVao.draw();
    }

    private void drawItem(Item item, float x, float y, float size) {
        if (item instanceof VoxelItem) {
            Voxel voxel = ((VoxelItem) item).getVoxel();
            addVoxel(voxel, x, y, size);
        }
    }

    private void addVoxel(Voxel voxel, float x, float y, float size) {
        Vector4f textureCoordinates = scene.getTexturePack().getTextureCoordinates(voxel.id, VoxelFace.TOP);
        addRect(texturedVao, x, y, x + size, y + size,
                textureCoordinates.x, textureCoordinates.y, textureCoordinates.z, textureCoordinates.w);
    }

    /**
     * Adds a textured rectangle at the provided positions.
     * */
    public void addRect(DynamicVAO vao, float x, float y, float x1, float y1, float u, float v, float s, float t) {
        float z = 0F;
        vao.position(x1, y, z).texture(s, v).endVertex();
        vao.position(x, y, z).texture(u, v).endVertex();
        vao.position(x, y1, z).texture(u, t).endVertex();
        vao.position(x, y1, z).texture(u, t).endVertex();
        vao.position(x1, y1, z).texture(s, t).endVertex();
        vao.position(x1, y, z).texture(s, v).endVertex();
    }

    private void buildSelection(float x, float y, float x1, float y1) {
        selection.begin();
        float z = 0F;

        selection.position(x1, y, z).endVertex();
        selection.position(x, y, z).endVertex();
        selection.position(x, y1, z).endVertex();
        selection.position(x, y1, z).endVertex();
        selection.position(x1, y1, z).endVertex();
        selection.position(x1, y, z).endVertex();
        selection.compile();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shapeShaderProgram.destroy();
        this.texturedShaderProgram.destroy();
        this.texturedVao.destroy();
    }
}