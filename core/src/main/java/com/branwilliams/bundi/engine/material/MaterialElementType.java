package com.branwilliams.bundi.engine.material;

public enum MaterialElementType {
    FLOAT("float"),
    VEC2("vec2"),
    VEC3("vec3"),
    VEC4("vec4"),
    MAT3("mat3"),
    MAT4("mat4"),
    SAMPLER_2D("sampler2D", true),
    SAMPLER_3D("sampler3D", true),
    SAMPLER_CUBEMAP("samplerCube", true);

    public final String glslType;

    public final boolean isTexture;

    MaterialElementType(String glslType) {
        this(glslType, false);
    }

    MaterialElementType(String glslType, boolean isTexture) {
        this.glslType = glslType;
        this.isTexture = isTexture;
    }

    public boolean isSampler() {
        return this == SAMPLER_2D || this == SAMPLER_3D || this == SAMPLER_CUBEMAP;
    }
}
