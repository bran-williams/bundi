#version 330

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 textureCoordinates;

out vec3 passFragPos;
out vec2 passTextureCoordinates;
flat out int blockLight;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    passFragPos = vec3(modelMatrix * vec4(position.xyz, 1.0));
    passTextureCoordinates = textureCoordinates;
    blockLight = int(position.w);

    gl_Position = projectionMatrix * viewMatrix * vec4(passFragPos, 1.0);
}