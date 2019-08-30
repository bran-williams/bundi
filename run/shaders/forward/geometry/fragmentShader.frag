#version 330

in vec3 passPosition;
in vec2 passTextureCoordinates;
in mat3 passTBN;

out vec4 albedoOut;

uniform sampler2D textureSampler;

void main() {
    albedoOut = vec4(texture(textureSampler, passTextureCoordinates).xyz, materialShininess);
}