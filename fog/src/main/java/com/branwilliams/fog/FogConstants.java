package com.branwilliams.fog;

import org.joml.Vector4f;

import java.awt.*;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;

public final class FogConstants {

    private FogConstants() {}

    // blueish
    public static final Vector4f SKY_COLOR = toVector4(new Color(0xFF87CEEB));

    // yellowish
    public static final  Vector4f SUN_COLOR = toVector4(new Color(0xFFFFE5B2));

    public static final String ENVIRONMENT_FILE = "environment.json";

    public static final String UI_INGAME_HUD = "ui/fog-ingame-hud.xml";

    public static final String MODEL_LOCATION = "models/cartoonland2/cartoonland2.obj";
    public static final String MODEL_TEXTURES = "models/cartoonland2/";

    public static final String[] PUFF_PARTICLES = {
            "textures/particle/WhitePuff/whitePuff00.png",
            "textures/particle/WhitePuff/whitePuff01.png",
            "textures/particle/WhitePuff/whitePuff02.png",
            "textures/particle/WhitePuff/whitePuff03.png",
            "textures/particle/WhitePuff/whitePuff04.png",
            "textures/particle/WhitePuff/whitePuff05.png",
            "textures/particle/WhitePuff/whitePuff06.png"
    };

    public static final String[] SMOKE_PARTICLES = {
            "textures/particle/smoke_01.png",
            "textures/particle/smoke_02.png",
            "textures/particle/smoke_03.png",
            "textures/particle/smoke_04.png",
            "textures/particle/smoke_05.png",
            "textures/particle/smoke_06.png",
            "textures/particle/smoke_07.png"
    };


    public static final String[] FLAME_PARTICLES = {
            "textures/particle/flame_05.png",
            "textures/particle/flame_06.png"
    };
}
