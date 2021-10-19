package com.branwilliams.bundi.engine.core.context;

/**
 * Holds the launch configuration variables given to the launcher in order to run an application.
 *
 * @author Brandon
 * @since May 02, 2019
 */
public class LaunchConfiguration {

    /**
     * The title of this app's window.
     * */
    public String windowTitle;

    /**
     * The launch window width.
     * */
    public int windowWidth;

    /**
     * The launch window height.
     * */
    public int windowHeight;

    /**
     * True for vertical-sync.
     * */
    public boolean vsync;

    /**
     * True if the app should launch in full-screen.
     * */
    public boolean fullscreen;

    /**
     * This is the directory for asset storage.
     * */
    public String assetDirectory;

    /**
     * This is the directory for temporary storage.
     * */
    public String tempDirectory;

    /**
     * This is the directory for storing screenshots.
     * */
    public String screenshotDirectory;

    /**
    * This is the class-path of the first scene to launch.
    * */
    public String launchScene;

    /**
     * This is the class-path to search for scenes.
     * */
    public String sceneDirectory;

    /**
     * Some default values here...
     * */
    public LaunchConfiguration() {
        this.screenshotDirectory = "screenshots";
    }
}
