#version 330

in vec3 passPosition;

out vec4 fragColor;

uniform samplerCube cubeMap;
uniform samplerCube transferCubeMap;
uniform float transferPercentage;

void main(void){
    fragColor = mix(texture(cubeMap, passPosition), texture(transferCubeMap, passPosition), transferPercentage);
}