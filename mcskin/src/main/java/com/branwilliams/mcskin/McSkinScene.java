package com.branwilliams.mcskin;

import com.branwilliams.bundi.engine.core.AbstractScene;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Lock;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPipeline;
import com.branwilliams.bundi.engine.shader.Camera;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Projection;
import com.branwilliams.bundi.engine.systems.DebugCameraMoveSystem;
import com.branwilliams.bundi.engine.texture.Texture;
import com.branwilliams.bundi.engine.texture.TextureData;
import com.branwilliams.bundi.engine.texture.TextureLoader;
import com.branwilliams.bundi.engine.util.TextureUtils;
import com.branwilliams.bundi.gui.api.ContainerManager;
import com.branwilliams.bundi.gui.api.components.Button;
import com.branwilliams.bundi.gui.api.components.TextField;
import com.branwilliams.bundi.gui.pipeline.GuiRenderPass;
import com.branwilliams.bundi.gui.screen.GuiScreenManager;
import com.branwilliams.mcskin.steve.MCModel;
import com.branwilliams.mcskin.steve.HumanoidModel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private String skinUrl;

    private Lock guiLock = new Lock();

    private Path tempDirectory;

    private final List<String> downloadedSkins = new CopyOnWriteArrayList<>();

    private Lock downloadLock = new Lock();

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
        mcModel = new HumanoidModel(new Material(), 0F);
//        mcModel = new PlayerModel(new Material(), 0F);
        mcApi = new McApi();

        skinUrl = "skinfixer/skin.png";
    }

    @Override
    public void play(Engine engine) {
        camera = new Camera();

        camera.setPosition(0, 0, 4F);
        camera.lookAt(0F, 0F, 0F);

        // Initialize the default skin.
        try {
            TextureData skinData = textureLoader.loadTexture(skinUrl);
            setModelSkin(skinData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextField usernameField;

    private Button submitButton;
    private Button fixButton;

    private void loadUI() {
        ContainerManager containerManager = guiScreenManager.loadAsGuiScreen("./ui/mcskin.xml");

        usernameField = containerManager.getByTag("lined");
        submitButton = containerManager.getByTag("submit");
        submitButton.setActive(true);

        fixButton = containerManager.getByTag("fix");

        submitButton.onPressed(((button, clickAction) -> {
            if (submitButton.isActive() && clickAction.buttonId == 0 && usernameField.hasText()) {
                downloadSkin(usernameField.getText());
            }
            return true;
        }));

        fixButton.onPressed(((button, clickAction) -> {
            if (fixButton.isActive() && clickAction.buttonId == 0) {
                try {
                    TextureData skinData = textureLoader.loadTexture(skinUrl);
                    setModelSkin(applyOverlay(skinData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }));
    }

    private void downloadSkin(String username) {
        if (!downloadLock.isLocked()) {
            DownloadSkinTask downloadSkinTask = new DownloadSkinTask(username, mcApi, tempDirectory, this::onSkinDownloaded);
            new Thread(downloadSkinTask).start();
            downloadLock.setLocked(true);
        }
    }

    private void onSkinDownloaded(String skinUrl) {
        synchronized (downloadedSkins) {
            downloadedSkins.add(skinUrl);
        }
    }

    private void updateSkin(String skinUrl) {
        try {
            TextureData textureData = textureLoader.loadTexture(skinUrl);
            setModelSkin(textureData);
            this.skinUrl = skinUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        fixButton.setActive(true);
    }

    /**
     * Sets or replaces the skin assigned to the model.
     * */
    private void setModelSkin(TextureData textureData) {
        Texture texture = new Texture(textureData, false);
        texture.bind();
        texture.nearestFilter();
        Texture.unbind(texture);
        mcModel.setTexture(texture);
    }

    /**
     * Applies the overlay onto of the provided texture data only if they are the same dimensions.
     * */
    private TextureData applyOverlay(TextureData textureData) {
        try {
            TextureData overlayData = textureLoader.loadTexture("skinfixer/overlay.png");

            // overlay if the dimensions line up!
            if (overlayData.getWidth() == textureData.getWidth() && overlayData.getHeight() == textureData.getHeight())
                textureData = TextureUtils.combine(textureData, overlayData, 4, this::combineOverlay);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return textureData;
    }

    @Override
    public void pause(Engine engine) {

    }

    @Override
    public void update(Engine engine, double deltaTime) {
        super.update(engine, deltaTime);
        guiScreenManager.update();

        synchronized (downloadedSkins) {
            if (!downloadedSkins.isEmpty()) {
                for (String skin : downloadedSkins) {
                    updateSkin(skin);
                }
                downloadedSkins.clear();

                if (downloadLock.isLocked())
                    downloadLock.setLocked(false);
            }
        }
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

    private Integer combineOverlay(int skinARGB, int overlayARGB) {
        if (overlayARGB == 0) {
            return skinARGB;
        } else {
            return overlayARGB;
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
