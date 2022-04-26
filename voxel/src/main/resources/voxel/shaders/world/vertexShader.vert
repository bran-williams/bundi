#version 330

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 textureCoordinates;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;

out vec3 passFragPos;
out vec2 passTextureCoordinates;
out vec3 passNormal;
out vec3 passTangent;

flat out int blockLight;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    passFragPos = vec3(modelMatrix * vec4(position.xyz, 1.0));
    passTextureCoordinates = textureCoordinates;
    passNormal = mat3(transpose(inverse(modelMatrix))) * normal;
    passTangent = tangent;
    blockLight = int(position.w);

    gl_Position = projectionMatrix * viewMatrix * vec4(passFragPos, 1.0);
}