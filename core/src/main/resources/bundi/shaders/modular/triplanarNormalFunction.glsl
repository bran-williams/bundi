
vec4 sampleTriplanarNormal(sampler2D textureSampler, vec3 fragPos, vec3 normal, float tile, float blendOffset) {
    vec2 uvX = fragPos.zy * tile;
    vec2 uvY = fragPos.xz * tile;
    vec2 uvZ = fragPos.xy * tile;

    // read texture at uv position of the three projections
    vec4 normalX = texture(textureSampler, uvX);
    vec4 normalY = texture(textureSampler, uvY);
    vec4 normalZ = texture(textureSampler, uvZ);

    vec3 worldNormal = normalize(fragModelMatrix * vec4(normal, 1.0)).xyz;

    //show texture on both sides of the object (positive and negative)
    vec3 weights = abs(worldNormal);
    weights = clamp(weights - blendOffset, 0, 1);
    weights = weights / (weights.x + weights.y + weights.z);

    // Get the sign (-1 or 1) of the surface normal
    vec3 axisSign = sign(worldNormal);

    // Flip tangent normal z to account for surface normal facing
    normalX.z *= axisSign.x;
    normalY.z *= axisSign.y;
    normalZ.z *= axisSign.z;

    // Swizzle tangent normals to match world orientation and triblend
    worldNormal = normalize(normalX.zyx * weights.x
    + normalY.xzy * weights.y
    + normalZ.xyz * weights.z);

    // combine the projected colors
    return vec4(worldNormal, 1.0);
}
