package com.branwilliams.frogger;

public class FroggerConstants {


    public static final int SCREEN_WIDTH_PIXELS = 1024;
    public static final int SCREEN_HEIGHT_PIXELS = 768;

    // dimensions and scale for frogger tilemap
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    public static final float TILE_SCALE = 2F;

    public static final int TILE_HEIGHT_SCALED = (int) (TILE_HEIGHT * TILE_SCALE);
    public static final int TILE_WIDTH_SCALED = (int) (TILE_WIDTH * TILE_SCALE);

    public static final int SCREEN_HEIGHT_TILES = SCREEN_HEIGHT_PIXELS / TILE_HEIGHT_SCALED;
    public static final int SCREEN_WIDTH_TILES = SCREEN_WIDTH_PIXELS / TILE_WIDTH_SCALED;
    public static final float CAMERA_MOVE_SPEED = 2F;

    public static final int[] FIREBALL_FRAMES = {
            0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
            50, 51, 52, 53, 54, 55, 56, 57, 58, 59
    };

    public static final int[] FROGMAN_FRAMES = { 0, 1, 2, 3, 0 };
    public static final int FROGMAN_FRAME_IDLE = 0;

    public static final int[] FIREPLACE2_FRAME = { 0, 1, 2, 3, 4, 5 };


    public static final String FROGMAN_NAME = "frogman";

    public static final String PARALLAX_BACKGROUND_FILE = "assets/parallax/glacial_background.json";
}
