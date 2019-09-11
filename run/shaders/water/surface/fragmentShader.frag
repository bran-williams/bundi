#version 330

const float Eta = 0.15; // Water

const float R_0 = 0.4;
const vec3 oceanblue = vec3(0, 0, 0.2);
const vec3 skyblue = vec3(0.39, 0.52, 0.93) * 0.9;

in vec3 incident;

in vec3 bitangent;
in vec3 normal;
in vec3 tangent;

in vec2 texCoord;

out vec4 fragColor;

uniform samplerCube enviroment;
uniform sampler2D normalMap;
uniform float passedTime;
uniform vec4 waterColor;

vec3 textureToNormal(vec4 orgNormalColor) {
    return normalize(vec3(clamp(orgNormalColor.r*2.0 - 1.0, -1.0, 1.0), clamp(orgNormalColor.g*2.0 - 1.0, -1.0, 1.0), clamp(orgNormalColor.b*2.0 - 1.0, -1.0, 1.0)));
}

void main() {
    // The normals stored in the texture are in object space. No world transformations are yet done.
    vec3 objectNormal = textureToNormal(texture(normalMap, texCoord));

    // These three vectors span a basis depending on the world transformations e.g. the waves.
    mat3 objectToWorldMatrix = mat3(normalize(bitangent), normalize(tangent), normalize(normal));

    vec3 worldNormal = objectToWorldMatrix * objectNormal;

    vec3 worldIncident = normalize(incident);

    vec3 refraction = refract(worldIncident, worldNormal, Eta);
    vec3 reflection = reflect(worldIncident, worldNormal);

    vec4 refractionColor = texture(enviroment, refraction);
    vec4 reflectionColor = texture(enviroment, reflection);

    float fresnel = Eta + (1.0 - Eta) * pow(max(0.0, 1.0 - dot(-worldIncident, worldNormal)), 5.0);

    vec4 color = mix(refractionColor, reflectionColor, fresnel) + waterColor;

    fragColor = color;
}