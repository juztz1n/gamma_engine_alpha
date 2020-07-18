#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;

uniform float exposure;
uniform float gamma;

void main()
{
	vec3 color = texture(quadTexture, texCoords).rgb;
  
	vec3 mapped = vec3(1.0) - exp(-color * exposure);
	
	mapped = pow(mapped, vec3(1.0 / gamma));
	    
	FragColor = vec4(mapped, 1.0);
}