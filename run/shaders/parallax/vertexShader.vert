#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec4 color;

out vec2 passUV;
out vec4 passColor;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform vec2 objectSize;
uniform vec2 offsetUV;

void main() {
    gl_Position = projectionMatrix * modelMatrix * vec4(position.xy * objectSize, position.z, 1.0);
    passUV = uv + offsetUV;
    passColor = color;
}