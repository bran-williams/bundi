#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;

out vec3 passPosition;
out vec3 passNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
    passPosition = position;
    passNormal = normal;
}