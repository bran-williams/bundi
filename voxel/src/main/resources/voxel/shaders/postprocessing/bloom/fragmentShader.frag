#version 330

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D emissionSampler;
uniform bool horizontal;

uniform float bloomWeights[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 texOffset = 1.0 / textureSize(emissionSampler, 0); // gets size of single texel
    vec3 result = texture(emissionSampler, textureCoordinates).rgb * bloomWeights[0]; // current fragment's contribution

    if (horizontal) {
        for (int i = 1; i < 5; ++i) {
            result += texture(emissionSampler, textureCoordinates + vec2(texOffset.x * i, 0.0)).rgb * bloomWeights[i];
            result += texture(emissionSampler, textureCoordinates - vec2(texOffset.x * i, 0.0)).rgb * bloomWeights[i];
        }
    } else {
        for (int i = 1; i < 5; ++i) {
            result += texture(emissionSampler, textureCoordinates + vec2(0.0, texOffset.y * i)).rgb * bloomWeights[i];
            result += texture(emissionSampler, textureCoordinates - vec2(0.0, texOffset.y * i)).rgb * bloomWeights[i];
        }
    }

    fragColor = vec4(result, 1.0);
}