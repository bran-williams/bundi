#version 330

in vec3 passPosition;
in vec2 passTextureCoordinates;
in mat3 passTBN;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec3 normalOut;

uniform sampler2D blendmapSampler;
uniform sampler2DArray textureSampler;
uniform sampler2DArray normalSampler;
uniform bool hasNormalTexture;
uniform float materialShininess;
uniform int tiling;

vec4 getColor(sampler2DArray texSampler, vec4 blend) {
    vec2 adjustedTextureCoordinates = passTextureCoordinates * tiling;

    //vec2 adjustedTextureCoordinates = passTextureCoordinates * uvTextureSize;
    vec4 color0 = texture(texSampler, vec3(adjustedTextureCoordinates, 0));
    vec4 color1 = texture(texSampler, vec3(adjustedTextureCoordinates, 1));
    vec4 color2 = texture(texSampler, vec3(adjustedTextureCoordinates, 2));
    vec4 color3 = texture(texSampler, vec3(adjustedTextureCoordinates, 3));

    vec4 baseColor = mix(color0, color1, blend.r);
    baseColor = mix(baseColor, color2, blend.g);
    baseColor = mix(baseColor, color3, blend.b);
    return baseColor;
}

void main() {
    vec4 blend = texture(blendmapSampler, passTextureCoordinates);
    albedoOut = vec4(getColor(textureSampler, blend).xyz, materialShininess);
    if (hasNormalTexture) {
        vec3 normal = normalize(getColor(normalSampler, blend).rgb * 2 - 1);
        normalOut = normalize(passTBN * normal);
    } else {
        normalOut = vec3(passTBN[2][0], passTBN[2][1], passTBN[2][2]);
    }
}