#version 330

in vec3 passPosition;
in vec4 passViewSpace;

out vec4 fragColor;

struct DirLight {
    vec3 direction;
};

uniform DirLight directionalLight;
uniform vec3 viewPos;
uniform vec4 sunColor;
uniform vec4 skyColor;
uniform float fogDensity;

//
vec4 computeFog(vec3 lightDir, vec3 viewDir, vec4 lightColor, float dist) {
    float fogAmount = 1.0 - exp( -dist * fogDensity );
    float sunAmount = max( dot( viewDir, -lightDir ), 0.0 );
    vec4  fogColor  = mix(skyColor, // bluish
    sunColor, // yellowish
    pow(sunAmount, 64.0) );
    return mix( lightColor, fogColor, fogAmount );
}

// plane based distance.
float computeDist(vec4 viewSpace) {
    return -(viewSpace.z);
}


void main() {
    vec3 viewDir  = normalize(viewPos - passPosition);

    // directional lights direction
    vec3 lightDir = normalize(-directionalLight.direction);

    vec4 lightColor = vec4(0, 0, 0, 1.0);

    //distance
    float dist = computeDist(passViewSpace);

    fragColor = computeFog(lightDir, viewDir, lightColor, dist);

}
