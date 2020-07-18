package zin.gammaEngine.graphics.components;

import org.joml.Vector3f;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Shader;
import zin.gammaEngine.graphics.Transform;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class PointLight extends GameComponent
{
	private int identifier;

	private Vector3f position, attenuation, ambient, diffuse, specular;
	private float intensity;

	public PointLight(Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f attenuation, float intensity)
	{
		this.position = new Vector3f();
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.attenuation = attenuation;
		this.intensity = intensity;

		identifier = GraphicsEngine.addLight(this);
	}

	@Override
	public void initialize(Transform transform)
	{

	}

	@Override
	public void update(Transform transform, double deltaTime)
	{
		position = transform.position;
		GraphicsEngine.setLight(identifier, this);
	}

	@Override
	public void input(Transform transform, double deltaTime)
	{

	}

	@Override
	public void render(Transform transform)
	{

	}

	@Override
	public void destroy()
	{

	}

	public void bind(Shader shader, int index)
	{
		shader.setUniform("pointLights[" + index + "].position", getPosition());
		shader.setUniform("pointLights[" + index + "].attenuation", getAttenuation());
		shader.setUniform("pointLights[" + index + "].ambient", getAmbient());
		shader.setUniform("pointLights[" + index + "].diffuse", getDiffuse());
		shader.setUniform("pointLights[" + index + "].specular", getSpecular());
		shader.setUniform("pointLights[" + index + "].intensity", getIntensity());
	}

	public Vector3f getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(Vector3f diffuse)
	{
		this.diffuse = diffuse;
	}

	public float getIntensity()
	{
		return intensity;
	}

	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}

	public Vector3f getAttenuation()
	{
		return attenuation;
	}

	public Vector3f getAmbient()
	{
		return ambient;
	}

	public void setAmbient(Vector3f ambient)
	{
		this.ambient = ambient;
	}

	public Vector3f getSpecular()
	{
		return specular;
	}

	public void setSpecular(Vector3f specular)
	{
		this.specular = specular;
	}

	public void setAttenuation(Vector3f attenuation)
	{
		this.attenuation = attenuation;
	}

	public Vector3f getPosition()
	{
		return position;
	}
}
