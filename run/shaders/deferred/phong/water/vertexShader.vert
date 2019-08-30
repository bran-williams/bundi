#version 330

layout (location = 0) in vec2 position;

out vec3 passPosition;
out vec2 passTextureCoordinates;
out vec4 clipSpace;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float tiling;

void main() {
    vec4 modelPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);

    passPosition = modelPosition.xyz;
    passTextureCoordinates = (position * 0.5F + 0.5F) * tiling;

    clipSpace = projectionMatrix * viewMatrix * modelPosition;
    gl_Position = clipSpace;
}