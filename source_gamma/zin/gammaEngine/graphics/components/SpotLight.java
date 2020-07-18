package zin.gammaEngine.graphics.components;

import org.joml.Math;
import org.joml.Vector3f;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Shader;
import zin.gammaEngine.graphics.Transform;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class SpotLight extends GameComponent
{
	private int identifier;

	private Vector3f direction, position, attenuation, ambient, diffuse, specular;
	private float intensity, cutOff, outerCutOff;

	public SpotLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f attenuation,
			float intensity, float cutOff, float outerCutOff)
	{
		this.direction = direction;
		this.position = new Vector3f();
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.attenuation = attenuation;
		this.intensity = intensity;
		this.cutOff = (float) Math.toRadians(cutOff);
		this.outerCutOff = (float) Math.toRadians(outerCutOff);

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
		shader.setUniform("spotLights[" + index + "].position", getPosition());
		shader.setUniform("spotLights[" + index + "].direction", getDirection());
		shader.setUniform("spotLights[" + index + "].attenuation", getAttenuation());
		shader.setUniform("spotLights[" + index + "].ambient", getAmbient());
		shader.setUniform("spotLights[" + index + "].diffuse", getDiffuse());
		shader.setUniform("spotLights[" + index + "].specular", getSpecular());
		shader.setUniform("spotLights[" + index + "].intensity", getIntensity());
		shader.setUniform("spotLights[" + index + "].cutOff", getCutOff());
		shader.setUniform("spotLights[" + index + "].outerCutOff", getOuterCutOff());
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

	public void setAttenuation(Vector3f attenuation)
	{
		this.attenuation = attenuation;
	}

	public float getCutOff()
	{
		return cutOff;
	}

	public void setCutOff(float cutOff)
	{
		this.cutOff = cutOff;
	}

	public float getOuterCutOff()
	{
		return outerCutOff;
	}

	public void setOuterCutOff(float outerCutOff)
	{
		this.outerCutOff = outerCutOff;
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

	public Vector3f getDirection()
	{
		return direction;
	}

	public void setDirection(Vector3f direction)
	{
		this.direction = direction;
	}

	public Vector3f getPosition()
	{
		return position;
	}
}
