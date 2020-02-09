package com.branwilliams.bundi.voxel.render.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.util.TextureUtils;
import com.branwilliams.bundi.voxel.VoxelScene;
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
public class VoxelGuiRenderPass extends RenderPass<VoxelRenderContext> implements Window.WindowListener {

    private static final float CROSSHAIR_SIZE = 8F;

    private final Transformable crosshairTransform = new Transformation();

    private final Vector4f crosshairColor = new Vector4f(1F);

    private final VoxelScene scene;

    private DynamicShaderProgram shapeShaderProgram;

    private DynamicShaderProgram texturedShaderProgram;

    private DynamicShaderProgram dynamicShaderProgram;

    private DynamicVAO shapeVao;

    private DynamicVAO texturedVao;

    private Texture[] mipmaps;

    private int mipLevel = 0;

    public VoxelGuiRenderPass(VoxelScene scene) {
        scene.addWindowListener(this);
        this.scene = scene;
    }

    @Override
    public void init(VoxelRenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shapeShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION);
            texturedShaderProgram = new DynamicShaderProgram(VertexFormat.POSITION_UV);
            dynamicShaderProgram = new DynamicShaderProgram();
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        shapeVao = new DynamicVAO(VertexFormat.POSITION);
        crosshairTransform.position(window.getWidth() * 0.5F, window.getHeight() * 0.5F, 0F);
        buildCrosshair();

        Texture texture = scene.getTexturePack().getDiffuseTextureAtlas();
        mipmaps = new Texture[TextureUtils.getMaxMipMaps(texture.getWidth(), texture.getHeight(), 1)];
        for (int i = 0; i < mipmaps.length; i++) {
            TextureData mipTextureData = texture.getBuffer(i);
            mipmaps[i] = new Texture(mipTextureData, false);
            mipmaps[i].bind();
            mipmaps[i].nearestFilter();
            Texture.unbind(mipmaps[i]);
        }
        texturedVao = new DynamicVAO(VertexFormat.POSITION_UV);
    }

    @Override
    public void render(VoxelRenderContext renderContext, Engine engine, Window window, double deltaTime) {
        glDisable(GL_DEPTH_TEST);

        if (scene.getGuiScreen() != null) {
            dynamicShaderProgram.bind();
            dynamicShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
            dynamicShaderProgram.setModelMatrix(Transformable.empty());
            scene.getGuiScreen().render();
            ShaderProgram.unbind();
            return;
        }

        drawCrosshair(renderContext);

//        drawPlayerInventory(renderContext, window);

        ShaderProgram.unbind();
    }

    @Override
    public void resize(Window window, int width, int height) {
        crosshairTransform.position(width * 0.5F, height * 0.5F, 0F);
    }

    private void drawCrosshair(VoxelRenderContext renderContext) {
        shapeShaderProgram.bind();
        shapeShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        shapeShaderProgram.setModelMatrix(crosshairTransform);
        shapeShaderProgram.setColor(crosshairColor);

        shapeVao.draw(GL_LINES);
    }

    private void drawPlayerInventory(VoxelRenderContext renderContext, Window window) {

        texturedShaderProgram.bind();
        texturedShaderProgram.setProjectionMatrix(renderContext.getOrthoProjection());
        texturedShaderProgram.setModelMatrix(Transformable.empty());

        float size = 32F;
        float padding = 6F;

        glActiveTexture(GL_TEXTURE0);
        scene.getTexturePack().getDiffuseTextureAtlas().bind();
        texturedVao.begin();

        List<Item> items = scene.getPlayerState().getInventory().getItems();

        float x = window.getWidth() * 0.5F - (items.size() * (size + padding)) * 0.5F;
        float y = window.getHeight() - size - padding;

        for (Item item : scene.getPlayerState().getInventory().getItems()) {
            drawItem(item, x, y, size);
            x += size + padding;
        }

        drawItem(scene.getPlayerState().getInventory().getHeldItem(),
                window.getWidth() * 0.5F - size * 0.5F,
                window.getHeight() - 2 * (size + padding),
                size);
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

//    private void drawMipmaps(Voxel voxel, int x, int y, int padding, int size) {
//        for (int i = 0; i < mipmaps.length; i++) {
//            mipmaps[i].bind();
//            texturedVao.begin();
//            addVoxel(voxel, x + (padding + size) * i, y, size);
//            texturedVao.draw();
//        }
//    }
//
//    private void drawTexture(Texture texture, int x, int y) {
//        texturedVao.begin();
//        texture.bind();
//        int width = scene.getTexturePack().getDiffuseTextureAtlas().getWidth();
//        int height = scene.getTexturePack().getDiffuseTextureAtlas().getHeight();
//        addRect(texturedVao, x, y, x + width, y + height, 0, 0, 1, 1);
//        texturedVao.draw();
//    }

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

    private void buildCrosshair() {
        shapeVao.begin();
        shapeVao.position(0, -CROSSHAIR_SIZE, 0).endVertex();
        shapeVao.position(0, CROSSHAIR_SIZE, 0).endVertex();

        shapeVao.position(-CROSSHAIR_SIZE, 0F, 0).endVertex();
        shapeVao.position(CROSSHAIR_SIZE, 0F, 0).endVertex();
        shapeVao.compile();
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shapeShaderProgram.destroy();
        this.dynamicShaderProgram.destroy();
        this.texturedShaderProgram.destroy();
        this.shapeVao.destroy();
        this.texturedVao.destroy();
    }
}
