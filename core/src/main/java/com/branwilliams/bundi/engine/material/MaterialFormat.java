package com.branwilliams.bundi.engine.material;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brandon
 * @since September 17, 2019
 */
public class MaterialFormat {

    public static final int NO_TEXTURE_INDEX = -1;

    public static MaterialFormat DIFFUSE_NORMAL_SPECULAR = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.SAMPLER_2D, "diffuse").addElement(MaterialElement.NORMAL,
            MaterialElementType.SAMPLER_2D, "normal").addElement(MaterialElement.SPECULAR,
            MaterialElementType.SAMPLER_2D, "specular");

    public static MaterialFormat DIFFUSE_SPECULAR = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.SAMPLER_2D, "diffuse").addElement(MaterialElement.SPECULAR,
            MaterialElementType.SAMPLER_2D, "specular");

    public static MaterialFormat DIFFUSE_SPECULAR_EMISSIVE = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.SAMPLER_2D, "diffuse").addElement(MaterialElement.SPECULAR,
            MaterialElementType.SAMPLER_2D, "specular").addElement(MaterialElement.EMISSIVE,
            MaterialElementType.SAMPLER_2D, "emissive");


    public static MaterialFormat DIFFUSE_SAMPLER2D = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.SAMPLER_2D, "diffuse");

    public static MaterialFormat DIFFUSE_VEC4 = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.VEC4, "diffuse");


    public static MaterialFormat DIFFUSE_VEC4_SPECULAR_VEC4 = new MaterialFormat().addElement(MaterialElement.DIFFUSE,
            MaterialElementType.VEC4, "diffuse").addElement(MaterialElement.SPECULAR,
            MaterialElementType.VEC4, "specular");
    private final Map<MaterialElement, MaterialEntry> elements;

    /**
     * Count the texture entries in order to determine their indices.
     * */
    private int textureCount = 0;

    public MaterialFormat() {
        this.elements = new HashMap<>();
    }

    public <T> MaterialFormat addElement(MaterialElement materialElement, MaterialElementType materialElementType,
                                         String variableName) {
        if (elements.containsKey(materialElement)) {
            throw new IllegalArgumentException(materialElement.name() + " is already specified!");
        }

        int index = NO_TEXTURE_INDEX;

        if (materialElementType.isTexture) {
            index = textureCount;
            textureCount++;
        }

        elements.put(materialElement, new MaterialEntry(materialElement, materialElementType, variableName, index));

        return this;
    }

    public String toGLSLUniform(String materialName) {
        String uniform = "struct Material { \n";
        for (MaterialEntry entry : elements.values()) {
            uniform += "    " + entry.elementType.glslType + " " + entry.variableName + ";\n";
        }
        uniform += "}; \n" +
                "uniform Material " + materialName + ";\n";
        return uniform;
    }

    public boolean hasElement(MaterialElement materialElement) {
        return elements.containsKey(materialElement);
    }

    public MaterialEntry getElement(MaterialElement element) {
        return elements.getOrDefault(element, null);
    }

    public Map<MaterialElement, MaterialEntry> getElements() {
        return elements;
    }

    public static MaterialFormat none() {
        return EmptyMaterialFormat.instance;
    }

    public static class MaterialEntry {

        public final MaterialElement element;

        public final MaterialElementType elementType;

        public final String variableName;

        public final int textureIndex;

        public MaterialEntry(MaterialElement element, MaterialElementType elementType, String variableName,
                             int textureIndex) {
            this.element = element;
            this.elementType = elementType;
            this.variableName = variableName;
            this.textureIndex = textureIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MaterialEntry that = (MaterialEntry) o;
            return textureIndex == that.textureIndex &&
                    element == that.element &&
                    elementType == that.elementType &&
                    Objects.equals(variableName, that.variableName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, elementType, variableName, textureIndex);
        }

        @Override
        public String toString() {
            return "MaterialEntry{" +
                    "element=" + element +
                    ", elementType=" + elementType +
                    ", variableName='" + variableName + '\'' +
                    ", textureIndex=" + textureIndex +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialFormat that = (MaterialFormat) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return "MaterialFormat{" +
                "elements=" + elements +
                ", textureCount=" + textureCount +
                '}';
    }

    private static class EmptyMaterialFormat extends MaterialFormat {

        private static final EmptyMaterialFormat instance = new EmptyMaterialFormat();

        private EmptyMaterialFormat() {
            super();
        }

        @Override
        public String toGLSLUniform(String materialName) {
            return "";

        }

        @Override
        public <T> MaterialFormat addElement(MaterialElement materialElement, MaterialElementType materialElementType,
                                             String variableName) {
            return this;
        }

        @Override
        public MaterialEntry getElement(MaterialElement element) {
            return null;
        }

        @Override
        public Map<MaterialElement, MaterialEntry> getElements() {
            return new HashMap<>();
        }
    }
}
