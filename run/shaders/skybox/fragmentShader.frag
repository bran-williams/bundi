#version 330

in vec3 passPosition;

out vec4 fragColor;

uniform samplerCube cubeMap;

void main(){
    fragColor = texture(cubeMap, passPosition);
}