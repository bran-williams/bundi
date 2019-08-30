#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinates;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;

out vec3 passFragPos;
out vec3 passNormal;
out vec2 passTextureCoordinates;
out vec3 passTangent;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    passFragPos = vec3(modelMatrix * vec4(position, 1.0));
//    passNormal  = vec3(modelMatrix * vec4(normal, 0.0));
    passNormal = mat3(transpose(inverse(modelMatrix))) * normal;
    passTangent = vec3(modelMatrix * vec4(tangent, 0.0));
    passTextureCoordinates = textureCoordinates;

    gl_Position = projectionMatrix * viewMatrix * vec4(passFragPos, 1.0);
}