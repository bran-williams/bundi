#version 330

in vec3 position;
in vec3 textureCoordinates;

out vec3 passPosition;
out vec3 passTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
    passPosition = position;
    passTexCoord = textureCoordinates;
}