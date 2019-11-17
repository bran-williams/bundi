#version 330

in vec3 position;

out vec3 passPosition;

void main() {
    gl_Position = vec4(position, 1.0);
    passPosition = position;
}