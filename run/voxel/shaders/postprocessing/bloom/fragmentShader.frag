#version 330
const vec3 edgeColor = vec3(0, 1, 0);

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D albedoSampler;
uniform sampler2D depthSampler;

uniform float far;
uniform float near;

float linearizeDepth(float z) {
    float n = near;
    float f = far;
    return (2.0 * n) / (f + n - z * (f - n));
}

vec3 grayScale(vec3 color) {
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    return vec3(gray);
}

uniform float bloomWeights[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

vec3 bloomBlur(bool horizontal) {
    vec2 tex_offset = 1.0 / textureSize(albedoSampler, 0); // gets size of single texel
    vec3 result = texture(albedoSampler, textureCoordinates).rgb * bloomWeights[0]; // current fragment's contribution

    if (horizontal) {
        for (int i = 1; i < 5; ++i) {
            result += texture(albedoSampler, textureCoordinates + vec2(tex_offset.x * i, 0.0)).rgb * bloomWeights[i];
            result += texture(albedoSampler, textureCoordinates - vec2(tex_offset.x * i, 0.0)).rgb * bloomWeights[i];
        }
    } else {
        for (int i = 1; i < 5; ++i) {
            result += texture(albedoSampler, textureCoordinates + vec2(0.0, tex_offset.y * i)).rgb * bloomWeights[i];
            result += texture(albedoSampler, textureCoordinates - vec2(0.0, tex_offset.y * i)).rgb * bloomWeights[i];
        }
    }

    return result;
}

void main() {
    vec4 sceneColor = texture(albedoSampler, textureCoordinates);
    if (sceneColor.a <= 0) discard;

    float pixelDepth = linearizeDepth(texture(depthSampler, textureCoordinates).r);

    fragColor = vec4(sceneColor.rgb, 1F);

    //    fragColor = vec4(mix(sceneColor, edgeColor, detectEdge()), 1);
}