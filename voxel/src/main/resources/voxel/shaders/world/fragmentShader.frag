#version 330

// constants
const float TRANSPARENCY_THRESHOLD = 0.5;
const int MAX_BLOCK_LIGHT = 16;
const int MIN_BLOCK_LIGHT = 1;

in vec3 passFragPos;
in vec2 passTextureCoordinates;
in vec3 passNormal;
in vec3 passTangent;
flat in int blockLight;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 fragNormal;
layout (location = 2) out vec4 fragEmissive;

struct Material {
    sampler2D diffuse;
    sampler2D normal;
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

/**
 TBN calculation from
 http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html

 normal - the vertex normal
 tangent - the vertex tangent
 bumpMapNormal - the value sampled from the normal texture in the range 0 - 1.
*/
vec3 calculateMappedNormal(vec3 normal, vec3 tangent, vec3 bumpMapNormal) {
    bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);
    tangent = normalize(tangent - dot(tangent, normal) * normal);
    vec3 bitangent = cross(tangent, normal);
    mat3 TBN = mat3(tangent, bitangent, normal);
    return normalize(TBN * bumpMapNormal);
}

vec3 calculateLightFactor(int blockLight) {
    float minLight = max(MIN_BLOCK_LIGHT, minBlockLight);

    float lightR = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackRed(blockLight)))) / MAX_BLOCK_LIGHT;
    float lightG = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackGreen(blockLight)))) / MAX_BLOCK_LIGHT;
    float lightB = min(MAX_BLOCK_LIGHT, max(minLight, float(unpackBlue(blockLight)))) / MAX_BLOCK_LIGHT;
    return vec3(lightR, lightG, lightB);
}

float packLightIntoAlpha(int blockLight) {
    float brightestLight = max(max(float(unpackRed(blockLight)), float(unpackGreen(blockLight))), float(unpackBlue(blockLight)));
    return 1.0 + brightestLight;
}

void main() {
    vec4 materialDiffuse = texture(material.diffuse, passTextureCoordinates);

    // Allow transparency
    if (materialDiffuse.a < TRANSPARENCY_THRESHOLD) {
        discard;
    }

    vec3 diffuseLightFactor = calculateLightFactor(blockLight);
    vec3 diffuse  = materialDiffuse.rgb * diffuseLightFactor;

    vec3 materialNormal = texture(material.normal, passTextureCoordinates).rgb;
    vec3 emission = texture(material.emission, passTextureCoordinates).rgb;

    fragColor = vec4(diffuse, packLightIntoAlpha(blockLight));
    fragNormal = vec4(calculateMappedNormal(passNormal, passTangent, materialNormal), 1.0);
    fragEmissive = vec4(emission.rgb * diffuseLightFactor, 1.0);
}
