#version 330

in vec3 passPosition;
in vec3 pasTexCoord;
out vec4 fragColor;

uniform vec4 apexColor;
uniform vec4 centerColor;

void main(void){
    // Determine the position on the sky dome where this pixel is located.
    float height = passPosition.y;

    // The value ranges from -1.0f to +1.0f so change it to only positive values.
    if (height < 0.0) {
    	height = 0.0F;
    }

    // Determine the gradient color by interpolating between the apex and center based on the height of the pixel in the sky dome.
    fragColor = mix(centerColor, apexColor, height);

}