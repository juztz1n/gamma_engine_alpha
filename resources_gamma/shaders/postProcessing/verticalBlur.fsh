#version 330 core

in vec2 texCoords[11];

out vec4 FragColor;

uniform sampler2D quadTexture;

const float kernel[11] = float[] ( 0.00093, 0.028002, 0.065984, 0.121703, 0.175713, 0.198596, 0.175713, 0.121703, 0.065984, 0.028002, 0.00093 );

void main()
{
	FragColor = vec4(0.0);
    
    for (int i = 0; i < 11; i++)
    	FragColor += texture(quadTexture, texCoords[i]) * kernel[i];
    for (int i = 0; i < 11; i++)
    	FragColor += texture(quadTexture, texCoords[i]) * kernel[i];
}