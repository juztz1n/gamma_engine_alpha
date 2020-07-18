package zin.gammaEngine.graphics.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import zin.gammaEngine.core.componentSystem.GameComponent;
import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.Transform;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class FreeMoveComponent extends GameComponent
{
	public Vector3f position;
	public Vector3f forward, up, right;
	public float pitch, yaw;

	public FreeMoveComponent()
	{
		position = new Vector3f();
		forward = new Vector3f(0, 0, 1);
		up = new Vector3f(0, 1, 0);
		right = new Vector3f();

		GraphicsEngine.setCurrentFreeMove(this);
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
		Vector3f front = new Vector3f(); 
		front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.y = (float)(Math.sin(Math.toRadians(pitch)));
		front.z =  (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		forward = front.normalize();
		right = new Vector3f(forward).cross(new Vector3f(0, 1, 0)).normalize();
		up = new Vector3f(right).cross(new Vector3f(forward)).normalize();
		

		float cameraSpeed = (float) (10f * deltaTime);
		yaw += Display.getDX() * 0.05f;
		pitch -= Display.getDY() * 0.05f;
		pitch = Math.min(Math.max(pitch, -90f), 90f);

		if (yaw > 360.0f)
			yaw = -360.0f;
		if (yaw < -360.0f)
			yaw = 360.0f;

		if (Display.isKeyDown(GLFW.GLFW_KEY_W) || Display.isKeyDown(GLFW.GLFW_KEY_ENTER))
			position.add(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_S))
			position.sub(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_A))
			position.sub(new Vector3f(right).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_D))
			position.add(new Vector3f(right).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_SPACE))
			position.add(new Vector3f(up).mul(cameraSpeed*0.2f));
		if (Display.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			position.add(new Vector3f(new Vector3f(up).negate()).mul(cameraSpeed*0.2f));
		
		position = transform.position;
	}

	@Override
	public void render(Transform transform)
	{

	}

	@Override
	public void destroy()
	{

	}

	public Matrix4f getTransformation()
	{
		return new Matrix4f().lookAt(position, new Vector3f(forward).add(new Vector3f(position)), up);
	}
}
