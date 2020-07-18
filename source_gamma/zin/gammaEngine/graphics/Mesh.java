package zin.gammaEngine.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

public class Mesh
{
	protected int vaoID, vboID, tcbID, nboID, tboID, iboID, vertexCount;

	public Mesh(float[] vertices, float[] texCoords, float[] normals, float[] tangents, int[] indices)
	{
		vertexCount = indices.length;

		FloatBuffer verticesBuffer = null;
		FloatBuffer texCoordsBuffer = null;
		FloatBuffer normalsBuffer = null;
		FloatBuffer tangentsBuffer = null;
		IntBuffer indicesBuffer = null;

		try
		{
			vaoID = glGenVertexArrays();
			glBindVertexArray(vaoID);

			vboID = glGenBuffers();
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			tcbID = glGenBuffers();
			texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
			texCoordsBuffer.put(texCoords).flip();
			glBindBuffer(GL_ARRAY_BUFFER, tcbID);
			glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			nboID = glGenBuffers();
			normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
			normalsBuffer.put(normals).flip();
			glBindBuffer(GL_ARRAY_BUFFER, nboID);
			glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			tboID = glGenBuffers();
			tangentsBuffer = MemoryUtil.memAllocFloat(tangents.length);
			tangentsBuffer.put(tangents).flip();
			glBindBuffer(GL_ARRAY_BUFFER, tboID);
			glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			iboID = glGenBuffers();
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboID);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindVertexArray(0);
		} finally
		{
			if (verticesBuffer != null)
				MemoryUtil.memFree(verticesBuffer);
			if (texCoordsBuffer != null)
				MemoryUtil.memFree(texCoordsBuffer);
			if (normalsBuffer != null)
				MemoryUtil.memFree(normalsBuffer);
			if (tangentsBuffer != null)
				MemoryUtil.memFree(tangentsBuffer);
			if (indicesBuffer != null)
				MemoryUtil.memFree(indicesBuffer);
		}
	}

	public Mesh(float[] vertices)
	{
		vertexCount = vertices.length / 2;

		FloatBuffer verticesBuffer = null;

		try
		{
			vaoID = glGenVertexArrays();
			glBindVertexArray(vaoID);

			vboID = glGenBuffers();
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
			verticesBuffer.put(vertices).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			glBindVertexArray(0);
		} finally
		{
			if (verticesBuffer != null)
				MemoryUtil.memFree(verticesBuffer);
		}
	}

	public void draw()
	{
		glBindVertexArray(vaoID);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);

		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);

		glBindVertexArray(0);
	}

	public int getVaoID()
	{
		return vaoID;
	}

	public int getVertexCount()
	{
		return vertexCount;
	}

	public void destroy()
	{
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboID);
		glDeleteBuffers(tcbID);
		glDeleteBuffers(nboID);
		glDeleteBuffers(tboID);
		glDeleteBuffers(iboID);

		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);
	}
}
