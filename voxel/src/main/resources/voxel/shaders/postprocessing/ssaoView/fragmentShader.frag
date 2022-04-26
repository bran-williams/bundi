#version 330

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D albedoSampler;
uniform sampler2D ssaoSampler;

void main() {
    vec4 sceneColor = texture(albedoSampler, textureCoordinates);
    if (sceneColor.a <= 0) discard;

    vec4 ssao = texture(ssaoSampler, textureCoordinates);
    float ao = max(ssao.r, max(ssao.g, ssao.b));

    fragColor = vec4(ao, ao, ao, 1.0);
}