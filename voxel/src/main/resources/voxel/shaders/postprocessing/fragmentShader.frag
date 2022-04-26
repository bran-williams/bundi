#version 330
const vec3 edgeColor = vec3(0, 1, 0);

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D albedoSampler;
uniform sampler2D ssaoSampler;
uniform sampler2D bloomSampler;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

vec3 grayScale(vec3 color) {
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    return vec3(gray);
}

void main() {
    vec4 sceneColor = texture(albedoSampler, textureCoordinates);
    if (sceneColor.a <= 0) discard;

    float ao = texture(ssaoSampler, textureCoordinates).r;
    vec3 bloom = texture(bloomSampler, textureCoordinates).rgb;

    fragColor = vec4(sceneColor.rgb * ao + bloom, 1.0);
}