#version 330

in vec3 passPosition;
in vec2 passTextureCoordinates;
in mat3 passTBN;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec3 normalOut;

uniform sampler2D textureSampler;
uniform sampler2D normalSampler;
uniform float materialShininess;
uniform bool hasNormalTexture;

void main() {
    albedoOut = vec4(texture(textureSampler, passTextureCoordinates).xyz, materialShininess);
    //normalOut = hasNormalTexture ? normalize(texture(normalSampler, passTextureCoordinates).rgb * 2 - 1) : normalize(passNormal);
    if (hasNormalTexture) {
        vec3 normal = normalize(texture(normalSampler, passTextureCoordinates).rgb * 2 - 1);
        normalOut = normalize(passTBN * normal);
    } else {
        normalOut = vec3(passTBN[2][0], passTBN[2][1], passTBN[2][2]);
    }
}