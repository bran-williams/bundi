#version 330

in vec2 textureCoordinates;

out vec4 fragColor;

const int MAX_LIGHTS = 16;
const float PI = 3.14159265359;

uniform sampler2D albedoTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 cameraPosition;
uniform vec2 screenSize;
uniform int numLights;

uniform struct Light {
    vec3 position;
    vec3 color;
    float ambiance;

    // variables for attenuation
    float constant;
    float linear;
    float quadratic;
} lights[MAX_LIGHTS];


/*
    Calculates the world-space position from the depth value.
    Credits to: https://stackoverflow.com/a/32246825 for this function.
*/
vec3 worldPosFromDepth(float depth) {
    float z = depth * 2.0 - 1.0;

    vec4 clipSpacePosition = vec4(textureCoordinates * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = inverse(projectionMatrix) * clipSpacePosition;

    // Perspective division
    viewSpacePosition /= viewSpacePosition.w;

    vec4 worldSpacePosition = inverse(viewMatrix) * viewSpacePosition;

    return worldSpacePosition.xyz;
}

// ----------------------------------------------------------------------------
float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0, 1), 5.0);
}
// ----------------------------------------------------------------------------


vec3 calculateLight(Light light, vec3 position, vec3 albedo, float metallic, vec3 N, float roughness) {
    vec3 V = normalize(cameraPosition - position);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);

    // calculate per-light radiance
    vec3 L = normalize(light.position - position);
    vec3 H = normalize(V + L);
    float distance = length(light.position - position);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    vec3 radiance = light.color * attenuation;

    // Cook-Torrance BRDF
    float NDF = DistributionGGX(N, H, roughness);
    float G = GeometrySmith(N, V, L, roughness);
    vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

    vec3 numerator = NDF * G * F;
    float denominator = 4 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
    vec3 specular = numerator / max(denominator, 0.001);  // 0.001 to prevent divide by zero.

    // kS is equal to Fresnel
    vec3 kS = F;
    // for energy conservation, the diffuse and specular light can't
    // be above 1.0 (unless the surface emits light); to preserve this
    // relationship the diffuse component (kD) should equal 1.0 - kS.
    vec3 kD = vec3(1.0) - kS;
    // multiply kD by the inverse metalness such that only non-metals
    // have diffuse lighting, or a linear blend if partly metal (pure metals
    // have no diffuse light).
    kD *= 1.0 - metallic;

    // scale light by NdotL
    float NdotL = max(dot(N, L), 0.0);

    return (kD * albedo / PI + specular) * radiance * NdotL;  // note that we already multiplied the BRDF by the Fresnel (kS) so we won't multiply by kS again
}

void main() {
    vec3 position = worldPosFromDepth(texture(depthTexture, textureCoordinates).r);

    // Albedo and metallic are stored in the albedo texture.
    vec4 colorSample = texture(albedoTexture, textureCoordinates);
    vec3 albedo = colorSample.rgb;
    albedo = pow(albedo, vec3(2.2));
    float metallic = colorSample.a;

    // Normal vector and roughness are stored in the normal texture.
    vec4 normalSample = texture(normalTexture, textureCoordinates);
    vec3 normal = normalSample.rgb;
    float roughness = normalSample.a;

    vec3 Lo = vec3(0);
    for (int i = 0; i < numLights; i++) {
        Lo += calculateLight(lights[i], position, albedo, metallic, normal, roughness);
    }

    vec3 ambient = vec3(0.03) * albedo; // * ao;
    vec3 color = ambient + Lo;

    // HDR tonemapping
    color = color / (color + vec3(1.0));
    // gamma correct
    color = pow(color, vec3(1.0/2.2));

    fragColor = vec4(color, 1.0);
}