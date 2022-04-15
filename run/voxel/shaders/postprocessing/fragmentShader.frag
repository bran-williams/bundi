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

void main() {
    vec4 sceneColor = texture(albedoSampler, textureCoordinates);
    if (sceneColor.a <= 0) discard;

    float pixelDepth = linearizeDepth(texture(depthSampler, textureCoordinates).r);

    fragColor = vec4(sceneColor.rgb, 1F);

    //    fragColor = vec4(mix(sceneColor, edgeColor, detectEdge()), 1);
}