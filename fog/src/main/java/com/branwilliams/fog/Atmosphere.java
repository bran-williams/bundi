package com.branwilliams.fog;

import com.branwilliams.bundi.engine.shader.DirectionalLight;
import com.branwilliams.bundi.engine.shader.Fog;
import org.joml.Vector4f;

public class Atmosphere {

    private DirectionalLight sun;

    private Vector4f sunColor;

    private Vector4f skyColor;

    private Fog fog;

    public Atmosphere(DirectionalLight sun, Vector4f skyColor, Vector4f sunColor, Fog fog) {
        this.sun = sun;
        this.skyColor = skyColor;
        this.sunColor = sunColor;
        this.fog = fog;
    }

    public DirectionalLight getSun() {
        return sun;
    }

    public void setSun(DirectionalLight sun) {
        this.sun = sun;
    }

    public Vector4f getSunColor() {
        return sunColor;
    }

    public void setSunColor(Vector4f sunColor) {
        this.sunColor = sunColor;
    }

    public Vector4f getSkyColor() {
        return skyColor;
    }

    public void setSkyColor(Vector4f skyColor) {
        this.skyColor = skyColor;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }
}
