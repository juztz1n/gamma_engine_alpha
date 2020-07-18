#version 330 core

in vec3 position;
in vec2 texCoords;

in mat3 tbnMatrix;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

struct Material
{
	sampler2D diffuse, gloss, normal, displacement;
	float specularIntensity, specularDampening, heightScale, heightBias;
};

struct DirectionalLight
{
	vec3 direction, ambient, diffuse, specular;
	float intensity;
};

struct PointLight
{
	vec3 position, attenuation, ambient, diffuse, specular;
	float intensity;
};

struct SpotLight
{
	vec3 position, direction, attenuation, ambient, diffuse, specular;
	float intensity, cutOff, outerCutOff;
};

uniform Material material;

const int MAX_DIRECTIONAL_LIGHTS = 5;
const int MAX_POINT_LIGHTS = 15;
const int MAX_SPOT_LIGHTS = 15;

uniform DirectionalLight directionalLights[MAX_DIRECTIONAL_LIGHTS];
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

uniform mat4 EyeMatrix;

vec3 calculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDir, vec2 parallaxTexCoords)
{
    vec3 lightDir = normalize(-light.direction);

    float diff = max(dot(normal, lightDir), 0.0);

	vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = ((8.0 + material.specularDampening) / (8.0 * 3.14159265)) * pow(max(dot(normal, halfwayDir), material.specularIntensity), material.specularDampening);

    vec3 ambient  = light.intensity * light.ambient  * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 diffuse  = light.intensity * light.diffuse  * diff * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 specular = light.intensity * light.specular * spec * vec3(texture(material.gloss, parallaxTexCoords));
    
    return (ambient + diffuse + specular);
}  

vec3 calculatePointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, vec2 parallaxTexCoords)
{
    vec3 lightDir = normalize(light.position - fragPos);

    float diff = max(dot(normal, lightDir), 0.0);

	vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = ((8.0 + material.specularDampening) / (8.0 * 3.14159265)) * pow(max(dot(normal, halfwayDir), material.specularIntensity), material.specularDampening);

    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.attenuation.x + light.attenuation.y * distance + light.attenuation.z * (distance * distance));    
    
    vec3 ambient  = light.intensity * light.ambient  * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 diffuse  = light.intensity * light.diffuse  * diff * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 specular = light.intensity * light.specular * spec * vec3(texture(material.gloss, parallaxTexCoords));
    
    diffuse  *= attenuation;
    specular *= attenuation;
    
    return (ambient + diffuse + specular);
}

vec3 calculateSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir, vec2 parallaxTexCoords)
{
    vec3 lightDir = normalize(light.position - fragPos);

    float diff = max(dot(normal, lightDir), 0.0);

	vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = ((8.0 + material.specularDampening) / (8.0 * 3.14159265)) * pow(max(dot(normal, halfwayDir), material.specularIntensity), material.specularDampening);

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.attenuation.x + light.attenuation.y * distance + light.attenuation.z * (distance * distance));    

    float theta = dot(lightDir, normalize(-light.direction)); 
    float epsilon = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);

    vec3 ambient = light.intensity * light.ambient * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 diffuse = light.intensity * light.diffuse * diff * vec3(texture(material.diffuse, parallaxTexCoords));
    vec3 specular = light.intensity * light.specular * spec * vec3(texture(material.gloss, parallaxTexCoords));

    diffuse *= attenuation * intensity;
    specular *= attenuation * intensity;

    return (ambient + diffuse + specular);
}
void main()
{
	vec3 eyeDir = normalize((inverse(EyeMatrix)[3]).xyz - position);
	
    vec2 parallaxTexCoords = texCoords + (normalize(vec3(eyeDir.x, eyeDir.y, eyeDir.z)) * tbnMatrix).xy * (texture(material.displacement, texCoords).r) * material.heightScale + material.heightBias;

	if(parallaxTexCoords.x > 1.0 || parallaxTexCoords.y > 1.0 || parallaxTexCoords.x < 0.0 || parallaxTexCoords.y < 0.0)
    	discard;

	vec3 result = vec3(0.0);
	
	vec3 norm = normalize(tbnMatrix * (255.0/128.0 * texture(material.normal, parallaxTexCoords).rgb - 1));
	
	for (int i = 0; i < MAX_DIRECTIONAL_LIGHTS; i++)
		if (directionalLights[i].intensity != 0)
			result += calculateDirectionalLight(directionalLights[i], norm, eyeDir, parallaxTexCoords);
	
	for (int i = 0; i < MAX_POINT_LIGHTS; i++)
		if (pointLights[i].intensity != 0)
			result += calculatePointLight(pointLights[i], norm, position, eyeDir, parallaxTexCoords);
	
	for (int i = 0; i < MAX_SPOT_LIGHTS; i++)
		if (spotLights[i].intensity != 0)
			result += calculateSpotLight(spotLights[i], norm, position, eyeDir, parallaxTexCoords);

    FragColor = vec4(result, 1.0);
	
	float brightness = dot(FragColor.rgb, vec3(0.299, 0.587, 0.114));
	
	BrightColor = FragColor * pow(brightness, 2);
}