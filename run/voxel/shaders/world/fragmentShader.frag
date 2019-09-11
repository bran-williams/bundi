#version 330

in vec3 passFragPos;
in vec3 passNormal;
in vec2 passTextureCoordinates;
in vec3 passTangent;

out vec4 fragColor;

struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    sampler2D normal;
    sampler2D emission;
    float shininess;
};

uniform Material material;
uniform DirLight directionalLight;
uniform vec3 viewPos;

// constants
const float transparencyThreshold = 0.5;

/**
 TBN calculation from
 http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
*/
vec3 calculateMappedNormal() {
    vec3 normal = normalize(passNormal);
    vec3 tangent = normalize(passTangent);
    tangent = normalize(tangent - dot(tangent, normal) * normal);
    vec3 bitangent = cross(tangent, normal);
    vec3 bumpMapNormal = texture(material.normal, passTextureCoordinates).xyz;
    bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);
    vec3 newNormal;
    mat3 TBN = mat3(tangent, bitangent, normal);
    newNormal = TBN * bumpMapNormal;
    newNormal = normalize(newNormal);
    return newNormal;
}

void main() {
    vec4 textureColor = texture(material.diffuse, passTextureCoordinates);

    // Allow transparency
    if (textureColor.a < transparencyThreshold) {
        discard;
    }

    vec3 normal   = calculateMappedNormal();
    vec3 viewDir  = normalize(viewPos - passFragPos);

    // directional lighting below:
    vec3 lightDir = normalize(-directionalLight.direction);

    // diffuse calculation
    float diff = max(dot(normal, lightDir), 0.0);

    // specular calculation
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    vec3 ambient  = directionalLight.ambient  *        textureColor.rgb;
    vec3 diffuse  = directionalLight.diffuse  * diff * textureColor.rgb;
    vec3 specular = directionalLight.specular * spec * texture(material.specular, passTextureCoordinates).rgb;

    vec3 emission = texture(material.emission, passTextureCoordinates).rgb;

    fragColor = vec4(ambient + diffuse + specular + emission, 1.0);
}
