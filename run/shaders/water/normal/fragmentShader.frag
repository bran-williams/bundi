#version 330

#define NUMBERWAVES 4

const float Eta = 0.15; // Water
const float PI = 3.141592654;
const float G = 9.81;

in vec2 passTextureCoordinates;

out vec4 fragColor;

uniform float waterPlaneLength;

uniform float passedTime;

uniform vec4 waveParameters[NUMBERWAVES];
uniform vec2 waveDirections[NUMBERWAVES];

vec3 calcNormal(vec2 uv) {
   vec3 N = vec3(0.0, 0.0, 1.0);
   for (int i = 0; i < NUMBERWAVES; i++) {
       vec2 direction = normalize(waveDirections[i]);
       float speed = waveParameters[i].x;
       float amplitude = waveParameters[i].y;
       float wavelength = waveParameters[i].z;
       float steepness = waveParameters[i].w;

       float frequency = sqrt(G*2.0*PI/wavelength);
       float phase = speed * frequency;

       float alpha = frequency * dot(direction, uv) + phase * passedTime;
       float C = cos(alpha);
       float S = sin(alpha);
       float val = pow(0.5 * (S + 1.0), steepness - 1.0) * C;
       val = frequency * amplitude * steepness * val;
       N += vec3(direction.x * val,
                 direction.y * val,
                 0.0);
   }
   return (normalize(N) * 0.5) + 0.5;
}

vec3 calcNormal1(vec2 uv) {

    vec3 vertex = vec3(uv.s, 0.0, uv.t);

    vec3 P = vertex;

    for (int i = 0; i < NUMBERWAVES; i++) {
        vec2 direction = normalize(waveDirections[i]);
        float speed = waveParameters[i].x;
        float amplitude = waveParameters[i].y;
        float wavelength = waveParameters[i].z;
        float steepness = waveParameters[i].w;

        float frequency = sqrt(G*2.0*PI/wavelength);
        float phase = speed * frequency;
        float alpha = frequency * dot(direction, vertex.xz) + phase * passedTime;

        float C = cos(alpha);
        float S = sin(alpha);

        P.x += steepness * amplitude * direction.x * C;
        P.y += amplitude * S;
        P.z += steepness * amplitude * direction.y * C;
    }

    vec3 N = vec3(0.0, 0.0, 1.0);

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

        N.x += direction.x * wavelength * amplitude * C;
        N.y += steepness * wavelength * amplitude * S;
        N.z += direction.y * wavelength * amplitude * C;
    }

//    N.x = -N.x;
//    N.y = 1.0 - N.y;
//    N.z = -N.z;

    return (normalize(N) * 0.5) + 0.5;
}

void main() {
   vec3 N = calcNormal1(passTextureCoordinates.st);
   fragColor = vec4(N.xyz, 1.0);
}