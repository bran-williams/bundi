package com.branwilliams.bundi.engine.shader.dynamic;

import com.branwilliams.bundi.engine.util.ShaderUtils;

import java.util.Arrays;
import java.util.Iterator;
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
public class VertexFormat <VertexElementType extends VertexElement> implements Iterable<VertexElementType> {

    public static final VertexFormat<VertexElements> NONE = new VertexFormat<>();

    public static final VertexFormat<VertexElements> POSITION = new VertexFormat<>(VertexElements.POSITION);

    public static final VertexFormat<VertexElements> POSITION_COLOR = new VertexFormat<>(VertexElements.POSITION, VertexElements.COLOR);

    public static final VertexFormat<VertexElements> POSITION_UV = new VertexFormat<>(VertexElements.POSITION, VertexElements.UV);

    public static final VertexFormat<VertexElements> POSITION_NORMAL = new VertexFormat<>(VertexElements.POSITION, VertexElements.NORMAL);

    public static final VertexFormat<VertexElements> POSITION_UV_COLOR = new VertexFormat<>(VertexElements.POSITION, VertexElements.UV,
            VertexElements.COLOR);

    public static final VertexFormat<VertexElements> POSITION_UV_NORMAL = new VertexFormat<>(VertexElements.POSITION, VertexElements.UV,
            VertexElements.NORMAL);

    public static final VertexFormat<VertexElements> POSITION_NORMAL_UV = new VertexFormat<>(VertexElements.POSITION,
            VertexElements.NORMAL, VertexElements.UV);

    public static final VertexFormat<VertexElements> POSITION_UV_COLOR_NORMAL = new VertexFormat<>(VertexElements.POSITION, VertexElements.UV,
            VertexElements.COLOR, VertexElements.NORMAL);

    public static final VertexFormat<VertexElements> POSITION_UV_NORMAL_TANGENT = new VertexFormat<>(VertexElements.POSITION,
            VertexElements.UV, VertexElements.NORMAL, VertexElements.TANGENT);

    public static final VertexFormat<VertexElements> POSITION_UV_NORMAL_TANGENT_BITANGENT = new VertexFormat<>(VertexElements.POSITION,
            VertexElements.UV, VertexElements.NORMAL, VertexElements.TANGENT, VertexElements.BITANGENT);

    public static final VertexFormat<VertexElements> POSITION_2D = new VertexFormat<>(VertexElements.POSITION_2D);

    public static final VertexFormat<VertexElements> POSITION_2D_COLOR = new VertexFormat<>(VertexElements.POSITION_2D, VertexElements.COLOR);

    public static final VertexFormat<VertexElements> POSITION_2D_UV = new VertexFormat<>(VertexElements.POSITION_2D, VertexElements.UV);

    public static final VertexFormat<VertexElements> POSITION_2D_UV_COLOR = new VertexFormat<>(VertexElements.POSITION_2D, VertexElements.UV, VertexElements.COLOR);

    public static final VertexFormat<VertexElements> POSITION_2D_NORMAL = new VertexFormat<>(VertexElements.POSITION_2D, VertexElements.NORMAL);

    public static final VertexFormat<VertexElements> POSITION_2D_UV_NORMAL = new VertexFormat<>(VertexElements.POSITION_2D, VertexElements.UV, VertexElements.NORMAL);

    /**
     * The list of elements this vertex format contains.
     * */
    private final List<VertexElementType> vertexElements;

    /**
     * The total size of the vertex this format represents.
     * */
    private final int elementSize;

    public VertexFormat(List<VertexElementType> vertexElements) {
        this.vertexElements = vertexElements;

        elementSize = this.vertexElements.stream()
                .mapToInt(e -> e.getSize())
                .sum();
    }

    public VertexFormat(VertexElementType... vertexElements) {
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
    public VertexElement getElement(int index) {
        return vertexElements.get(index);
    }

    /**
     * @return The index for the provided VertexElement.
     * */
    public int getElementIndex(VertexElement vertexElement) {
        return this.vertexElements.indexOf(vertexElement);
    }

    public List<VertexElementType> getVertexElements() {
        return vertexElements;
    }

    public boolean hasElement(VertexElement vertexElement) {
        return this.vertexElements.contains(vertexElement);
    }

    public int getElementCount() {
        return vertexElements.size();
    }

    /**
     * Creates the GLSL input from a {@link VertexFormat}.
     * */
    public String toVertexShaderInputGLSL() {
        StringBuilder vertexShaderInput = new StringBuilder();
        for (int i = 0; i < vertexElements.size(); i++) {
            VertexElement vertexElement = vertexElements.get(i);
            vertexShaderInput.append(ShaderUtils.createLayout(i, "in "
                    + vertexElement.getType()
                    + " "
                    + vertexElement.getVariableName()
                    + ";"));
        }
        return vertexShaderInput.toString();
    }

    @Override
    public Iterator<VertexElementType> iterator() {
        return vertexElements.iterator();
    }
}
