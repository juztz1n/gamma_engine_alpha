#version 330 core

in vec2 texCoords;

out vec4 FragColor;

uniform sampler2D quadTexture;

uniform float ScreenWidth;
uniform float ScreenHeight;

uniform float radius;

uniform float softness;

void main()
{
	vec3 color = texture(quadTexture, texCoords).rgb;
		
	vec2 position = (gl_FragCoord.xy / vec2(ScreenWidth, ScreenHeight)) - vec2(0.5);
	
	float vignette = smoothstep(radius, radius-softness, length(position));
	
	color.rgb = mix(color, color * vignette, 0.5);
		
	FragColor = vec4(color, 1.0);
}