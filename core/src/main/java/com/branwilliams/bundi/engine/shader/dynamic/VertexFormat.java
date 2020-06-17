package com.branwilliams.bundi.engine.shader.dynamic;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the format for a vertex within a mesh.
 * Contains a list of {@link VertexElements VertexElements} which are the different parts of the vertex format (i.e.
 * position, uv, color,...). These VertexElements are stored in a specific order within a list and their indices
 * correspond to the indices of a {@link com.branwilliams.bundi.engine.shader.VertexArrayObject VAO}s attributes.
 *
 * @author Brandon
 * @since April 11, 2019
 */
public class VertexFormat {

    public static final VertexFormat NONE = new VertexFormat();

    public static final VertexFormat POSITION = new VertexFormat(VertexElements.POSITION);

    public static final VertexFormat POSITION_COLOR = new VertexFormat(VertexElements.POSITION, VertexElements.COLOR);

    public static final VertexFormat POSITION_UV = new VertexFormat(VertexElements.POSITION, VertexElements.UV);

    public static final VertexFormat POSITION_NORMAL = new VertexFormat(VertexElements.POSITION, VertexElements.NORMAL);

    public static final VertexFormat POSITION_UV_COLOR = new VertexFormat(VertexElements.POSITION, VertexElements.UV,
            VertexElements.COLOR);

    public static final VertexFormat POSITION_UV_NORMAL = new VertexFormat(VertexElements.POSITION, VertexElements.UV,
            VertexElements.NORMAL);

    public static final VertexFormat POSITION_UV_COLOR_NORMAL = new VertexFormat(VertexElements.POSITION, VertexElements.UV,
            VertexElements.COLOR, VertexElements.NORMAL);

    public static final VertexFormat POSITION_UV_NORMAL_TANGENT = new VertexFormat(VertexElements.POSITION,
            VertexElements.UV, VertexElements.NORMAL, VertexElements.TANGENT);

    public static final VertexFormat POSITION_UV_NORMAL_TANGENT_BITANGENT = new VertexFormat(VertexElements.POSITION,
            VertexElements.UV, VertexElements.NORMAL, VertexElements.TANGENT, VertexElements.BITANGENT);

    public static final VertexFormat POSITION_2D = new VertexFormat(VertexElements.POSITION_2D);

    public static final VertexFormat POSITION_2D_COLOR = new VertexFormat(VertexElements.POSITION_2D, VertexElements.COLOR);

    public static final VertexFormat POSITION_2D_UV = new VertexFormat(VertexElements.POSITION_2D, VertexElements.UV);

    public static final VertexFormat POSITION_2D_UV_COLOR = new VertexFormat(VertexElements.POSITION_2D, VertexElements.UV,
            VertexElements.COLOR);

    /**
     * The list of elements this vertex format contains.
     * */
    private final List<VertexElements> vertexElements;

    /**
     * The total size of the vertex this format represents.
     * */
    private final int elementSize;

    public VertexFormat(List<VertexElements> vertexElements) {
        this.vertexElements = vertexElements;

        elementSize = this.vertexElements.stream()
                .mapToInt(e -> e.getSize())
                .sum();
    }

    public VertexFormat(VertexElements... vertexElements) {
        this(Arrays.asList(vertexElements));
    }

    /**
     * @return True if there are no {@link VertexElements VertexElements} within this vertex format.
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
            offset += vertexElements.get(i).getSize();
        }
        return offset;
    }

    public int getVertexSizeInBytes() {
        int size = 0;
        for (VertexElement vertexElement : this.vertexElements) {
            size += Float.SIZE * vertexElement.getSize();
        }
        return size >> 4;
    }

    /**
     * @return True if this vertex format contains a {@link VertexElements#POSITION} or {@link VertexElements#POSITION_2D}
     * vertex element.
     * */
    public boolean hasPositionElement() {
        return hasElement(VertexElements.POSITION) || hasElement(VertexElements.POSITION_2D);
    }

    /**
     * @return The VertexElement at the index provided.
     * */
    public VertexElements getElement(int index) {
        return vertexElements.get(index);
    }

    /**
     * @return The index for the provided VertexElement.
     * */
    public int getElementIndex(VertexElements vertexElements) {
        return this.vertexElements.indexOf(vertexElements);
    }

    public List<VertexElements> getVertexElements() {
        return vertexElements;
    }

    public boolean hasElement(VertexElements vertexElements) {
        return this.vertexElements.contains(vertexElements);
    }

    public int getElementCount() {
        return vertexElements.size();
    }
}
