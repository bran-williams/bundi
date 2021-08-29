#version 330


in vec3 passFragPos;
in vec3 passNormal;
in vec2 passTextureCoordinates;

out vec4 fragColor;



struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform DirLight directionalLight;
uniform sampler2D diffuse;

uniform vec3 viewPos;

const vec4 specular = vec4(1, 1, 1, 1.0);
const float shininess = 16;

const float transparencyThreshold = 0.1;

void main() {
    vec4 textureColor = texture(diffuse, passTextureCoordinates);

    // Allow transparency
    if (textureColor.a < transparencyThreshold) {
        discard;
    }

    vec3 normal   = normalize(passNormal);
    vec3 viewDir  = normalize(viewPos - passFragPos);

    // directional lighting below:
    vec3 lightDir = normalize(-directionalLight.direction);

    // diffuse calculation
    float diff = max(dot(normal, lightDir), 0.0);

    // specular calculation
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    vec3 ambient  = directionalLight.ambient  *        textureColor.rgb;
    vec3 diffuse  = directionalLight.diffuse  * diff * textureColor.rgb;
    vec3 specular = directionalLight.specular * spec * specular.rgb;

    vec3 emission = vec3(0); // texture(material.emission, passTextureCoordinates).rgb;

    fragColor = vec4(ambient + diffuse + specular + emission, 1.0);
}