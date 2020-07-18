package zin.gammaEngine.graphics.components;

import org.joml.Vector3f;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Shader;
import zin.gammaEngine.graphics.Transform;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class DirectionalLight extends GameComponent
{
	private Vector3f ambient, diffuse, specular, direction;
	private float intensity;

	public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular, float intensity)
	{
		this.direction = direction;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.intensity = intensity;

		GraphicsEngine.addLight(this);
	}

	@Override
	public void initialize(Transform transform)
	{

	}

	@Override
	public void update(Transform transform, double deltaTime)
	{

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
		shader.setUniform("directionalLights[" + index + "].direction", getDirection());
		shader.setUniform("directionalLights[" + index + "].ambient", getAmbient());
		shader.setUniform("directionalLights[" + index + "].diffuse", getDiffuse());
		shader.setUniform("directionalLights[" + index + "].specular", getSpecular());
		shader.setUniform("directionalLights[" + index + "].intensity", getIntensity());
	}

	public Vector3f getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(Vector3f diffuse)
	{
		this.diffuse = diffuse;
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

	public float getIntensity()
	{
		return intensity;
	}

	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}

	public Vector3f getDirection()
	{
		return direction;
	}
}
