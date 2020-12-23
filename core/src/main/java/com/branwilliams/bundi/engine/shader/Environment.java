package com.branwilliams.bundi.engine.shader;

public class Environment {

    private Fog fog;

    private PointLight[] pointLights;

    private DirectionalLight[] directionalLights;

    private SpotLight[] spotLights;

    public Environment(Fog fog, PointLight[] pointLights, DirectionalLight[] directionalLights, SpotLight[] spotLights) {
        this.fog = fog;
        this.pointLights = pointLights;
        this.directionalLights = directionalLights;
        this.spotLights = spotLights;
    }

    public boolean hasFog() {
        return fog != null;
    }

    public boolean hasPointLights() {
        return pointLights != null && pointLights.length > 0;
    }

    public boolean hasDirectionalLights() {
        return directionalLights != null && directionalLights.length > 0;
    }

    public boolean hasSpotLights() {
        return spotLights != null && spotLights.length > 0;
    }

    public boolean hasLights() {
        return hasPointLights() || hasDirectionalLights() || hasSpotLights();
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public DirectionalLight[] getDirectionalLights() {
        return directionalLights;
    }

    public void setDirectionalLights(DirectionalLight[] directionalLights) {
        this.directionalLights = directionalLights;
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

}
