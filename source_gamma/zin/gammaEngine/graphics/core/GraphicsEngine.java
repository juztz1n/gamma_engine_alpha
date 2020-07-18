package zin.gammaEngine.graphics.core;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import zin.gammaEngine.core.CoreEngine;
import zin.gammaEngine.core.Logger;
import zin.gammaEngine.core.Main;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.ScreenQuad;
import zin.gammaEngine.graphics.Shader;
import zin.gammaEngine.graphics.components.DirectionalLight;
import zin.gammaEngine.graphics.components.FreeMoveComponent;
import zin.gammaEngine.graphics.components.PointLight;
import zin.gammaEngine.graphics.components.SpotLight;
import zin.gammaEngine.graphics.postProcessingSystem.FrameBufferObject;

public class GraphicsEngine
{
	private static Shader phongShader, skyboxShader;

	private static Matrix4f projectionMatrix;
	private static List<Shader> shaders = new ArrayList<>();
	private static List<DirectionalLight> directionalLights = new ArrayList<>();
	private static List<PointLight> pointLights = new ArrayList<>();
	private static List<SpotLight> spotLights = new ArrayList<>();

	private boolean postProcessing = true, toneMapping = true, bloom, vignetting;

	private float gamma, exposure, bloomFactor = 1.5f, vignetteRadius = 0.5f, vignetteSoftness = 0.25f,
			bloomQuality = 3, bloomStrength = 4;

	private Shader defaultFBOShader, toneMappingShader, brightFilterShader, verticalBlurShader, horizontalBlurShader,
			bloomShader, vignetteShader;

	private FrameBufferObject screenFBO, brightFilterFBO, verticalBlurFBO, horizontalBlurFBO, bloomFBO, vignetteFBO;

	private ScreenQuad quad;

	private static FreeMoveComponent freeMove;

	private CoreEngine coreEngine;

	public GraphicsEngine(CoreEngine coreEngine)
	{
		this.coreEngine = coreEngine;
	}

	public void initialize()
	{
		new FreeMoveComponent();

		projectionMatrix = new Matrix4f().perspective(Main.getFOV(), Display.getAspectRatio(), Main.Z_NEAR, Main.Z_FAR);

		quad = new ScreenQuad();

		Logger.info("Graphics Card Manufacturer: " + GL11.glGetString(GL11.GL_VENDOR));
		Logger.info("Graphics Card Name: " + GL11.glGetString(GL11.GL_RENDERER));
		Logger.info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
		Logger.info("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		Logger.info("GLFW Version: " + GLFW.glfwGetVersionString());
		Logger.info("LWJGL Version: " + Version.getVersion());
		System.out.println("\n");

		GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		GL11.glEnable(GL30.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		phongShader = new Shader("resources_gamma/shaders/general/phong.vsh",
				"resources_gamma/shaders/general/phong.fsh");
		skyboxShader = new Shader("resources_gamma/shaders/skybox/skybox.vsh",
				"resources_gamma/shaders/skybox/skybox.fsh");
	}

	public void initializePostProcessing()
	{
		if (postProcessing)
		{
			if (toneMapping)
			{
				screenFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(), GL30.GL_COLOR_ATTACHMENT0,
						true);
				toneMappingShader = new Shader("resources_gamma/shaders/postProcessing/toneMapping.vsh",
						"resources_gamma/shaders/postProcessing/toneMapping.fsh");

				setGamma(gamma);
				setExposure(exposure);
			} else
			{
				screenFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(), GL30.GL_COLOR_ATTACHMENT0,
						false);
			}
			defaultFBOShader = new Shader("resources_gamma/shaders/postProcessing/defaultFramebuffer.vsh",
					"resources_gamma/shaders/postProcessing/defaultFramebuffer.fsh");

			if (bloom)
			{
				brightFilterFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
						GL30.GL_COLOR_ATTACHMENT0, false);
				brightFilterShader = new Shader("resources_gamma/shaders/postProcessing/brightFilter.vsh",
						"resources_gamma/shaders/postProcessing/brightFilter.fsh");

				brightFilterShader.setUniform("bloomStrength", bloomStrength);

				verticalBlurShader = new Shader("resources_gamma/shaders/postProcessing/verticalBlur.vsh",
						"resources_gamma/shaders/postProcessing/verticalBlur.fsh");
				horizontalBlurShader = new Shader("resources_gamma/shaders/postProcessing/horizontalBlur.vsh",
						"resources_gamma/shaders/postProcessing/horizontalBlur.fsh");

				bloomQuality = Math.min(bloomQuality, 4);

				if (bloomQuality == 0)
				{
					verticalBlurFBO = new FrameBufferObject(Display.getWidth() / 5, Display.getHeight() / 5,
							GL30.GL_COLOR_ATTACHMENT0, false);
					horizontalBlurFBO = new FrameBufferObject(Display.getWidth() / 5, Display.getHeight() / 5,
							GL30.GL_COLOR_ATTACHMENT0, false);
				} else if (bloomQuality == 1)
				{

					verticalBlurFBO = new FrameBufferObject(Display.getWidth() / 4, Display.getHeight() / 4,
							GL30.GL_COLOR_ATTACHMENT0, false);
					horizontalBlurFBO = new FrameBufferObject(Display.getWidth() / 4, Display.getHeight() / 4,
							GL30.GL_COLOR_ATTACHMENT0, false);
				} else if (bloomQuality == 2)
				{
					verticalBlurFBO = new FrameBufferObject(Display.getWidth() / 3, Display.getHeight() / 3,
							GL30.GL_COLOR_ATTACHMENT0, false);
					horizontalBlurFBO = new FrameBufferObject(Display.getWidth() / 3, Display.getHeight() / 3,
							GL30.GL_COLOR_ATTACHMENT0, false);
				} else if (bloomQuality == 3)
				{
					verticalBlurFBO = new FrameBufferObject(Display.getWidth() / 2, Display.getHeight() / 2,
							GL30.GL_COLOR_ATTACHMENT0, false);
					horizontalBlurFBO = new FrameBufferObject(Display.getWidth() / 2, Display.getHeight() / 2,
							GL30.GL_COLOR_ATTACHMENT0, false);
				} else if (bloomQuality == 4)
				{
					verticalBlurFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
							GL30.GL_COLOR_ATTACHMENT0, false);
					horizontalBlurFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(),
							GL30.GL_COLOR_ATTACHMENT0, false);
				}

				verticalBlurShader.setUniform("ProjectionMatrix", projectionMatrix);
				verticalBlurShader.setUniform("BlurWidth", (float) verticalBlurFBO.getWidth());
				verticalBlurShader.setUniform("BlurHeight", (float) verticalBlurFBO.getHeight());

				horizontalBlurShader.setUniform("ProjectionMatrix", projectionMatrix);
				horizontalBlurShader.setUniform("BlurWidth", (float) horizontalBlurFBO.getWidth());
				horizontalBlurShader.setUniform("BlurHeight", (float) horizontalBlurFBO.getHeight());

				bloomShader = new Shader("resources_gamma/shaders/postProcessing/bloom.vsh",
						"resources_gamma/shaders/postProcessing/bloom.fsh");
				bloomFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(), GL30.GL_COLOR_ATTACHMENT0,
						false);

				bloomShader.setUniform("highlightTexture", 0);
				bloomShader.setUniform("originalTexture", 1);
				setBloomFactor(bloomFactor);
			}
			if (vignetting)
			{
				vignetteShader = new Shader("resources_gamma/shaders/postProcessing/vignette.vsh",
						"resources_gamma/shaders/postProcessing/vignette.fsh");
				vignetteFBO = new FrameBufferObject(Display.getWidth(), Display.getHeight(), GL30.GL_COLOR_ATTACHMENT0,
						false);
				vignetteShader.setUniform("radius", vignetteRadius);
				vignetteShader.setUniform("softness", vignetteSoftness);
				vignetteShader.setUniform("ScreenWidth", (float) Display.getWidth());
				vignetteShader.setUniform("ScreenHeight", (float) Display.getHeight());
			}
		}

		updateProjectionMatrix();
	}

	public static void updateProjectionMatrix()
	{
		projectionMatrix = new Matrix4f().perspective(Main.getFOV(), Display.getAspectRatio(), Main.Z_NEAR, Main.Z_FAR);
		for (Shader shader : shaders)
		{
			shader.setUniform("ProjectionMatrix", projectionMatrix);
		}
	}

	public void preRender()
	{
		if (postProcessing)
			screenFBO.bind();
	}

	public void postRender()
	{
		if (postProcessing && toneMapping)
		{
			if (bloom && vignetting)
			{
				brightFilterFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				toneMappingShader.bind();
				quad.draw();

				horizontalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightFilterFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				brightFilterShader.bind();
				quad.draw();

				verticalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, horizontalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				horizontalBlurShader.bind();
				quad.draw();

				bloomFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				verticalBlurShader.bind();
				quad.draw();

				vignetteFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, bloomFBO.getColorTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightFilterFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				bloomShader.bind();
				quad.draw();

				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, vignetteFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				vignetteShader.bind();
				quad.draw();
			} else if (!bloom && vignetting)
			{
				vignetteFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				toneMappingShader.bind();
				quad.draw();

				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, vignetteFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				vignetteShader.bind();
				quad.draw();
			} else if (bloom && !vignetting)
			{
				brightFilterFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				toneMappingShader.bind();
				quad.draw();

				horizontalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightFilterFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				brightFilterShader.bind();
				quad.draw();

				verticalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, horizontalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				horizontalBlurShader.bind();
				quad.draw();

				bloomFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				verticalBlurShader.bind();
				quad.draw();

				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, bloomFBO.getColorTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightFilterFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				bloomShader.bind();
				quad.draw();
			} else
			{
				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				toneMappingShader.bind();
				quad.draw();
			}
		} else if (postProcessing && !toneMapping)
		{
			if (bloom && vignetting)
			{
				horizontalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				brightFilterShader.bind();
				quad.draw();

				verticalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, horizontalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				horizontalBlurShader.bind();
				quad.draw();

				bloomFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				verticalBlurShader.bind();
				quad.draw();

				vignetteFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, bloomFBO.getColorTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				bloomShader.bind();
				quad.draw();

				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, vignetteFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				vignetteShader.bind();
				quad.draw();
			} else if (!bloom && vignetting)
			{
				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				vignetteShader.bind();
				quad.draw();
			} else if (bloom && !vignetting)
			{
				horizontalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				brightFilterShader.bind();
				quad.draw();

				verticalBlurFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, horizontalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				horizontalBlurShader.bind();
				quad.draw();

				bloomFBO.bind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalBlurFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				verticalBlurShader.bind();
				quad.draw();

				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, bloomFBO.getColorTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				bloomShader.bind();
				quad.draw();
			} else
			{
				FrameBufferObject.unbind();
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, screenFBO.getColorTexture());
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				defaultFBOShader.bind();
				quad.draw();
			}
		}
	}

	public void render()
	{
		preRender();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		updateLights();
		coreEngine.getGame().getRootObject().render();
		coreEngine.getGame().render();
		postRender();
	}

	private void updateLights()
	{
		for (Shader shader : shaders)
		{
			shader.setUniform("EyeMatrix", freeMove.getTransformation());
			for (int i = 0; i < directionalLights.size(); i++)
			{
				directionalLights.get(i).bind(shader, i);
			}
			for (int i = 0; i < pointLights.size(); i++)
			{
				pointLights.get(i).bind(shader, i);
			}
			for (int i = 0; i < spotLights.size(); i++)
			{
				spotLights.get(i).bind(shader, i);
			}
		}
	}

	public void destroy()
	{
		for (Shader shader : shaders)
		{
			shader.destroy();
		}
	}

	public static void setCurrentFreeMove(FreeMoveComponent camera)
	{
		GraphicsEngine.freeMove = camera;
	}

	public static FreeMoveComponent getCurrentFreeMove()
	{
		return freeMove;
	}

	public static void addShader(Shader shader)
	{
		shaders.add(shader);
	}

	public static int addLight(DirectionalLight directionalLight)
	{
		directionalLights.add(directionalLight);
		return directionalLights.size() - 1;
	}

	public static int addLight(PointLight pointLight)
	{
		pointLights.add(pointLight);
		return pointLights.size() - 1;
	}

	public static int addLight(SpotLight spotLight)
	{
		spotLights.add(spotLight);
		return spotLights.size() - 1;
	}

	public static void setLight(int identifier, DirectionalLight directionalLight)
	{
		directionalLights.set(identifier, directionalLight);
	}

	public static void setLight(int identifier, PointLight pointLight)
	{
		pointLights.set(identifier, pointLight);
	}

	public static void setLight(int identifier, SpotLight spotLight)
	{
		spotLights.set(identifier, spotLight);
	}

	public static void setSkyboxShader(Shader skyboxShader)
	{
		GraphicsEngine.skyboxShader = skyboxShader;
	}

	public static Shader getSkyboxShader()
	{
		return skyboxShader;
	}

	public boolean postProcessingEnabled()
	{
		return postProcessing;
	}

	public void setPostProcessing(boolean postProcessing)
	{
		this.postProcessing = postProcessing;
	}

	public boolean bloomEnabled()
	{
		return bloom;
	}

	public void setBloom(boolean bloom)
	{
		this.bloom = bloom;
	}

	public boolean vignettingEnabled()
	{
		return vignetting;
	}

	public void setVignetting(boolean vignetting)
	{
		this.vignetting = vignetting;
	}

	public float getBloomFactor()
	{
		return bloomFactor;
	}

	public void setBloomFactor(float bloomFactor)
	{
		this.bloomFactor = bloomFactor;

		if (bloomShader != null)
			bloomShader.setUniform("bloomFactor", bloomFactor);
	}

	public float getVignetteRadius()
	{
		return vignetteRadius;
	}

	public void setVignetteRadius(float vignetteRadius)
	{
		this.vignetteRadius = vignetteRadius;

		if (vignetteShader != null)
			vignetteShader.setUniform("radius", vignetteRadius);
	}

	public float getVignetteSoftness()
	{
		return vignetteSoftness;
	}

	public void setVignetteSoftness(float vignetteSoftness)
	{
		this.vignetteSoftness = vignetteSoftness;

		if (vignetteShader != null)
			vignetteShader.setUniform("softness", vignetteSoftness);
	}

	public boolean toneMappingEnabled()
	{
		return toneMapping;
	}

	public void setToneMapping(boolean toneMapping)
	{
		this.toneMapping = toneMapping;
	}

	public float getGamma()
	{
		return gamma;
	}

	public void setGamma(float gamma)
	{
		this.gamma = gamma;

		if (toneMappingShader != null)
			toneMappingShader.setUniform("gamma", gamma);
	}

	public float getExposure()
	{
		return exposure;
	}

	public void setExposure(float exposure)
	{
		this.exposure = exposure;

		if (toneMappingShader != null)
			toneMappingShader.setUniform("exposure", exposure);
	}

	public float getBloomQuality()
	{
		return bloomQuality;
	}

	public void setBloomQuality(float bloomQuality)
	{
		this.bloomQuality = bloomQuality;
	}

	public float getBloomStrength()
	{
		return bloomStrength;
	}

	public void setBloomStrength(float bloomStrength)
	{
		this.bloomStrength = bloomStrength;

		if (brightFilterShader != null)
			brightFilterShader.setUniform("bloomStrength", bloomStrength);
	}

	public static void setPhongShader(Shader phongShader)
	{
		GraphicsEngine.phongShader = phongShader;
	}

	public static Shader getPhongShader()
	{
		return phongShader;
	}
}
