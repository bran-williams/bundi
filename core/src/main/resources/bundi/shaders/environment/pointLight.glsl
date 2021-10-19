// vec3 normal - the normal for this fragment
// vec3 materialDiffuse.rgb - diffuse color of the material
// vec3 materialSpecular.rgb - specular color of the material
// float materialShininess - shininess of the material
// int pointLightCount - number of lights

// point lighting below:
for (int i = 0; i < pointLightCount; i++) {
    PointLight pointLight = pointLights[i];
    vec3 lightDir = normalize(pointLight.position - passFragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    // diffuse calculation
    float diff = max(dot(normal, lightDir), 0.0);

    float distance = length(pointLight.position - passFragPos);
    float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance +
                        pointLight.quadratic * (distance * distance));

    vec3 ambient  = pointLight.ambient  *        materialDiffuse.rgb;
    vec3 diffuse  = pointLight.diffuse  * diff * materialDiffuse.rgb;

    ambient *= attenuation;
    diffuse *= attenuation;

    vec4 lightColor = vec4(ambient + diffuse, 0.0);

    // specular calculation
    float spec = max(dot(normal, halfwayDir), 0.0);
    if (diff > 0 && spec > 0) {
        spec = pow(spec, materialShininess);
        vec3 specular = pointLight.specular * spec * materialSpecular.rgb;
        specular *= attenuation;
        lightColor.r += specular.r;
        lightColor.g += specular.g;
        lightColor.b += specular.b;
    }
    pixelColor += lightColor;
}

