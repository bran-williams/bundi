#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;

out vec3 posNorm;
out vec3 sunNorm;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 sunPos;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);

    posNorm = normalize(position);

    //Sun pos being a constant vector, we can normalize it in the vshader
    //and pass it to the fshader without having to re-normalize it
    sunNorm = normalize(sunPos);
}