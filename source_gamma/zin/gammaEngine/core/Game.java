package zin.gammaEngine.core;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.core.componentSystem.GameObject;
import zin.gammaEngine.graphics.components.FreeMoveComponent;
import zin.gammaEngine.graphics.core.GraphicsEngine;
import zin.gammaEngine.graphics.utils.DisplayState;

public abstract class Game
{
	private GameObject root;

	private DisplayState state;

	private int width, height;
	private String title;

	public Game(String title, int width, int height, DisplayState state)
	{
		this.title = title;
		this.width = width;
		this.height = height;
		this.state = state;
	}

	private CoreEngine coreEngine;
	private GraphicsEngine graphicsEngine;
	private boolean shouldClose = false;

	public abstract void initialize();

	public abstract void update();

	public abstract void input();

	public abstract void render();

	public GameObject getRootObject()
	{
		if (root == null)
			root = new GameObject();

		return root;
	}

	public GameObject addObject(GameObject object)
	{
		return getRootObject().addChild(object);
	}

	public GameObject addComponent(GameComponent component)
	{
		return getRootObject().addComponent(component);
	}

	public void setCoreEngine(CoreEngine coreEngine)
	{
		this.coreEngine = coreEngine;
	}

	public CoreEngine getCoreEngine()
	{
		return coreEngine;
	}

	public void setGraphicsEngine(GraphicsEngine graphicsEngine)
	{
		this.graphicsEngine = graphicsEngine;
	}

	public GraphicsEngine getGraphicsEngine()
	{
		return graphicsEngine;
	}

	public FreeMoveComponent getFreeMove()
	{
		return GraphicsEngine.getCurrentFreeMove();
	}

	public void setShouldClose(boolean shouldClose)
	{
		this.shouldClose = shouldClose;
	}

	public boolean shouldClose()
	{
		return shouldClose;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public DisplayState getState()
	{
		return state;
	}

	public void setState(DisplayState state)
	{
		this.state = state;
	}
}
