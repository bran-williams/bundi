// vec3 normal - the normal for this fragment
// vec3 materialDiffuse.rgb - diffuse color of the material
// vec3 materialSpecular.rgb - specular color of the material
// float materialShininess - shininess of the material
// int dirLightCount - number of lights

// directional lighting below:
for (int i = 0; i < dirLightCount; i++) {
    DirLight dirLight = dirLights[i];
    vec3 lightDir = normalize(-dirLight.direction);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    // diffuse calculation
    float diff = max(dot(normal, lightDir), 0.0);

    vec3 ambient  = dirLight.ambient  *        materialDiffuse.rgb;
    vec3 diffuse  = dirLight.diffuse  * diff * materialDiffuse.rgb;

    vec4 lightColor = vec4(ambient + diffuse, 0.0);

    // specular calculation
    float spec = max(dot(normal, halfwayDir), 0.0);
    if (diff > 0 && spec > 0) {
        spec = pow(spec, materialShininess);
        vec3 specular = dirLight.specular * spec * materialSpecular.rgb;
        lightColor.r += specular.r;
        lightColor.g += specular.g;
        lightColor.b += specular.b;
    }
    pixelColor += lightColor;
}

