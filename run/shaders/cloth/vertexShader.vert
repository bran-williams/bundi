#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 textureCoordinates;


out vec3 passFragPos;
out vec3 passNormal;
out vec2 passTextureCoordinates;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    passFragPos = vec3(modelMatrix * vec4(position, 1.0));
    passNormal = mat3(transpose(inverse(modelMatrix))) * normal;

    passTextureCoordinates = textureCoordinates;

    gl_Position = projectionMatrix * viewMatrix * vec4(passFragPos, 1.0);
}