#version 330 core

in vec2 Position;

out vec2 texCoords[11];

uniform float BlurWidth;

void main()
{
	gl_Position = vec4(Position, 0.0, 1.0);
	
	vec2 center = Position * 0.5 + 0.5;
	float pixelSize = 1.0 / (BlurWidth);
	
	for(int i = -5; i < 5; i++)
	{
		texCoords[i+5] = center + vec2(pixelSize * i, 0.0);
	}
}