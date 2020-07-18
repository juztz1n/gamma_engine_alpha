#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;

void main()
{
	FragColor = texture(quadTexture, texCoords);
	
	float brightness = dot(FragColor.rgb, vec3(0.299, 0.587, 0.114));
	
	FragColor = FragColor * pow(brightness, 2);
}