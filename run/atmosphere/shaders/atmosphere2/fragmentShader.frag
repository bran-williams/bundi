#version 330

in vec3 posNorm;
in vec3 sunNorm;

out vec4 fragColor;

uniform sampler2D tint;//the color of the sky on the half-sphere where the sun is. (time x height)
uniform sampler2D tint2;//the color of the sky on the opposite half-sphere. (time x height)
uniform sampler2D sun;//sun texture (radius x time)
uniform float weather;//mixing factor (0.5 to 1.0)

//---------MAIN------------
void main() {
    vec3 color;

    float dist = dot(sunNorm, posNorm);

    //We read the tint texture according to the position of the sun and the weather factor
    vec3 color_wo_sun = texture(tint2, vec2((sunNorm.y + 1.0) / 2.0,max(0.01,posNorm.y))).rgb;
    vec3 color_w_sun = texture(tint, vec2((sunNorm.y + 1.0) / 2.0,max(0.01,posNorm.y))).rgb;
    color = weather*mix(color_wo_sun,color_w_sun,dist*0.5+0.5);

    //Sun
    float radius = length(posNorm - sunNorm);

    if(radius < 0.05){//We are in the area of the sky which is covered by the sun
        float time = clamp(sunNorm.y,0.01,1);
        radius = radius/0.05;
        if(radius < 1.0-0.001){//< we need a small bias to avoid flickering on the border of the texture
            //We read the alpha value from a texture where x = radius and y=height in the sky (~time)
            vec4 sun_color = texture(sun,vec2(radius,time));
            color = mix(color, sun_color.rgb, sun_color.a);
        }
    }

//    //Moon
//    float radius_moon = length(posNorm+sunNorm);//the moon is at position -sun_pos
//    if(radius_moon < 0.03){//We are in the area of the sky which is covered by the moon
//        //We define a local plane tangent to the skydome at -sun_norm
//        //We work in model space (everything normalized)
//        vec3 n1 = normalize(cross(-sunNorm,vec3(0,1,0)));
//        vec3 n2 = normalize(cross(-sunNorm,n1));
//        //We project pos_norm on this plane
//        float x = dot(posNorm,n1);
//        float y = dot(posNorm,n2);
//        //x,y are two sine, ranging approx from 0 to sqrt(2)*0.03. We scale them to [-1,1], then we will translate to [0,1]
//        float scale = 23.57*0.5;
//        //we need a compensation term because we made projection on the plane and not on the real sphere + other approximations.
//        float compensation = 1.4;
//        //And we read in the texture of the moon. The projection we did previously allows us to have an undeformed moon
//        //(for the sun we didn't care as there are no details on it)
//        color = mix(color, texture(moon,vec2(x,y)*scale*compensation+vec2(0.5)).rgb, clamp(-sunNorm.y*3,0,1));
//    }

    fragColor = vec4(mix(color, vec3(0, 0, 1), 0.25), 1.0);

}