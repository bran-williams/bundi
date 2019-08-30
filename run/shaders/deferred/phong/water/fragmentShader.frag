#version 330

in vec3 passPosition;
in vec2 passTextureCoordinates;
in vec4 clipSpace;

layout (location = 0) out vec4 albedoOut;
layout (location = 1) out vec3 normalOut;

uniform sampler2D reflection;
uniform sampler2D refraction;
uniform sampler2D dudv;
uniform sampler2D normalSampler;
uniform samplerCube environmentMap;
uniform float offset;
uniform vec3 cameraPos;
uniform mat4 modelMatrix;
uniform vec3 waterColor;
uniform float waveStrength;

const float HALF_PI = 1.570796327;

/*
    Stolen from glslify. Used to rotate normal map values so that they're vertical.
*/
mat3 rotation3dX(float angle) {
	float s = sin(angle);
	float c = cos(angle);

	return mat3(
		1.0, 0.0, 0.0,
		0.0, c, s,
		0.0, -s, c
	);
}

void main() {
    vec2 ndc = (clipSpace.xy/clipSpace.w) * 0.5 + 0.5;
    vec2 reflectCoord = vec2(ndc.x, -ndc.y);
    vec2 refractCoord = vec2(ndc.x, ndc.y);

    // Calculate offset from dudv map.
    vec2 offset1 = (texture(dudv, vec2(passTextureCoordinates.x + offset, passTextureCoordinates.y)).rg * 2 - 1) * waveStrength;
    vec2 offset2 = (texture(dudv, vec2(-passTextureCoordinates.x + offset, passTextureCoordinates.y + offset)).rg * 2 - 1) * waveStrength;
    vec2 totalOffset = offset1 + offset2;

    // offset and clamp reflection/refraction texture coordinates.
    reflectCoord += totalOffset;
    reflectCoord.x = clamp(reflectCoord.x, 0.001, 0.999);
    reflectCoord.y = clamp(reflectCoord.y, -0.999, -0.001);

    refractCoord += totalOffset;
    refractCoord = clamp(refractCoord, 0.001, 0.999);

    //float ratio = 1.00 / 1.33;
    //vec3 R = refract(I, normal, ratio);
    // mix(waterColor, texture(environmentMap, R).rgb, 0.5);

    // sample reflection/refraction textures and combine them.
    vec4 reflectionColor = texture(reflection, reflectCoord);
    vec4 refractionColor = texture(refraction, refractCoord);

    // Environment mapping
    vec2 distortedCoords = passTextureCoordinates + totalOffset;
    //distortedCoords = clamp(distortedCoords, 0.001, 0.999);

    vec3 normal = normalize(rotation3dX(-HALF_PI) * normalize(texture(normalSampler, distortedCoords) * 2 - 1).rgb);
    normalOut = normal;
    vec3 I = normalize(passPosition - cameraPos);
    //vec3 R = reflect(I, normal);
    float ratio = 1.00 / 1.33;
    vec3 R = refract(I, normal, ratio);
    vec4 environmentColor = texture(environmentMap, R);

    vec3 toCameraVector = normalize(cameraPos - passPosition);
    float refractiveFactor = dot(toCameraVector, vec3(0, 1, 0));
    vec3 effectsCombined = mix(mix(reflectionColor.rgb, environmentColor.rgb, 0.2), refractionColor.rgb, refractiveFactor);

    // combine the effects with the water color.
    vec3 color = mix(effectsCombined, waterColor, 0.2);


    albedoOut = vec4(color, 0.0);

    //colorOut = texture(waterTexture, distortedCoord);
    // specular component
    //colorOut.a = 0;
}