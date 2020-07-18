#version 330 core

in vec3 Position;

out vec3 texCoords;

uniform mat4 ProjectionMatrix;
uniform mat4 EyeMatrix;

void main()
{
	gl_Position = ProjectionMatrix * vec4(mat3(EyeMatrix) * Position, 1.0);
	texCoords = Position;
}