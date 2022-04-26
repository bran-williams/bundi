#version 330

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D ssaoSampler;

void main() {
    vec2 texelSize = 1.0 / vec2(textureSize(ssaoSampler, 0));

    float result = 0.0;
    for (int x = -2; x < 2; ++x) {
        for (int y = -2; y < 2; ++y) {
            vec2 offset = vec2(float(x), float(y)) * texelSize;
            result += texture(ssaoSampler, textureCoordinates + offset).r;
        }
    }
    result = result / (4.0 * 4.0);

    fragColor = vec4(result, result, result, 1.0);
}
