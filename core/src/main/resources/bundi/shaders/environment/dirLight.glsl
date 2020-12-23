    // directional lighting below:
    for (int i = 0; i < light_count; i++) {
        DirLight dirLight = dirLights[i];
        vec3 lightDir = normalize(-dirLight.direction);
        vec3 halfwayDir = normalize(lightDir + viewDir);

        // diffuse calculation
        float diff = max(dot(normal, lightDir), 0.0);

        // specular calculation
        float spec = pow(max(dot(normal, halfwayDir), 0.0),     material_shininess);

        vec3 ambient  = dirLight.ambient  *        material_diffuse;
        vec3 diffuse  = dirLight.diffuse  * diff * material_diffuse;
        vec3 specular = dirLight.specular * spec * material_specular;

        vec4 lightColor = vec4(ambient + diffuse + specular, 0.0);
        pixelColor += lightColor;
    }

