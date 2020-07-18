package zin.gammaEngine.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import zin.gammaEngine.core.Logger;
import zin.gammaEngine.core.Main;
import zin.gammaEngine.graphics.utils.TextureType;

public class Texture
{
	public int identifier;
	public int width, height, nrChannels;
	public String fileName;

	private boolean failed = false;

	public Texture(String fileName, TextureType type)
	{
		this.fileName = fileName;

		ByteBuffer image;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			STBImage.stbi_set_flip_vertically_on_load(true);
			image = STBImage.stbi_load(fileName, w, h, comp, STBImage.STBI_rgb_alpha);
			if (image == null)
			{
				Logger.error("Failed to load a texture file! \"" + fileName + "\"");
				failed = true;
				return;
			}

			width = w.get();
			height = h.get();
			nrChannels = comp.get();
		}

		identifier = GL11.glGenTextures();
		bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_REPEAT);
		if (Main.MIPMAPPING)
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		} else
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		if ((type == TextureType.NORMAL) || (type == TextureType.DISPLACEMENT))
		{
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, image);
		} else
		{
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_SRGB_ALPHA, width, height, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, image);
		}

		if (Main.MIPMAPPING)
		{
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		}

		if (Main.ANISOTROPIC_FILTERING)
		{
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic)
			{
				float amount = Math.min(Main.ANISOTROPIC_FILTERING_AMOUNT,
						GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
						amount);
			} else
			{
				Logger.warning("Anisotropic filtering is unsupported on your device.");
			}
		}
	}

	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, identifier);
	}

	public void unbind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void destroy()
	{
		GL11.glDeleteTextures(identifier);
	}

	public boolean hasFailed()
	{
		return failed;
	}
}
