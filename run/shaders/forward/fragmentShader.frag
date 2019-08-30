#version 330

in vec2 passTextureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

// Texture sampler
uniform sampler2D textureSampler;

// Lighting variables
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;
uniform vec4 color;

// Cel shading variables
uniform bool celShaded;
const float levels = 4.0;

void main() {
    vec4 textureColor = texture(textureSampler, passTextureCoordinates);

    // Allow transparency
    if (textureColor.a < 0.5) {
        discard;
    }

    // Normalized vectors
    vec3 surfaceNormalNorm = normalize(surfaceNormal);
    vec3 toLightVectorNorm = normalize(toLightVector);

    // * lighting related variables
    float brightness = max(dot(surfaceNormalNorm, toLightVectorNorm), 0.2);

    // Cel shaded look
    if (celShaded) {
        float level = floor(brightness * levels);
        brightness = max(level / levels, 0.15);
    }

    // * lighting related variables
    vec3 diffuse = brightness * lightColor;

    // Specular lighting is calculated here, using the above variables*
    float specularFactor = max(dot(reflect(-toLightVectorNorm, surfaceNormalNorm), normalize(toCameraVector)), 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);

    // Cel shaded look
    if (celShaded) {
        float level = floor(dampedFactor * levels);
        dampedFactor = level / levels;
    }

    vec3 specularColor = dampedFactor * reflectivity * lightColor;

    fragColor = vec4(diffuse, 1.0) * color * textureColor + vec4(specularColor, 1.0);
}