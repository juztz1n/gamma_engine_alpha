#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D originalTexture;
uniform sampler2D highlightTexture;

uniform float bloomFactor;
	
void main()
{
	vec2 texcoords = texCoords;
   	//texcoords.x += sin(texcoords.y * 4*2*3.14159 + time*3) / 100;
   	//texcoords.y -= cos(texcoords.x * 4*2*3.14159 + time*3) / 100;

	vec4 original = texture(originalTexture, texcoords);
	vec4 highlight = texture(highlightTexture, texcoords);
	
	FragColor = original + highlight / bloomFactor;
}