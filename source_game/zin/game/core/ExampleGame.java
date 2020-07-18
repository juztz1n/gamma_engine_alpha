package zin.game.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.core.CoreEngine;
import zin.gammaEngine.core.Game;
import zin.gammaEngine.core.Main;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.components.FreeMoveComponent;
import zin.gammaEngine.graphics.components.SkyboxComponent;
import zin.gammaEngine.graphics.components.SpotLight;
import zin.gammaEngine.graphics.core.GraphicsEngine;
import zin.gammaEngine.graphics.utils.DisplayState;

public class ExampleGame extends Game
{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final String TITLE = CoreEngine.getVersion() + " | Game Window (" + WIDTH + "x" + HEIGHT + ")";
	public static final DisplayState STATE = DisplayState.BORDERLESS;

	/*
	 * Important note:
	 * Sorry for a lack of documentation/comments.
	 * This project was only to get a feel for the ins and outs of OpenGL.
	 * This is by no means a complete project, in-fact it's a prototype.
	 * Soon enough I will be releasing a much better version in C++.
	 * 
	 * The project will be stored under gamma_engine. Thank you for taking
	 * the time to check out my code :).
	 */
	public ExampleGame()
	{
		super(TITLE, WIDTH, HEIGHT, STATE);
	}

	@Override
	public void initialize()
	{
		getCoreEngine().setPrintInfo(true);

		String[] textures =
		{ "resources_game/textures/skybox/zpos.jpg", "resources_game/textures/skybox/zneg.jpg",
				"resources_game/textures/skybox/ypos.jpg", "resources_game/textures/skybox/yneg.jpg",
				"resources_game/textures/skybox/xpos.jpg", "resources_game/textures/skybox/xneg.jpg" };

		addComponent(new SkyboxComponent(textures));

		GameObject light = new GameObject();
		light.addComponent(new SpotLight(new Vector3f(0, 0.5f, 1), new Vector3f(0.05f), new Vector3f(1),
				new Vector3f(1), new Vector3f(1, 0, 0), 1, 42f, 40f));

		light.getTransform().position = new Vector3f(0, .5f, 0);

		addObject(light);

		GameObject camera = new GameObject();
		camera.addComponent(new FreeMoveComponent());

		addObject(camera);

		getGraphicsEngine().setToneMapping(true);
		getGraphicsEngine().setGamma(2.2f);
		getGraphicsEngine().setExposure(1f);

		getGraphicsEngine().setBloomFactor(3f);
		getGraphicsEngine().setBloomQuality(4);
		getGraphicsEngine().setBloomStrength(5);

		getGraphicsEngine().setVignetteRadius(0.5f);
		getGraphicsEngine().setVignetteSoftness(0.25f);

		getGraphicsEngine().setVignetting(true);
		getGraphicsEngine().setBloom(true);
	}

	@Override
	public void update()
	{

	}

	@Override
	public void input()
	{
		if (Display.isKeyReleased(GLFW.GLFW_KEY_F1))
		{
			getGraphicsEngine().setBloom(!getGraphicsEngine().bloomEnabled());
			getGraphicsEngine().setVignetting(!getGraphicsEngine().vignettingEnabled());
		}

		if (Display.isKeyReleased(GLFW.GLFW_KEY_F2))
			getGraphicsEngine().setBloomStrength(getGraphicsEngine().getBloomStrength() + 1);
		if (Display.isKeyReleased(GLFW.GLFW_KEY_F3))
			getGraphicsEngine().setBloomStrength(getGraphicsEngine().getBloomStrength() - 1);

		if (Display.isKeyReleased(GLFW.GLFW_KEY_ESCAPE))
			setShouldClose(true);
		if (Display.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_MIDDLE))
			Display.setMouseGrabbed(!Display.isMouseGrabbed());

		Main.FOV = 120 - Math.max(Math.min((Display.getMouseWheel().y * 5), 119), -59);
		GraphicsEngine.updateProjectionMatrix();
	}

	@Override
	public void render()
	{

	}
}
