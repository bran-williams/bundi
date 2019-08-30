package com.branwilliams.bundi.engine.shader.dynamic;

/**
 * Represents an element that a vertex format can support.
 * @author Brandon
 * @since April 11, 2019
 * */
public enum VertexElement {
    POSITION("vec3", "position", 3),
    COLOR("vec4", "color", 4),
    UV("vec2", "textureCoordinates", 2),
    NORMAL("vec3", "normal", 3),
    POSITION_2D("vec2", "position", 2),
    TANGENT("vec3", "tangent", 3),
    BITANGENT("vec3", "bitangent", 3);

    public final int size;

    public final String type;

    public final String variableName;

    VertexElement(String type, String variableName, int size) {
        this.type = type;
        this.variableName = variableName;
        this.size = size;
    }

}
