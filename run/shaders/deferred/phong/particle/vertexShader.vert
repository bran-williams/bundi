#version 330

layout (location = 0) in vec2 position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main() {
    mat4 modelViewMatrix = viewMatrix * modelMatrix;

    // column 0
    modelViewMatrix[0][0] = 1;
    modelViewMatrix[0][1] = 0;
    modelViewMatrix[0][2] = 0;

    // column 1
    modelViewMatrix[1][0] = 0;
    modelViewMatrix[1][1] = 1;
    modelViewMatrix[1][2] = 0;

    // column 2
    modelViewMatrix[2][0] = 0;
    modelViewMatrix[2][1] = 0;
    modelViewMatrix[2][2] = 1;

    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
//    passTextureCoordinates = position + vec2(0.5);
//    passTextureCoordinates.y = 1.0 - passTextureCoordinates.y;
}