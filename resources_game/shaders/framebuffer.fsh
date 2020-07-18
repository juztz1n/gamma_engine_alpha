#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;

void main()
{
	FragColor = texture(quadTexture, texCoords);
    
    float brightness = dot(FragColor.rgb, vec3(0.2126, 0.7152, 0.0722));

}