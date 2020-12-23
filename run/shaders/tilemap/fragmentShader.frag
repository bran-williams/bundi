#version 330

in vec3 uvSpriteIndex;

out vec4 fragColor;

uniform sampler2DArray diffuse;

void main() {
    fragColor = texture(diffuse, uvSpriteIndex);
}