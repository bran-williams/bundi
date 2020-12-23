#version 330

layout (location = 0) in vec2 position;

// xy is position and z is spriteIndex
layout (location = 1) in vec3 instanceData;

out vec3 uvSpriteIndex;

// size of sprite (scaled width and height)
uniform vec2 spriteSize;

uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main() {
    vec2 worldPosition = instanceData.xy + (position * spriteSize);
    gl_Position = projectionMatrix * modelMatrix * vec4(worldPosition, 0.0, 1.0);

    uvSpriteIndex = vec3(position, instanceData.z);
}