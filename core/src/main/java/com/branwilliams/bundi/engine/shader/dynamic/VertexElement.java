package com.branwilliams.bundi.engine.shader.dynamic;

public interface VertexElement {

    /**
     * The size of this element.
     * */
    int getSize();

    /**
     * The GLSL Type for this element.
     * */
    String getType();

    /**
     * The variable name given to this element.
     * */
    String getVariableName();

    /**
     * The name of this vertex element as it's passed from the vertexShader to the next
     * */
    String getPassName();
}
