#version 330

in vec2 textureCoordinates;

out vec4 fragColor;

uniform sampler2D textureSampler;

//uniform float far;
//uniform float near;
uniform float exposure;

/*float linearizeDepth(float z) {
     float n = near;
     float f = far;
     return (2.0 * n) / (f + n - z * (f - n));
}*/

/**
    Sobel edge detection using the x and y kernels defined above.
*/
/*
const mat3 sx = mat3(
    1.0, 2.0, 1.0,
    0.0, 0.0, 0.0,
   -1.0, -2.0, -1.0
);
const mat3 sy = mat3(
    1.0, 0.0, -1.0,
    2.0, 0.0, -2.0,
    1.0, 0.0, -1.0
);

const vec3 edgeColor = vec3(0, 1, 0);

float detectEdge() {
    mat3 I;
    for (int i=0; i<3; i++) {
        for (int j=0; j<3; j++) {
            float screenSample = linearizeDepth(texture(depthSampler, (gl_FragCoord.xy + vec2(i - 1, j - 1)) / screenSize).r);
            I[i][j] = screenSample;
            //vec3 screenSample = texture(textureSampler, (gl_FragCoord.xy + vec2(i - 1, j - 1)) / screenSize).rgb;
            //I[i][j] = length(screenSample);
        }
    }

    float gx = dot(sx[0], I[0]) + dot(sx[1], I[1]) + dot(sx[2], I[2]);
    float gy = dot(sy[0], I[0]) + dot(sy[1], I[1]) + dot(sy[2], I[2]);

    float g = sqrt(pow(gx, 2.0) + pow(gy, 2.0));

    float minStep = 0.4;
    float maxStep = 0.6;
    return smoothstep(minStep, maxStep, g);
}*/

vec3 grayScale(vec3 color) {
	float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
	return vec3(gray);
}

void main() {
    vec3 texColor = texture(textureSampler, textureCoordinates).rgb;

    // exposure tonemapping
    vec3 sceneColor = vec3(1.0) - exp(-texColor * exposure);

    fragColor = vec4(sceneColor, 1);
    //float pixelDepth = linearizeDepth(texture(depthSampler, textureCoordinates).r);
    //fragColor = vec4(mix(sceneColor, edgeColor, detectEdge()), 1);
}