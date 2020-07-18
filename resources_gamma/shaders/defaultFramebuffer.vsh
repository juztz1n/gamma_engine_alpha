#version 330 core

in vec2 Position;

out vec2 texCoords;

void main()
{
	gl_Position = vec4(Position, 0.0, 1.0);
	
	texCoords = vec2((Position.x+1.0)/2.0, (Position.y+1.0)/2.0);
}