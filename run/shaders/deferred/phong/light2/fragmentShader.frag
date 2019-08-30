#version 330

out vec4 fragColor;

const vec3 fogColor = vec3(0.15, 0.15, 0.15);
const float fogDensity = 0.0025;

uniform sampler2D albedoTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 cameraPosition;
uniform vec2 screenSize;
uniform bool blinn;
uniform bool gamma;

uniform struct Light {
    vec3 position;
    vec3 color;
    float ambiance;

    // variables for attenuation
    float constant;
    float linear;
    float quadratic;
} light;


/*
    Calculates the world-space position from the depth value.
    Credits to: https://stackoverflow.com/a/32246825 for this function.
*/
vec3 worldPosFromViewSpace(vec4 viewSpace) {
    vec4 worldSpacePosition = inverse(viewMatrix) * viewSpace;

    return worldSpacePosition.xyz;
}

vec2 getTextureCoordinates() {
    return gl_FragCoord.xy / screenSize;
}

/**
    Calculates the view-space position of the pixel from the depth value.
*/
vec4 viewSpaceFromDepth(float depth) {
    float z = depth * 2.0 - 1.0;
    vec2 textureCoordinates = getTextureCoordinates();
    vec4 clipSpacePosition = vec4(textureCoordinates * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = inverse(projectionMatrix) * clipSpacePosition;

    // Perspective division
    viewSpacePosition /= viewSpacePosition.w;
    return viewSpacePosition;
}

vec4 calculateLight(Light light, vec3 normal) {
    vec2 textureCoordinates = getTextureCoordinates();
    vec4 colorSample = texture(albedoTexture, textureCoordinates);
    vec4 viewSpace = viewSpaceFromDepth(texture(depthTexture, textureCoordinates).r);
    vec3 position = worldPosFromViewSpace(viewSpace);

    float materialShininess = colorSample.a;

    vec3 color = colorSample.rgb;

    if (gamma) {
        color = pow(color, vec3(2.2));
    }
    // Ambient calculation
    vec3 ambient = light.ambiance * light.color;

    //
    vec3 surfaceToLight = normalize(light.position - position);

    // Diffuse calculation
    float diffuseCoefficient = max(dot(normal, surfaceToLight), 0);
    vec3 diffuse = diffuseCoefficient * light.color;

    // Specular calculation
    float specularCoefficient = 0;
    vec3 surfaceToCamera = normalize(cameraPosition - position);
    if (diffuseCoefficient > 0 && materialShininess > 0) {
        if (blinn) {
            specularCoefficient = pow(max(0.0, dot(normal, normalize(surfaceToLight + surfaceToCamera))), materialShininess * 2);
        } else {
            specularCoefficient = pow(max(0.0, dot(surfaceToCamera, reflect(-surfaceToLight, normal))), materialShininess);
        }
    }
    vec3 specular = specularCoefficient * light.color;

    // Attenuation calculation
    float distanceToLight = length(light.position - position);
    float attenuation = 1.0 / (light.constant + light.linear * distanceToLight + light.quadratic * (distanceToLight * distanceToLight));

    vec3 linearColor = (ambient + attenuation * (diffuse + specular)) * color;

    // fog calculation
    float dist = length(viewSpace);
    float fogFactor = 1.0 /exp(dist * fogDensity);
    fogFactor = clamp( fogFactor, 0.0, 1.0 );
    // mix function fogColor⋅(1−fogFactor) + lightColor⋅fogFactor
    linearColor = mix(fogColor, linearColor, fogFactor);

    return vec4(linearColor, 1.0);
}

void main() {
    vec3 normal = texture(normalTexture, getTextureCoordinates()).xyz;

    if (normal.x == 0 && normal.y == 0 && normal.z == 0) {
        fragColor = texture(albedoTexture, getTextureCoordinates());
    } else {
        fragColor = calculateLight(light, normal);
    }
}