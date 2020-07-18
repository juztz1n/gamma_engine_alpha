#version 330 core

in vec2 Position;

out vec2 texCoords;

uniform float ScreenWidth;

void main()
{
	gl_Position = vec4(Position, 0.0, 1.0);
	texCoords = Position * 0.5 + 0.5;
}