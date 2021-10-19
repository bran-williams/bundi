
vec4 sampleTriplanar(sampler2D textureSampler, vec3 fragPos, vec3 normal, float tile, float blendOffset) {
    vec2 uvX = fragPos.zy * tile;
    vec2 uvY = fragPos.xz * tile;
    vec2 uvZ = fragPos.xy * tile;

    // read texture at uv position of the three projections
    vec4 colX = texture(textureSampler, uvX);
    vec4 colY = texture(textureSampler, uvY);
    vec4 colZ = texture(textureSampler, uvZ);

    vec3 worldNormal = normalize(fragModelMatrix * vec4(normal, 1.0)).xyz;

    //show texture on both sides of the object (positive and negative)
    vec3 weights = abs(worldNormal);
    weights = clamp(weights - blendOffset, 0, 1);
    weights = weights / (weights.x + weights.y + weights.z);

    //combine weights with projected colors
    colX *= weights.z;
    colY *= weights.x;
    colZ *= weights.y;

    // combine the projected colors
    return colX + colY + colZ;
}
