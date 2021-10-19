#version 330

in vec3 passPosition;
in vec2 passTextureCoordinates;
in mat3 passTBN;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec3 normalOut;

uniform sampler2D textureSampler;
uniform sampler2D normalSampler;

uniform bool hasNormalTexture;
uniform float materialShininess;
uniform int tiling;


vec3 getBlend(vec3 surfaceNormal) {
    // in wNorm is the world-space normal of the fragment
    vec3 blending = abs( surfaceNormal );
    blending = normalize(max(blending, 0.00001)); // Force weights to sum to 1.0
    float b = (blending.x + blending.y + blending.z);
    blending /= vec3(b, b, b);
    return blending;
}

vec4 getColor(sampler2D texSampler, vec3 coords, vec3 blend) {
    vec4 xaxis = texture2D(texSampler, coords.yz);
    vec4 yaxis = texture2D(texSampler, coords.xz);
    vec4 zaxis = texture2D(texSampler, coords.xy);
    // blend the results of the 3 planar projections.
    return xaxis * blend.x + xaxis * blend.y + zaxis * blend.z;
}

void main() {
    vec3 surfaceNormal = vec3(passTBN[2][0], passTBN[2][1], passTBN[2][2]);
    vec3 blend = getBlend(surfaceNormal);

    albedoOut = vec4(getColor(textureSampler, passPosition, blend).xyz, materialShininess);

    if (hasNormalTexture) {
        vec3 normal = normalize(getColor(normalSampler, passPosition, blend).rgb * 2 - 1);
        normalOut = normalize(passTBN * normal);
    } else {
        normalOut = surfaceNormal;
    }
}