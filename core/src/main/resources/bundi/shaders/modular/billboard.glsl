mat4 modelViewMatrix = viewMatrix * modelMatrix;

// the scale is not retained with this method.
//modelViewMatrix[0][0] = 1;
//modelViewMatrix[0][1] = 0;
//modelViewMatrix[0][2] = 0;
//
//modelViewMatrix[1][0] = 0;
//modelViewMatrix[1][1] = 1;
//modelViewMatrix[1][2] = 0;
//
//modelViewMatrix[2][0] = 0;
//modelViewMatrix[2][1] = 0;
//modelViewMatrix[2][2] = 1;

// This is expensive to do!! the length function uses sqrt
modelViewMatrix[0][0] = length(vec3(modelMatrix[0]));
modelViewMatrix[0][1] = 0;
modelViewMatrix[0][2] = 0;

modelViewMatrix[1][0] = 0;
modelViewMatrix[1][1] = length(vec3(modelMatrix[1]));
modelViewMatrix[1][2] = 0;

modelViewMatrix[2][0] = 0;
modelViewMatrix[2][1] = 0;
modelViewMatrix[2][2] = 1;

gl_Position = projectionMatrix * modelViewMatrix * vertexPos;
passFragPos = vec3(modelMatrix * vertexPos);
passViewSpace = modelViewMatrix * vertexPos;