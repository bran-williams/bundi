#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoordinates;
layout (location = 2) in vec3 normal;

out vec2 passTextureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform vec4 clippingPlane;
uniform bool hasFakeLighting;

void main() {
    vec4 modelPosition = modelMatrix * vec4(position, 1.0);

    gl_ClipDistance[0] = dot(modelPosition, clippingPlane);

    gl_Position = projectionMatrix * viewMatrix * modelPosition;
    passTextureCoordinates = textureCoordinates;

    surfaceNormal = (modelMatrix * vec4(hasFakeLighting ? vec3(0, 1, 0) : normal, 0)).xyz;
    toLightVector = lightPosition - modelPosition.xyz;
    toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1)).xyz - modelPosition.xyz;
}