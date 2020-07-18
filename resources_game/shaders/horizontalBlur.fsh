#version 330 core

in vec2 texCoords[11];

out vec4 FragColor;

uniform sampler2D quadTexture;

void main()
{
	FragColor = vec4(0.0);
    
	FragColor += texture(quadTexture, texCoords[0]) * 0.0093;
    FragColor += texture(quadTexture, texCoords[1]) * 0.028002;
    FragColor += texture(quadTexture, texCoords[2]) * 0.065984;
    FragColor += texture(quadTexture, texCoords[3]) * 0.121703;
    FragColor += texture(quadTexture, texCoords[4]) * 0.175713;
    FragColor += texture(quadTexture, texCoords[5]) * 0.198596;
    FragColor += texture(quadTexture, texCoords[6]) * 0.175713;
    FragColor += texture(quadTexture, texCoords[7]) * 0.121703;
    FragColor += texture(quadTexture, texCoords[8]) * 0.065984;
    FragColor += texture(quadTexture, texCoords[9]) * 0.028002;
    FragColor += texture(quadTexture, texCoords[10]) * 0.0093;
}