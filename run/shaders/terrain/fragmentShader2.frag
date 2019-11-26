#version 330

const float PI = 3.1415926535897932384626433832795;

const vec4 GRASS = vec4(0);
const vec4 SAND = vec4(1, 0, 0, 0);
const vec4 ROCK = vec4(0, 1, 0, 0);
const vec4 SNOW = vec4(0, 0, 1, 0);

const vec3 UP = vec3(0, 1, 0);
const float MIN_GRASS_SLOPE = 0.2F;
const float MAX_GRASS_SLOPE = PI * 0.25F;

const float FADE_AMOUNT = 0.005;
const float MAX_HEIGHT = 128;
const float SNOW_HEIGHT = 0.75;
const float SAND_HEIGHT = 0.25;


in vec3 passPosition;
in vec2 passTextureCoordinates;
in mat3 passTBN;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec3 normalOut;

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

vec4 getBlend(vec3 normal) {
    // Calculate the slope given the normal (assuming the UP vector)
    float height = passPosition.y / MAX_HEIGHT;

    vec4 flatColor = GRASS;

    // border between flat color and sand...
    if (height >= SAND_HEIGHT - FADE_AMOUNT && height <= SAND_HEIGHT + FADE_AMOUNT) {
        float blendFactor = 1 - ((height - SAND_HEIGHT + FADE_AMOUNT) / (2 * FADE_AMOUNT));
        flatColor = mix(GRASS, SAND, blendFactor);
        // anything below here is sand..
    } else if (height <= SAND_HEIGHT) {
        flatColor = SAND;
    }

    // border between flat color and snow...
    if (height >= SNOW_HEIGHT - FADE_AMOUNT && height <= SNOW_HEIGHT + FADE_AMOUNT) {
        float blendFactor = (height - SNOW_HEIGHT + FADE_AMOUNT) / (2 * FADE_AMOUNT);
        flatColor = mix(GRASS, SNOW, blendFactor);
    } else if (height >= SNOW_HEIGHT) {
        flatColor = SNOW;
    }

    float slope = acos(dot(normal, UP)) / (2 * PI);

    vec4 blendColor = flatColor;
    if (slope < MIN_GRASS_SLOPE) {
        blendColor = mix(flatColor, ROCK, slope / MIN_GRASS_SLOPE);
    } else if (slope >= MIN_GRASS_SLOPE && slope < MAX_GRASS_SLOPE) {
        blendColor = mix(ROCK, flatColor, (slope - MIN_GRASS_SLOPE) * (1 / (MAX_GRASS_SLOPE - MIN_GRASS_SLOPE)));
    } else if (slope >= MAX_GRASS_SLOPE) {
        blendColor = flatColor;
    }

    return blendColor;
}

//vec4 calculateFlatColor() {
//    // Calculate the slope given the normal (assuming the UP vector)
//    float height = passPosition.y / MAX_HEIGHT;
//
//    vec4 blendColor = GRASS;
//
//    // border between flat color and sand...
//    if (height >= SAND_HEIGHT - FADE_AMOUNT && height <= SAND_HEIGHT + FADE_AMOUNT) {
//        float blendFactor = 1 - ((height - SAND_HEIGHT + FADE_AMOUNT) / (2 * FADE_AMOUNT));
//        blendColor = mix(blendColor, SAND, blendFactor);
//        // anything below here is sand..
//    } else if (height <= SAND_HEIGHT) {
//        blendColor = SAND;
//    }
//
//    // border between flat color and snow...
//    if (height >= SNOW_HEIGHT - FADE_AMOUNT && height <= SNOW_HEIGHT + FADE_AMOUNT) {
//        float blendFactor = (height - SNOW_HEIGHT + FADE_AMOUNT) / (2 * FADE_AMOUNT);
//        blendColor = mix(blendColor, SNOW, blendFactor);
//    } else if (height >= SNOW_HEIGHT) {
//        blendColor = SNOW;
//    }
//    return blendColor;
//}
//
//vec4 getBlend(vec3 normal) {
//    vec4 flatColor  = calculateFlatColor();
//    vec4 slopeColor = ROCK;
//
//    vec4 blendColor = flatColor;
//
//    float slope = acos(dot(normal, UP)) / (2 * PI);
//
//    if (slope < MIN_GRASS_SLOPE) {
//        blendColor = mix(flatColor, slopeColor, slope / MIN_GRASS_SLOPE);
//    } else if (slope >= MIN_GRASS_SLOPE && slope < MAX_GRASS_SLOPE) {
//        blendColor = mix(slopeColor, flatColor, (slope - MIN_GRASS_SLOPE) * (1 / (MAX_GRASS_SLOPE - MIN_GRASS_SLOPE)));
//    } else if (slope >= MAX_GRASS_SLOPE) {
//        blendColor = flatColor;
//    }
//
//    return blendColor;
//}

void main() {
    vec3 surfaceNormal = vec3(passTBN[2][0], passTBN[2][1], passTBN[2][2]);
    vec4 blend = getBlend(surfaceNormal);

    albedoOut = vec4(getColor(textureSampler, blend).xyz, materialShininess);

    if (hasNormalTexture) {
        vec3 normal = normalize(getColor(normalSampler, blend).rgb * 2 - 1);
        normalOut = normalize(passTBN * normal);
    } else {
        normalOut = surfaceNormal;
    }
}