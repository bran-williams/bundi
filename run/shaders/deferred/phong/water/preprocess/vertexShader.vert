#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinates;

out vec2 passTextureCoordinates;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec4 plane;

void main() {
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);

    gl_ClipDistance[0] = dot(worldPosition, plane);

    gl_Position = projectionMatrix * viewMatrix * worldPosition;
    passTextureCoordinates = textureCoordinates;
}