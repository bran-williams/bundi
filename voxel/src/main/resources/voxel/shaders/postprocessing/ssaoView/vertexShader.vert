#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;

out vec2 textureCoordinates;

uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * vec4(position, 1.0);
    textureCoordinates = uv;
}