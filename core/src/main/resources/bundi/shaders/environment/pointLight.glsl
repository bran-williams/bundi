    // point lighting below:
    for (int i = 0; i < light_count; i++) {
        PointLight pointLight = pointLights[i];
        vec3 lightDir = normalize(pointLight.position - passFragPos);
        vec3 halfwayDir = normalize(lightDir + viewDir);

        // diffuse calculation
        float diff = max(dot(normal, lightDir), 0.0);

        // specular calculation
        float spec = pow(max(dot(normal, halfwayDir), 0.0),     material_shininess);

        float distance = length(pointLight.position - passFragPos);
        float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance +
                            pointLight.quadratic * (distance * distance));

        vec3 ambient  = pointLight.ambient  *        material_diffuse;
        vec3 diffuse  = pointLight.diffuse  * diff * material_diffuse;
        vec3 specular = pointLight.specular * spec * material_specular;

        ambient *= attenuation;
        diffuse *= attenuation;
        specular *= attenuation;

        vec4 lightColor = vec4(ambient + diffuse + specular, 0.0);
        pixelColor += lightColor;
    }

