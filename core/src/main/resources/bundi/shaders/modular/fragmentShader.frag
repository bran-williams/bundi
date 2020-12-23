#version 330

/*defines*/

in vec4 passViewSpace;
in vec3 passFragPos;
/*fragin*/

out vec4 fragColor;

#ifdef FOG
uniform vec4 fogColor;
uniform float fogDensity;

vec4 computeFog(vec4 pixelColor, float dist) {
    float fogAmount = 1.0 - exp( -dist * fogDensity );
    return mix( pixelColor, fogColor, fogAmount );
}

// plane based distance.
float computeDist(vec4 viewSpace) {
    return -(viewSpace.z);
}
#endif

/*material*/
/*fraguniforms*/

void main() {
    vec4 pixelColor = vec4(0, 0, 0, 1);

    /*fragcolor*/

    #ifdef FOG
    pixelColor = computeFog(pixelColor, computeDist(passViewSpace));
    #endif

    fragColor = pixelColor;
}