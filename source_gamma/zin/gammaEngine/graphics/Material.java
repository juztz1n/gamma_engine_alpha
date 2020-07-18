package zin.gammaEngine.graphics;

import org.lwjgl.opengl.GL13;

import zin.gammaEngine.graphics.utils.TextureType;

public class Material
{
	private Texture diffuse, gloss, normal, displacement;
	private float specularIntensity, specularDampening, parallaxScale, parallaxBias;

	public Material(String diffuse, String gloss, String normal, String displacement, float specularIntensity,
			float specularDampening, float parallaxScale, float parallaxOffset)
	{
		this.diffuse = new Texture(diffuse, TextureType.DIFFUSE);
		this.gloss = new Texture(gloss, TextureType.GLOSS);
		this.normal = new Texture(normal, TextureType.NORMAL);
		this.displacement = new Texture(displacement, TextureType.DISPLACEMENT);

		if (this.diffuse.hasFailed())
		{
			this.diffuse = new Texture("resources/textures/default_diffuse.jpg", TextureType.DIFFUSE);
		}

		if (this.gloss.hasFailed())
		{
			this.gloss = new Texture("resources/textures/default_gloss.jpg", TextureType.GLOSS);
		}

		if (this.normal.hasFailed())
		{
			this.normal = new Texture("resources/textures/default_normal.jpg", TextureType.NORMAL);
		}

		if (this.displacement.hasFailed())
		{
			this.displacement = new Texture("resources/textures/default_disp.png", TextureType.DISPLACEMENT);
		}

		this.specularIntensity = specularIntensity;
		this.specularDampening = specularDampening;
		this.parallaxScale = parallaxScale;
		this.parallaxBias = -(parallaxScale / 2) + (parallaxScale / 2) * parallaxOffset;
	}

	public void bindDiffuse()
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		diffuse.bind();
	}

	public void bindGloss()
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		gloss.bind();
	}

	public void bindNormal()
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		normal.bind();
	}

	private void bindDisplacement()
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		displacement.bind();
	}

	public void bind()
	{
		bindDiffuse();
		bindGloss();
		bindNormal();
		bindDisplacement();
	}

	public void setDiffuse(Texture diffuse)
	{
		this.diffuse = diffuse;
	}

	public Texture getDiffuse()
	{
		return diffuse;
	}

	public void setGloss(Texture gloss)
	{
		this.gloss = gloss;
	}

	public Texture getGloss()
	{
		return gloss;
	}

	public void setNormal(Texture normal)
	{
		this.normal = normal;
	}

	public Texture getNormal()
	{
		return normal;
	}

	public Texture getDisplacement()
	{
		return displacement;
	}

	public void setDisplacement(Texture displacement)
	{
		this.displacement = displacement;
	}

	public float getSpecularIntensity()
	{
		return specularIntensity;
	}

	public float getSpecularDampening()
	{
		return specularDampening;
	}

	public float getParallaxScale()
	{
		return parallaxScale;
	}

	public float getParallaxBias()
	{
		return parallaxBias;
	}

	public void destroy()
	{
		diffuse.destroy();
		gloss.destroy();
		normal.destroy();
		displacement.destroy();
	}

	public void unbind()
	{
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		diffuse.unbind();
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		gloss.unbind();
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		normal.unbind();
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		displacement.unbind();
	}
}
