package com.branwilliams.bundi.engine.core.context;

/**
 * Holds the launch configuration variables given to the launcher in order to run an application.
 *
 * @author Brandon
 * @since May 02, 2019
 */
public class LaunchConfiguration {

    public String windowTitle;

    public int windowWidth;

    public int windowHeight;

    public boolean vsync;

    public boolean fullscreen;

    public String keycodes;

    /***/
    public String assetDirectory;

    public String tempDirectory;

    /**/
    public String launchScene;

    public String sceneDirectory;

}
