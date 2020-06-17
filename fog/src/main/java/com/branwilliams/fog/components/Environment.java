package com.branwilliams.fog.components;

import com.branwilliams.bundi.engine.shader.DirectionalLight;
import com.branwilliams.bundi.engine.shader.PointLight;
import com.branwilliams.bundi.engine.shader.SpotLight;
import com.branwilliams.fog.Atmosphere;

public class Environment {

    private Atmosphere atmosphere;

    private PointLight[] pointLights;

    private DirectionalLight[] directionalLights;

    private SpotLight[] spotLights;

}
