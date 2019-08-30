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
const float tileSize = 64;

/**
 TBN calculation from
 http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html
*/
vec3 calculateMappedNormal() {
    vec3 Normal = normalize(passNormal);
    vec3 Tangent = normalize(passTangent);
    Tangent = normalize(Tangent - dot(Tangent, Normal) * Normal);
    vec3 Bitangent = cross(Tangent, Normal);
    vec3 BumpMapNormal = texture(material.normal, passTextureCoordinates).xyz;
    BumpMapNormal = 2.0 * BumpMapNormal - vec3(1.0, 1.0, 1.0);
    vec3 NewNormal;
    mat3 TBN = mat3(Tangent, Bitangent, Normal);
    NewNormal = TBN * BumpMapNormal;
    NewNormal = normalize(NewNormal);
    return NewNormal;
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
