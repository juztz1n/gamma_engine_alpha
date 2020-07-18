package zin.gammaEngine.graphics;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class ScreenQuad extends Mesh
{
	private static final float[] VERTICES =
	{ -1, 1, -1, -1, 1, 1, 1, -1 };

	public ScreenQuad()
	{
		super(VERTICES);
	}

	public void bind()
	{
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);

		glDisable(GL_DEPTH_TEST);
	}

	@Override
	public void draw()
	{
		bind();
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		unbind();
	}

	public void unbind()
	{
		glDisableVertexAttribArray(0);

		glBindVertexArray(0);

		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void destroy()
	{
		glDisableVertexAttribArray(0);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboID);

		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);
	}
}
