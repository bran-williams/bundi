package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lock;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.shader.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.components.TextField;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.mcskin.steve.CreeperModel;
import com.branwilliams.mcskin.steve.MCModel;
import com.branwilliams.mcskin.steve.SteveModel;

import java.io.IOException;
import java.nio.file.Path;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McSkinScene extends AbstractScene implements Window.KeyListener {

    private McApi mcApi;

    private Camera camera;

    private GuiScreenManager guiScreenManager;

    private MCModel mcModel;

    private TextureLoader textureLoader;

    private Lock guiLock = new Lock();

    private Path tempDirectory;

    public McSkinScene() {
        super("mc skin");
        this.addKeyListener(this);
    }

    @Override
    public void init(Engine engine, Window window) throws Exception {
        tempDirectory = engine.getContext().getTempDirectory();

        window.disableCursor();

        guiScreenManager = new GuiScreenManager(this);
        guiScreenManager.init(engine, window);

        es.addSystem(new DebugCameraMoveSystem(this, guiLock, this::getCamera, 0.16F, 1F, true));
        es.initSystems(engine, window);

        Projection worldProjection = new Projection(window, 70, 0.001F, 1000F);

        RenderContext renderContext = new RenderContext(worldProjection);
        RenderPipeline<RenderContext> renderPipeline = new RenderPipeline<>(renderContext);
        renderPipeline.addLast(new SteveRenderPass(this::getCamera, this::getMcModel));
        renderPipeline.addLast(new GuiRenderPass<>(this, this::getGuiScreenManager));
        McSkinRenderer mcSkinRenderer = new McSkinRenderer(this, renderPipeline);
        this.setRenderer(mcSkinRenderer);

        textureLoader = new TextureLoader(engine.getContext());
        mcModel = new SteveModel(new Material(), 0F);
        mcApi = new McApi();

    }

    private TextField usernameField;

    private Button submitButton;
    private Button fixButton;

    @Override
    public void play(Engine engine) {
        camera = new Camera();

        camera.setPosition(0, 0, 4F);
        camera.lookAt(0F, 0F, 0F);

        // Initialize the default skin.
        try {
            TextureData textureData = textureLoader.loadTexture("temp/skin.png");
            Texture texture = new Texture(textureData, false);
            texture.bind();
            texture.nearestFilter();
            Texture.unbind(texture);

            mcModel.setTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUI() {
        ContainerManager containerManager = guiScreenManager.load("./mcskin.xml");

        usernameField = containerManager.getByTag("lined");
        submitButton = containerManager.getByTag("submit");
        submitButton.setHighlight(true);

        fixButton = containerManager.getByTag("fix");


        submitButton.onPressed(((button, clickAction) -> {
            if (submitButton.isHighlight() && clickAction.buttonId == 0 && usernameField.hasText()) {
                downloadSkin(usernameField.getText());
            }
            return true;
        }));

        fixButton.onPressed(((button, clickAction) -> {
            if (fixButton.isHighlight() && clickAction.buttonId == 0) {
                System.out.println("TODO implement me!");
            }
            return true;
        }));
    }

    private void downloadSkin(String username) {
        DownloadSkinTask downloadSkinTask = new DownloadSkinTask(username, mcApi, tempDirectory, this::onSkinDownloaded);
        downloadSkinTask.run();
    }

    private void onSkinDownloaded(String skinUrl) {
        try {
            TextureData textureData = textureLoader.loadTexture(skinUrl);
            Texture texture = new Texture(textureData, false);
            texture.bind();
            texture.nearestFilter();
            Texture.unbind(texture);
            mcModel.setTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fixButton.setHighlight(true);
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        if (guiScreenManager.getGuiScreen() != null)
            guiScreenManager.getGuiScreen().update();
    }

    @Override
    public void keyPress(Window window, int key, int scancode, int mods) {

    }

    @Override
    public void keyRelease(Window window, int key, int scancode, int mods) {
        switch (key) {
            case GLFW_KEY_ESCAPE:
                guiLock.toggle();
                if (guiLock.isLocked()) {
                    loadUI();
                    window.showCursor();
                } else {
                    window.disableCursor();
                    guiScreenManager.setGuiScreen(null);
                }
                break;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public MCModel getMcModel() {
        return mcModel;
    }

    public GuiScreenManager getGuiScreenManager() {
        return guiScreenManager;
    }
}
