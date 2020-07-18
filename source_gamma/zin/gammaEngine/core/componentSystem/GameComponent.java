package zin.gammaEngine.core.componentSystem;

import zin.gammaEngine.graphics.Transform;

public abstract class GameComponent
{
	private GameObject parent;

	private boolean removed;

	public abstract void initialize(Transform transform);

	public abstract void update(Transform transform, double deltaTime);

	public abstract void input(Transform transform, double deltaTime);

	public abstract void render(Transform transform);

	public abstract void destroy();

	public void setParent(GameObject parent)
	{
		this.parent = parent;
	}

	public GameObject getParent()
	{
		return parent;
	}

	public void remove()
	{
		destroy();

		removed = true;
	}

	public boolean isRemoved()
	{
		return removed;
	}
}
