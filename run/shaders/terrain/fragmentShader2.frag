#version 330

const vec4 GRASS = vec4(0);
const vec4 SAND = vec4(1, 0, 0, 0);
const vec4 ROCK = vec4(0, 1, 0, 0);
const vec4 SNOW = vec4(0, 0, 1, 0);

const vec3 UP = vec3(0, 1, 0);
const float SNOW_HEIGHT = 64;
const float SAND_HEIGHT = 12;

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
    float slope = acos(dot(normal, UP));

    vec4 blendColor = vec4(0.0);

    // flatBlend is the blending factors used for flat surfaces
    vec4 flatBlend = GRASS;
    // horizontalBlend is the blending factors used for horizontal surfaces (e.g. slopes)
    vec4 horizontalBlend = ROCK;



    if (slope < 0.2) {
        float blendFactor = slope / 0.2;

        blendColor = mix(flatBlend, horizontalBlend, blendFactor);

        if (passPosition.y < SAND_HEIGHT) {
            blendColor += SAND * blendFactor;
        }

        if (passPosition.y > SNOW_HEIGHT) {
            blendColor += SNOW * blendFactor;
        }


    }

    if (slope >= 0.2 && slope < 0.7) {
        blendColor = mix(horizontalBlend, flatBlend, (slope - 0.2) * (1 / (0.7 - 0.2)));
    }

    if (slope >= 0.7) {
        blendColor = horizontalBlend;
    }




    return blendColor;

}

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