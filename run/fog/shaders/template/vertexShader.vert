#version 330

/*vslayout*/

/*vsout*/
out vec4 passViewSpace;
out vec3 passFragPos;

/*vsuniforms*/
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    passFragPos = vec3(modelMatrix * vec4(position, 1.0));
    passViewSpace = viewMatrix * vec4(passFragPos, 1.0);

    /*vsmain*/

    #ifdef FOG

    #endif
}