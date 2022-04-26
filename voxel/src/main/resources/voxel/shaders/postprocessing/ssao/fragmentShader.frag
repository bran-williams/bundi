#version 330

const int NOISE_X = 4;
const int NOISE_Y = 4;
const int KERNEL_SIZE = 64;

//float radius = 0.5;
//float bias = 0.025;

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D albedoSampler;
uniform sampler2D normalSampler;
uniform sampler2D depthSampler;
uniform sampler2D noiseSampler;

uniform mat4 projectionMatrix;
uniform int screenWidth;
uniform int screenHeight;
uniform vec3 samples[KERNEL_SIZE];
uniform float power;
uniform float radius;
uniform float bias;

/*
    Calculates the view-space position from the depth value.
    Credits to: https://stackoverflow.com/a/32246825 for this function.
*/
vec3 viewPosFromDepth(float depth) {
    float z = depth * 2.0 - 1.0;

    vec4 clipSpacePosition = vec4(textureCoordinates * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = inverse(projectionMatrix) * clipSpacePosition;

//    // Perspective division
    viewSpacePosition /= viewSpacePosition.w;

    return viewSpacePosition.xyz;
}

void main() {
    vec4 sceneColor = texture(albedoSampler, textureCoordinates);
    if (sceneColor.a <= 0) { discard; }

    vec2 noiseScale = vec2(screenWidth / NOISE_X, screenHeight / NOISE_Y);
    vec3 randomVec = normalize(texture(noiseSampler, textureCoordinates * noiseScale).xyz);

    vec3 position = viewPosFromDepth(texture(depthSampler, textureCoordinates).r);
    vec3 normal = normalize(texture(normalSampler, textureCoordinates).rgb);


    // create TBN change-of-basis matrix: from tangent-space to view-space
    vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN       = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;
    for (int i = 0; i < KERNEL_SIZE; ++i) {
        // get sample position
        vec3 samplePos = TBN * samples[i]; // from tangent to view-space
        samplePos = position + samplePos * radius;

        vec4 offset = vec4(samplePos, 1.0);
        offset      = projectionMatrix * offset;    // from view to clip-space
        offset.xyz /= offset.w;               // perspective divide
        offset.xyz  = offset.xyz * 0.5 + 0.5; // transform to range 0.0 - 1.0
        float sampleDepth = viewPosFromDepth(texture(depthSampler, offset.xy).r).z;

        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(position.z - sampleDepth));
        occlusion       += (sampleDepth >= samplePos.z + bias ? 1.0 : 0.0) * rangeCheck;
    }
    occlusion = 1.0 - (occlusion / KERNEL_SIZE);

    fragColor = vec4(pow(occlusion, power));
}