/**
 TBN calculation from
 http://ogldev.atspace.co.uk/www/tutorial26/tutorial26.html

 normal - the vertex normal
 tangent - the vertex tangent
 bumpMapNormal - the value sampled from the normal texture in the range 0 - 1.
*/
vec3 calculateMappedNormal(vec3 normal, vec3 tangent, vec3 bumpMapNormal) {
    bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);
    tangent = normalize(tangent - dot(tangent, normal) * normal);
    vec3 bitangent = cross(tangent, normal);
    mat3 TBN = mat3(tangent, bitangent, normal);
    return normalize(TBN * bumpMapNormal);
}
