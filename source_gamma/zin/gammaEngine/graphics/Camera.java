package zin.gammaEngine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera
{
	public Vector3f position, forward, up, right;
	public float pitch, yaw;

	public Camera(Vector3f position)
	{
		this.position = position;
		forward = new Vector3f(0, 0, 1);
		up = new Vector3f(0, 1, 0);
		right = new Vector3f();
	}

	public Camera()
	{
		position = new Vector3f();
		forward = new Vector3f(0, 0, 1);
		up = new Vector3f(0, 1, 0);
		right = new Vector3f();
	}

	public void update(double deltaTime)
	{
		Vector3f front = new Vector3f((float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch)),
				(float) Math.sin(Math.toRadians(pitch)),
				(float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch)));
		forward = front.normalize();
		right = new Vector3f(forward).cross(new Vector3f(0, 1, 0)).normalize();
		up = new Vector3f(right).cross(new Vector3f(forward)).normalize();

		float cameraSpeed = (float) (10f * deltaTime);
		yaw += Display.getDX() * 0.05f;
		pitch -= Display.getDY() * 0.05f;
		pitch = Math.min(Math.max(pitch, -90f), 90f);

		if (pitch > 90)
			pitch = 90;
		if (pitch < -90)
			pitch = -90;
		if (yaw < 0.0)
			yaw += 360.0;
		if (yaw > 360.0)
			yaw -= 360;

		if (Display.isKeyDown(GLFW.GLFW_KEY_W) || Display.isKeyDown(GLFW.GLFW_KEY_ENTER))
			position.add(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_S))
			position.sub(new Vector3f(forward).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_A))
			position.sub(new Vector3f(right).mul(cameraSpeed));
		if (Display.isKeyDown(GLFW.GLFW_KEY_D))
			position.add(new Vector3f(right).mul(cameraSpeed));
	}

	public Matrix4f getTransformation()
	{
		return new Matrix4f().lookAt(position, new Vector3f(forward).add(new Vector3f(position)), up);
	}
}
