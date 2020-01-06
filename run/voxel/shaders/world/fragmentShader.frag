#version 330

// constants
const float transparencyThreshold = 0.5;
//const vec4 fogColor = vec4(0.5, 0.5,0.5);
const float FogDensity = 0.025;

in vec3 passFragPos;
in vec3 passNormal;
in vec2 passTextureCoordinates;
in vec3 passTangent;
in vec4 passViewSpace;

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

uniform vec4 sunColor;
uniform vec4 skyColor;
uniform float fogDensity;

//#if FOG_LINEAR
//vec4 computeFog(vec4 lightColor, float dist) {
//    // 20 - fog starts; 80 - fog ends
//    float fogFactor = (80 - dist)/(80 - 20);
//    fogFactor = clamp( fogFactor, 0.0, 1.0 );
//
//    //if you inverse color in glsl mix function you have to
//    //put 1.0 - fogFactor
//    return mix(fogColor, lightColor, fogFactor);
//}
//    #elif FOG_EXPONENTIAL_SQUARED
//vec4 computeFog(vec4 lightColor, float dist) {
//    float fogFactor = 1.0 /exp( (dist * FogDensity)* (dist * FogDensity));
//    fogFactor = clamp( fogFactor, 0.0, 1.0 );
//
//    return mix(fogColor, lightColor, fogFactor);
//}
//    #elif FOG_EXPONENTIAL
//vec4 computeFog(vec4 lightColor, float dist) {
//    float fogFactor = 1.0 / exp(dist * FogDensity);
//    fogFactor = clamp( fogFactor, 0.0, 1.0 );
//
//    // mix function fogColor⋅(1−fogFactor) + lightColor⋅fogFactor
//    return mix(fogColor, lightColor, fogFactor);
//}
//    #endif



//    #if FOG_PLANE_BASED
//float computeDist(vec4 viewSpace) {
//    return abs(viewSpace.z);
//}
//    #else
//float computeDist(vec4 viewSpace) {
//    return length(viewSpace);
//}
//    #endif

//
vec4 computeFog(vec3 lightDir, vec3 viewDir, vec4 lightColor, float dist) {
//    float fogAmount = 1.0 - exp( -dist * FogDensity );
//    vec4  fogColor  = vec4(0.5, 0.6, 0.7, 1.0);
//    return mix( lightColor, fogColor, fogAmount );

    float fogAmount = 1.0 - exp( -dist * fogDensity );
    float sunAmount = max( dot( viewDir, -lightDir ), 0.0 );
    vec4  fogColor  = mix( skyColor,
    sunColor, // yellowish
    pow(sunAmount, 64.0) );
    return mix( lightColor, fogColor, fogAmount );

//    float fogFactor = 1.0 / exp(dist * FogDensity);
//     fogFactor = clamp( fogFactor, 0.0, 1.0 );
//    // mix function fogColor⋅(1−fogFactor) + lightColor⋅fogFactor
//    return mix(fogColor, lightColor, fogFactor);
}

// plane based distance.
float computeDist(vec4 viewSpace) {
    return -(viewSpace.z);
    // return abs(viewSpace.z);
}

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

    vec4 lightColor = vec4(ambient + diffuse + specular + emission, 1.0);

    //distance
    float dist = computeDist(passViewSpace);

    fragColor = computeFog(lightDir, viewDir, lightColor, dist);

}
