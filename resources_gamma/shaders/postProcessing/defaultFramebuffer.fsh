#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;
uniform float ScreenWidth;
uniform float ScreenHeight;

void main()
{
	FragColor = texture(quadTexture, texCoords);
}