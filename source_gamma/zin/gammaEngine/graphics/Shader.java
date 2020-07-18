package zin.gammaEngine.graphics;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import zin.gammaEngine.core.Logger;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class Shader
{
	public int identifier;
	public Map<String, Integer> uniforms = new HashMap<>();

	public Shader(String vertexPath, String fragmentPath, String geometryPath)
	{
		StringBuilder vertexShaderSource = new StringBuilder();
		try
		{
			InputStream stream = new FileInputStream(vertexPath);

			InputStreamReader in = new InputStreamReader(stream);

			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null)
			{
				vertexShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load vertex shader \"" + vertexPath + "\".");
			return;
		}
		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderID, vertexShaderSource);
		glCompileShader(vertexShaderID);
		if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(vertexShaderID, 500));
			Logger.error("Failed to compile vertex shader \"" + vertexPath + "\".");
			return;
		}

		StringBuilder fragmentShaderSource = new StringBuilder();
		try
		{
			InputStream stream = new FileInputStream(fragmentPath);

			InputStreamReader in = new InputStreamReader(stream);

			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null)
			{
				fragmentShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load fragment shader \"" + fragmentPath + "\".");
			return;
		}
		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID, fragmentShaderSource);
		glCompileShader(fragmentShaderID);
		if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(fragmentShaderID, 500));
			Logger.error("Failed to compile fragment shader \"" + fragmentPath + "\".");
			return;
		}

		StringBuilder geometryShaderSource = new StringBuilder();
		try
		{
			InputStream stream = new FileInputStream(geometryPath);

			InputStreamReader in = new InputStreamReader(stream);

			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null)
			{
				geometryShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load geometry shader \"" + geometryPath + "\".");
			return;
		}
		int geometryShaderID = glCreateShader(GL_GEOMETRY_SHADER);
		glShaderSource(geometryShaderID, geometryShaderSource);
		glCompileShader(geometryShaderID);
		if (glGetShaderi(geometryShaderID, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(geometryShaderID, 500));
			Logger.error("Failed to compile geometry shader \"" + geometryPath + "\".");
			return;
		}

		identifier = glCreateProgram();
		glAttachShader(identifier, vertexShaderID);
		glAttachShader(identifier, fragmentShaderID);
		glAttachShader(identifier, geometryShaderID);
		glLinkProgram(identifier);

		glDetachShader(identifier, vertexShaderID);
		glDetachShader(identifier, fragmentShaderID);
		glDetachShader(identifier, geometryShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteShader(geometryShaderID);

		GraphicsEngine.addShader(this);
	}

	public Shader(String vertexPath, String fragmentPath)
	{
		StringBuilder vertexShaderSource = new StringBuilder();
		try
		{
			InputStream stream = new FileInputStream(vertexPath);

			InputStreamReader in = new InputStreamReader(stream);

			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null)
			{
				vertexShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load vertex shader \"" + vertexPath + "\".");
			return;
		}
		int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderID, vertexShaderSource);
		glCompileShader(vertexShaderID);
		if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(vertexShaderID, 500));
			Logger.error("Failed to compile vertex shader \"" + vertexPath + "\".");
			return;
		}

		StringBuilder fragmentShaderSource = new StringBuilder();
		try
		{
			InputStream stream = new FileInputStream(fragmentPath);

			InputStreamReader in = new InputStreamReader(stream);

			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null)
			{
				fragmentShaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e)
		{
			Logger.error("Failed to load fragment shader \"" + fragmentPath + "\".");
			return;
		}
		int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID, fragmentShaderSource);
		glCompileShader(fragmentShaderID);
		if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE)
		{
			System.out.println(glGetShaderInfoLog(fragmentShaderID, 500));
			Logger.error("Failed to compile fragment shader \"" + fragmentPath + "\".");
			return;
		}

		identifier = glCreateProgram();
		glAttachShader(identifier, vertexShaderID);
		glAttachShader(identifier, fragmentShaderID);
		glLinkProgram(identifier);

		glDetachShader(identifier, vertexShaderID);
		glDetachShader(identifier, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);

		GraphicsEngine.addShader(this);
	}

	public void setAttribute(int index, String name)
	{
		glBindAttribLocation(identifier, index, name);
	}

	public boolean exists(String name)
	{
		return uniforms.get(name) != null;
	}

	public void addUniform(String name)
	{
		uniforms.put(name, glGetUniformLocation(identifier, name));
	}

	public void setUniform(String name, Vector2f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform2f(getUniform(name), value.x, value.y);
	}

	public void setUniform(String name, Vector3f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform3f(getUniform(name), value.x, value.y, value.z);
	}

	public void setUniform(String name, Matrix4f value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		value.get(buffer);
		glUniformMatrix4fv(getUniform(name), false, buffer);
	}

	public void setUniform(String name, float value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform1f(getUniform(name), value);
	}

	public void setUniform(String name, int value)
	{
		if (!exists(name))
			addUniform(name);

		bind();
		glUniform1i(getUniform(name), value);
	}

	public int getUniform(String name)
	{
		return uniforms.get(name);
	}

	public void bind()
	{
		glUseProgram(identifier);
	}

	public static void unbind()
	{
		glUseProgram(0);
	}

	public void destroy()
	{
		unbind();
		glDeleteProgram(identifier);
	}
}
