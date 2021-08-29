package com.branwilliams.bundi.engine.shader.dynamic;

import com.branwilliams.bundi.engine.util.ShaderUtils;

/**
 * Represents an element that a vertex format can support.
 * @author Brandon
 * @since April 11, 2019
 * */
public enum VertexElements implements VertexElement {
    POSITION("vec3", "position", 3),
    COLOR("vec4", "color", 4),
    UV("vec2", "textureCoordinates", 2),
    NORMAL("vec3", "normal", 3),
    POSITION_2D("vec2", "position", 2),
    TANGENT("vec3", "tangent", 3),
    BITANGENT("vec3", "bitangent", 3);

    private static final String PASS_PREFIX = "pass";

    private final int size;

    private final String type;

    private final String variableName;

    VertexElements(String type, String variableName, int size) {
        this.type = type;
        this.variableName = variableName;
        this.size = size;
    }


    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public String getPassName() {
        return PASS_PREFIX + ShaderUtils.capitalizeFirstChar(variableName);
    }
}
