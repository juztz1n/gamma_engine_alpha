package zin.gammaEngine.core.componentSystem;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import zin.gammaEngine.graphics.Transform;

public class GameObject
{
	private boolean removed;

	private List<GameObject> children;
	private List<GameComponent> components;

	private Transform transform;

	public GameObject()
	{
		removed = false;

		children = new ArrayList<>();
		components = new ArrayList<>();
		transform = new Transform();
	}

	public GameObject addChild(GameObject child)
	{
		getChildren().add(child);
		return this;
	}

	public GameObject addComponent(GameComponent component)
	{
		getComponents().add(component);
		component.setParent(this);
		return this;
	}

	public void initialize()
	{
		for (GameComponent component : getComponents())
			component.initialize(transform);

		for (GameObject child : getChildren())
			child.initialize();
	}

	public void input(double deltaTime)
	{
		for (GameComponent component : getComponents())
			component.input(transform, deltaTime);

		for (GameObject child : getChildren())
			child.input(deltaTime);
	}

	public void update(double deltaTime)
	{
		for (int i = 0; i < getComponents().size(); i++)
		{
			if (getComponents().get(i).isRemoved())
			{
				getComponents().remove(i);
				continue;
			}

			getComponents().get(i).update(transform, deltaTime);
		}

		for (int i = 0; i < getChildren().size(); i++)
		{
			if (getChildren().get(i).isRemoved())
			{
				getChildren().remove(i);
				continue;
			}

			getChildren().get(i).update(deltaTime);
		}
	}

	public void render()
	{
		for (int i = 0; i < getComponents().size(); i++)
			getComponents().get(i).render(transform);

		for (int i = 0; i < getChildren().size(); i++)
			getChildren().get(i).render();
	}

	public void destroy()
	{
		for (GameComponent component : getComponents())
			component.destroy();

		for (GameObject child : getChildren())
			child.destroy();
	}

	public Transform getTransform()
	{
		return transform;
	}

	public Vector3f getPosition()
	{
		return transform.position;
	}

	public Quaternionf getRotation()
	{
		return transform.rotation;
	}

	public Vector3f getScale()
	{
		return transform.scale;
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

	public List<GameObject> getChildren()
	{
		return children;
	}

	public void setChildren(List<GameObject> children)
	{
		this.children = children;
	}

	public List<GameComponent> getComponents()
	{
		return components;
	}

	public void setComponents(List<GameComponent> components)
	{
		this.components = components;
	}
}
