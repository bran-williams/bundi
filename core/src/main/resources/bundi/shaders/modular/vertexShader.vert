#version 330
//vec4 vertexPos - this is the vertex as vec4

/*vertexlayout*/

/*vertexout*/
out vec4 passViewSpace;
out vec3 passFragPos;

/*vertexuniforms*/
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vertexPos;
    passFragPos = vec3(modelMatrix * vertexPos);
    passViewSpace = viewMatrix * vec4(passFragPos, 1.0);

    /*vertexmain*/
}