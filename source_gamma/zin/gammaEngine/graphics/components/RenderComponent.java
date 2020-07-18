package zin.gammaEngine.graphics.components;

import zin.gammaEngine.core.FileSystem;
import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Material;
import zin.gammaEngine.graphics.Model;
import zin.gammaEngine.graphics.Transform;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class RenderComponent extends GameComponent
{
	private Model model;
	private Material material;

	public RenderComponent(String fileName, Material material)
	{
		this.model = new Model(fileName);
		this.material = material;
	}

	public RenderComponent(String fileName)
	{
		RenderComponent loadedComponent = FileSystem.loadRenderComponent(fileName);

		model = loadedComponent.getModel();
		material = loadedComponent.getMaterial();
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
		material.bind();
		GraphicsEngine.getPhongShader().setUniform("ModelMatrix", transform.getTransformation());
		GraphicsEngine.getPhongShader().setUniform("material.diffuse", 0);
		GraphicsEngine.getPhongShader().setUniform("material.gloss", 1);
		GraphicsEngine.getPhongShader().setUniform("material.normal", 2);
		GraphicsEngine.getPhongShader().setUniform("material.displacement", 3);
		GraphicsEngine.getPhongShader().setUniform("material.heightScale", material.getParallaxScale());
		GraphicsEngine.getPhongShader().setUniform("material.heightBias", material.getParallaxBias());
		GraphicsEngine.getPhongShader().setUniform("material.specularIntensity", material.getSpecularIntensity());
		GraphicsEngine.getPhongShader().setUniform("material.specularDampening", material.getSpecularDampening());
		model.draw();
	}

	@Override
	public void destroy()
	{
		material.destroy();
		model.destroy();
	}

	public void setModel(Model model)
	{
		this.model = model;
	}

	public Model getModel()
	{
		return model;
	}

	public void setMaterial(Material material)
	{
		this.material = material;
	}

	public Material getMaterial()
	{
		return material;
	}
}
