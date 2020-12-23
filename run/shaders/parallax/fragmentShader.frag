#version 330

in vec2 passUV;
in vec4 passColor;

out vec4 fragColor;

uniform sampler2D diffuse;

void main(){
    fragColor = texture(diffuse, passUV) * passColor;
}