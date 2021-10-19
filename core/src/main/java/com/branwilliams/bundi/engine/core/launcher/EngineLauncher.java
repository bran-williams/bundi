package com.branwilliams.bundi.engine.core.launcher;

import com.branwilliams.bundi.engine.core.context.*;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.core.Keycodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.branwilliams.bundi.engine.core.EngineConstants.KEYCODES_RESOURCE;

/**
 * Created by Brandon Williams on 7/22/2018.
 */
public final class EngineLauncher {

    private static final Logger log = LoggerFactory.getLogger(EngineLauncher.class);

    private EngineLauncher() {

    }

    /**
     * Launches the engine with the provided {@link LaunchConfiguration}. Creates an instance of the launch scene and
     * an instance of {@link EngineContext} for the engine before running it.
     * */
    public static void launchEngine(LaunchConfiguration configuration) {
        EngineContext engineContext = null;
        Scene scene = null;

        try {
            Class<?> launchScene = Class.forName(configuration.launchScene);
            if (!Scene.class.isAssignableFrom(launchScene)) {
                throw new RuntimeException("Launch scene must be a subclass of Scene!");
            }

            scene = (Scene) launchScene.newInstance();
            engineContext = createContext(configuration);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Unable to find launch scene '%s'.", configuration.launchScene),
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("Unable to access constructor of '%s'.", configuration.launchScene), e);
        } catch (InstantiationException e) {
            throw new RuntimeException(
                    String.format("Unable to construct an instance of '%s'.", configuration.launchScene), e);
        }

        runEngine(configuration, engineContext, scene);
    }

    /**
     * Creates the {@link EngineContext} from the launch configuration. Finds every class which extends from
     * {@link Scene} that is not abstract and does not contain the {@link Ignore} annotation.
     * */
    private static EngineContext createContext(LaunchConfiguration configuration) {
        Reflections reflections = new Reflections(configuration.sceneDirectory);
        List<Class<? extends Scene>> scenes = new ArrayList<>();

        for (Class<? extends Scene> clazz : reflections.getSubTypesOf(Scene.class)) {
            if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()
                    && !clazz.isAnnotationPresent(Ignore.class)) {
                scenes.add(clazz);
            }
        }
        log.info(String.format("Scenes found: [%d] %s", scenes.size(), scenes.toString()));

        return new EngineContext(Paths.get(configuration.assetDirectory), Paths.get(configuration.tempDirectory),
                Paths.get(configuration.screenshotDirectory), scenes);
    }

    /**
     * Runs the engine with the launch configuration, engine context, and scene provided.
     * */
    private static void runEngine(LaunchConfiguration configuration, EngineContext engineContext, Scene scene) {
        Keycodes keycodes = loadKeycodes();

        Window window = new Window(configuration.windowTitle, configuration.windowWidth, configuration.windowHeight,
                configuration.vsync, configuration.fullscreen, keycodes);

        Engine engine = new Engine(engineContext, window, scene);
        engine.run();
    }

    /**
     * Loads the keycode mappings defined by the launch configuration.
     * */
    private static Keycodes loadKeycodes() {
        String keycodesFileContents = IOUtils.readResource(KEYCODES_RESOURCE, null);

        Gson gson = new GsonBuilder().create();
        Type keyCodesType = new TypeToken<Map<String, Integer>>() {}.getType();
        try {
            Map<String, Integer> keyCodes = gson.fromJson(keycodesFileContents, keyCodesType);
            return new Keycodes(keyCodes);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(String.format("Unable to parse keycodes file '%s'.", KEYCODES_RESOURCE));
        }
    }

    public static void main(String[] args) {
        // argument check
        if (args.length <= 0) {
            throw new RuntimeException("The path to a configuration file must be provided.");
        }

        Path path = Paths.get(args[0]);

        // existence check
        if (!Files.exists(path)) {
            throw new RuntimeException(String.format("The provided configuration file '%s' could not be found.",
                    path.toAbsolutePath()));
        }

        // directory check
        if (Files.isDirectory(path)) {
            throw new RuntimeException(String.format("The provided configuration file '%s' cannot be a directory.",
                    path.toAbsolutePath()));
        }

        try {
            String fileContents = IOUtils.readFile(path, null);
            Gson gson = new GsonBuilder().create();
            LaunchConfiguration configuration = gson.fromJson(fileContents, LaunchConfiguration.class);
            launchEngine(configuration);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(String.format("Unable to parse configuration file '%s'.",
                    path.toAbsolutePath()), e);
        }
    }

}
