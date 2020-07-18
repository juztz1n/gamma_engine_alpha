#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;

uniform float bloomStrength;

void main()
{
	float brightness = dot(texture(quadTexture, texCoords).rgb, vec3(0.2125, 0.7154, 0.0721));

	FragColor = texture(quadTexture, texCoords) * pow(brightness, 30 / bloomStrength);
}