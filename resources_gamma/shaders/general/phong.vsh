#version 330 core

in vec3 Position;
in vec2 TexCoords;
in vec3 Normal;
in vec3 Tangent;

out vec3 position;
out vec2 texCoords;

out mat3 tbnMatrix;

uniform mat4 ModelMatrix;
uniform mat4 EyeMatrix;
uniform mat4 ProjectionMatrix;

void main()
{
    position = vec3(ModelMatrix * vec4(Position, 1.0));
    texCoords = TexCoords;
    
    vec3 T = normalize((ModelMatrix * vec4(Tangent, 1.0)).xyz);
    vec3 N = normalize((ModelMatrix * vec4(Normal, 1.0)).xyz);
    
    T = normalize(T - dot(T, N) * N);
    
    vec3 B = normalize(cross(T, N));
    
    tbnMatrix = mat3(-T, B, N);
    
	gl_Position = ProjectionMatrix * EyeMatrix * ModelMatrix * vec4(Position, 1.0);
}