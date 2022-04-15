#version 330

// constants
const float transparencyThreshold = 0.5;
const int MAX_BLOCK_LIGHT = 16;
const int MIN_BLOCK_LIGHT = 1;

in vec3 passFragPos;
in vec2 passTextureCoordinates;
flat in int blockLight;

out vec4 fragColor;

struct Material {
    sampler2D diffuse;
    sampler2D emission;
};

uniform Material material;

uniform vec3 sunPosition;
uniform vec4 sunColor;
uniform vec4 skyColor;
uniform float fogDensity;
uniform int minBlockLight;

int unpackRed(int light) {
    return (light & 0xF00) >> 8;
}

int unpackGreen(int light) {
    return (light & 0xF0) >> 4;
}

int unpackBlue(int light) {
    return light & 0xF;
}

void main() {
    vec4 textureColor = texture(material.diffuse, passTextureCoordinates);

    // Allow transparency
    if (textureColor.a < transparencyThreshold) {
        discard;
    }

    float minLight = max(MIN_BLOCK_LIGHT, minBlockLight);

    float lightR = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackRed(blockLight)))) / MAX_BLOCK_LIGHT;
    float lightG = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackGreen(blockLight)))) / MAX_BLOCK_LIGHT;
    float lightB = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackBlue(blockLight)))) / MAX_BLOCK_LIGHT;
    vec3 lightFactor = vec3(lightR, lightG, lightB);

    vec3 diffuse  = textureColor.rgb * lightFactor;
    vec3 emission = texture(material.emission, passTextureCoordinates).rgb;

    float brightestLight = max(max(lightFactor.r, lightFactor.g), lightFactor.b);
    vec4 lightColor = vec4(diffuse + emission, 1F + brightestLight);

    fragColor = lightColor;
}
