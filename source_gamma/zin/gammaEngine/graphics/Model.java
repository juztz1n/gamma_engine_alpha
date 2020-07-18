package zin.gammaEngine.graphics;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import zin.gammaEngine.core.Logger;

import static org.lwjgl.assimp.Assimp.*;

public class Model
{
	private AIScene scene;
	private List<Mesh> meshes = new ArrayList<>();

	public Model(final String fileName)
	{
		scene = aiImportFile(fileName, aiProcess_Triangulate | aiProcess_CalcTangentSpace);

		if (scene == null)
		{
			Logger.error("Failed to load scene \"" + fileName + "\".");
			return;
		}

		for (int i = 0; i < scene.mNumMeshes(); i++)
		{
			AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
			processMesh(mesh);
		}
	}

	private void processMesh(final AIMesh mesh)
	{
		float[] vertices = new float[mesh.mNumVertices() * 3];
		float[] texCoords = new float[mesh.mTextureCoords(0).capacity() * 2];
		float[] normals = new float[mesh.mNormals().capacity() * 3];
		float[] tangents = new float[mesh.mTangents().capacity() * 3];
		float[] bitangents = new float[mesh.mBitangents().capacity() * 3];
		int[] indices = new int[mesh.mFaces().capacity() * 3];

		int index = 0;

		for (int i = 0; i < mesh.mNumVertices(); i++)
		{
			vertices[index++] = mesh.mVertices().get(i).x();
			vertices[index++] = mesh.mVertices().get(i).y();
			vertices[index++] = mesh.mVertices().get(i).z();
		}

		index = 0;

		for (int i = 0; i < mesh.mNormals().capacity(); i++)
		{
			normals[index++] = mesh.mNormals().get(i).x();
			normals[index++] = mesh.mNormals().get(i).y();
			normals[index++] = mesh.mNormals().get(i).z();
		}

		index = 0;

		for (int i = 0; i < mesh.mTangents().capacity(); i++)
		{
			tangents[index++] = mesh.mTangents().get(i).x();
			tangents[index++] = mesh.mTangents().get(i).y();
			tangents[index++] = mesh.mTangents().get(i).z();
		}

		index = 0;

		for (int i = 0; i < mesh.mBitangents().capacity(); i++)
		{
			bitangents[index++] = mesh.mBitangents().get(i).x();
			bitangents[index++] = mesh.mBitangents().get(i).y();
			bitangents[index++] = mesh.mBitangents().get(i).z();
		}

		index = 0;

		for (int i = 0; i < mesh.mNumVertices(); i++)
		{
			texCoords[index++] = mesh.mTextureCoords(0).get(i).x();
			texCoords[index++] = mesh.mTextureCoords(0).get(i).y();
		}

		index = 0;

		IntBuffer buffer = BufferUtils.createIntBuffer(mesh.mNumFaces() * 3);
		for (int i = 0; i < mesh.mNumFaces(); i++)
		{
			AIFace face = mesh.mFaces().get(i);
			buffer.put(face.mIndices());
		}

		buffer.flip();

		for (int i = 0; i < buffer.capacity(); i++)
		{
			indices[i] = buffer.get(i);
		}

		meshes.add(new Mesh(vertices, texCoords, normals, tangents, indices));
	}

	public void draw()
	{
		for (Mesh mesh : meshes)
		{
			mesh.draw();
		}
	}

	public void destroy()
	{
		for (Mesh mesh : meshes)
		{
			mesh.destroy();
		}
	}
}
