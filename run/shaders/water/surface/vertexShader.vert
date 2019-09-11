#version 330

#define NUMBERWAVES 4

const float PI = 3.141592654;
const float G = 9.81;

layout (location = 0) in vec3 position;

out vec3 incident;
out vec3 bitangent;
out vec3 normal;
out vec3 tangent;
out vec2 texCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat3 inverseViewNormalMatrix;

uniform float passedTime;

uniform float waterPlaneLength;

uniform vec4 waveParameters[NUMBERWAVES];
uniform vec2 waveDirections[NUMBERWAVES];

void main() {
    vec4 P = vec4(position, 1.0);
    vec3 B = vec3(0.0);
    vec3 T = vec3(0.0);
    vec3 N = vec3(0.0);

    for (int i = 0; i < NUMBERWAVES; i++) {
        vec2 direction = normalize(waveDirections[i]);
        float speed = waveParameters[i].x;
        float amplitude = waveParameters[i].y;
        float wavelength = waveParameters[i].z;
        float steepness = waveParameters[i].w;

        float frequency = sqrt(G*2.0*PI/wavelength);
        float phase = speed * frequency;
        float alpha = frequency * dot(direction, position.xz) + phase * passedTime;

        float C = cos(alpha);
        float S = sin(alpha);

        P.x += steepness * amplitude * direction.x * C;
        P.y += amplitude * S;
        P.z += steepness * amplitude * direction.y * C;
    }

    for (int i = 0; i < NUMBERWAVES; i++) {
        vec2 direction = normalize(waveDirections[i]);
        float speed = waveParameters[i].x;
        float amplitude = waveParameters[i].y;
        float wavelength = waveParameters[i].z;
        float steepness = waveParameters[i].w;

        float frequency = sqrt(G*2.0*PI/wavelength);
        float phase = speed * frequency;
        float alpha = frequency * dot(direction, P.xz) + phase * passedTime;

        float C = cos(alpha);
        float S = sin(alpha);

        // x direction
        B.x += steepness * direction.x * direction.x * wavelength * amplitude * S;
        B.y += direction.x * wavelength * amplitude * C;
        B.z += steepness * direction.x * direction.y * wavelength * amplitude * S;

        // y direction
        N.x += direction.x * wavelength * amplitude * C;
        N.y += steepness * wavelength * amplitude * S;
        N.z += direction.y * wavelength * amplitude * C;

        // z direction
        T.x += steepness * direction.x * direction.y * wavelength * amplitude * S;
        T.y += direction.y * wavelength * amplitude * C;
        T.z += steepness * direction.y * direction.y * wavelength * amplitude * S;
    }

    tangent   = normalize(vec3(-T.x, T.y, 1.0 - T.z));
    bitangent = normalize(vec3(1.0 - B.x, B.y, -B.z));
    normal    = normalize(vec3(-N.x, 1.0 - N.y, -N.z));

    vec4 vertex = viewMatrix * modelMatrix * P;

    incident = inverseViewNormalMatrix * vec3(vertex);

    texCoord = position.xz / waterPlaneLength;

    gl_Position = projectionMatrix * vertex;
}