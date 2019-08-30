#version 330

in vec2 passTextureCoordinates;
in mat3 passTBN;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec4 normalOut;

// RGB = diffuse color
// A = metallic
uniform sampler2D albedoSampler;

// RGB = normal vector
// A = roughness
uniform sampler2D normalSampler;

void main() {
    albedoOut = texture(albedoSampler, passTextureCoordinates);

    vec4 normalSample = texture(normalSampler, passTextureCoordinates);
    vec3 normal = normalize(normalSample.rgb * 2 - 1);
    normalOut = vec4(normalize(passTBN * normal), normalSample.a);
}