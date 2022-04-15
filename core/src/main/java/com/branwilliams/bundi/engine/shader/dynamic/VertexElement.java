package com.branwilliams.bundi.engine.shader.dynamic;

import com.branwilliams.bundi.engine.util.ShaderUtils;

/**
 * Denotes the element of a vertex for a model.
 * Examples are:
 * <pre>
 *     3d position = (size=3, type="vec3", variableName="position", passName="passPosition")
 *     texture UV = (size=2, type="vec2", variableName="uv", passName="passUV")
 * <pre/>
 *
 * */
public interface VertexElement {

    String PASS_PREFIX = "pass";

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
    default String getPassName() {
        return PASS_PREFIX + ShaderUtils.capitalizeFirstChar(getVariableName());
    }

}
