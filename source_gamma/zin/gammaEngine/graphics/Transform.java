package zin.gammaEngine.graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform
{
	public Vector3f position, scale;
	public Quaternionf rotation;

	public Transform(Vector3f position, Quaternionf rotation, Vector3f scale)
	{
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Transform()
	{
		position = new Vector3f();
		rotation = new Quaternionf();
		scale = new Vector3f(1);
	}

	public Transform(Transform src)
	{
		position = src.position;
		rotation = src.rotation;
		scale = src.scale;
	}

	public Matrix4f getTransformation()
	{
		Matrix4f position = new Matrix4f().translate(this.position);
		Matrix4f rotation = new Matrix4f().rotate(this.rotation);
		Matrix4f scale = new Matrix4f().scale(this.scale);

		return position.mul(rotation).mul(scale);
	}
}
