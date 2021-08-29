package com.branwilliams.bundi.engine.shader.modular;

public class ModularShaderConstants {

    /**
     * ************************************************************************
     * These are the comments expected be patched by some modules
     * ************************************************************************
     * */

    public static final String DEFINES_COMMENT = "defines";
    public static final String VERTEX_LAYOUT_COMMENT = "vertexlayout";
    public static final String VERTEX_OUT_COMMENT = "vertexout";
    public static final String VERTEX_MAIN_COMMENT = "vertexmain";
    public static final String FRAG_IN_COMMENT = "fragin";
    public static final String MATERIAL_STRUCT_COMMENT = "materialstruct";
    public static final String FRAG_UNIFORMS_COMMENT = "fraguniforms";
    public static final String FRAG_FUNCTIONS_COMMENT = "fragfunctions";
    public static final String FRAG_COLOR_COMMENT = "fragcolor";

    /**
     * ************************************************************************
     * These are the locations of the resources used by the modular shader program.
     * ************************************************************************
     * */

    public static final String VERTEX_SHADER_LOCATION = "bundi/shaders/modular/vertexShader.vert";
    public static final String FRAGMENT_SHADER_LOCATION = "bundi/shaders/modular/fragmentShader.frag";
    public static final String NORMAL_FUNCTION_LOCATION = "bundi/shaders/modular/normalFunction.glsl";

    public static final String DEFAULT_MATERIAL_NAME = "material";
}
