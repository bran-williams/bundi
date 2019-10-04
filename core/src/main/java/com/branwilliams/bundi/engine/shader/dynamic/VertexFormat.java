package com.branwilliams.bundi.engine.shader.dynamic;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the format for a vertex within a mesh.
 * Contains a list of {@link VertexElement VertexElements} which are the different elements of a vertex.
 * These VertexElements are stored in a specific order within a list and their indices correspond to the indices of the
 * {@link com.branwilliams.bundi.engine.shader.VertexBufferObject VBOs} for each element within some
 * {@link com.branwilliams.bundi.engine.shader.VertexArrayObject VAO}.
 *
 * @author Brandon
 * @since April 11, 2019
 */
public class VertexFormat {

    public static final VertexFormat NONE = new VertexFormat();

    public static final VertexFormat POSITION_UV_COLOR = new VertexFormat(VertexElement.POSITION, VertexElement.UV,
            VertexElement.COLOR);

    public static final VertexFormat POSITION_COLOR = new VertexFormat(VertexElement.POSITION, VertexElement.COLOR);

    public static final VertexFormat POSITION_UV = new VertexFormat(VertexElement.POSITION, VertexElement.UV);

    public static final VertexFormat POSITION_UV_NORMAL = new VertexFormat(VertexElement.POSITION, VertexElement.UV,
            VertexElement.NORMAL);

    public static final VertexFormat POSITION_NORMAL = new VertexFormat(VertexElement.POSITION, VertexElement.NORMAL);

    public static final VertexFormat POSITION = new VertexFormat(VertexElement.POSITION);

    public static final VertexFormat POSITION_2D = new VertexFormat(VertexElement.POSITION_2D);

    public static final VertexFormat POSITION_UV_NORMAL_TANGENT_BITANGENT = new VertexFormat(VertexElement.POSITION,
            VertexElement.UV, VertexElement.NORMAL, VertexElement.TANGENT, VertexElement.BITANGENT);

    public static final VertexFormat POSITION_2D_UV_COLOR = new VertexFormat(VertexElement.POSITION_2D, VertexElement.UV,
            VertexElement.COLOR);

    public static final VertexFormat POSITION_2D_COLOR = new VertexFormat(VertexElement.POSITION_2D, VertexElement.COLOR);

    public static final VertexFormat POSITION_2D_UV = new VertexFormat(VertexElement.POSITION_2D, VertexElement.UV);


    /**
     * The list of elements this vertex format contains.
     * */
    private final List<VertexElement> vertexElements;

    /**
     * The total size of the vertex this format represents.
     * */
    private final int elementSize;

    public VertexFormat(VertexElement... vertexElements) {
        this.vertexElements = Arrays.asList(vertexElements);

        elementSize = this.vertexElements.stream()
                .mapToInt(e -> e.size)
                .sum();
    }

    /**
     * @return True if there are no {@link VertexElement VertexElements} within this vertex format.
     * */
    public boolean isEmpty() {
        return elementSize == 0;
    }

    /**
     * @return The total size of the vertex this format represents.
     * */
    public int getElementSize() {
        return elementSize;
    }

    /**
     * @return The offset for the vertex element at the index provided.
     * */
    public int getElementOffset(int index) {
        int offset = 0;
        for (int i = 0; i < index && i < vertexElements.size(); i++) {
            offset += vertexElements.get(i).size;
        }
        return offset;
    }

    /**
     * @return True if this vertex format contains a {@link VertexElement#POSITION} or {@link VertexElement#POSITION_2D}
     * vertex element.
     * */
    public boolean hasPositionElement() {
        return hasElement(VertexElement.POSITION) || hasElement(VertexElement.POSITION_2D);
    }

    /**
     * @return The VertexElement at the index provided.
     * */
    public VertexElement getElement(int index) {
        return vertexElements.get(index);
    }

    /**
     * @return The index for the provided VertexElement.
     * */
    public int getElementIndex(VertexElement vertexElement) {
        return vertexElements.indexOf(vertexElement);
    }

    public List<VertexElement> getVertexElements() {
        return vertexElements;
    }

    public boolean hasElement(VertexElement vertexElement) {
        return vertexElements.contains(vertexElement);
    }

    public int getElementCount() {
        return vertexElements.size();
    }
}
