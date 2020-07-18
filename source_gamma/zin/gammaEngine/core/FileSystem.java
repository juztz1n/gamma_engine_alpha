package zin.gammaEngine.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import zin.gammaEngine.graphics.Material;
import zin.gammaEngine.graphics.components.RenderComponent;

public class FileSystem
{
	public static RenderComponent loadRenderComponent(String fileName)
	{
		String[] info =
		{ "", "", "", "", "", "0", "32", "0", "1" };

		InputStream stream = null;
		try
		{
			stream = new FileInputStream("resources/model_files/" + fileName + ".gmf");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		InputStreamReader in = new InputStreamReader(stream);

		BufferedReader reader = new BufferedReader(in);
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				if (line.split(" ").length > 1)
				{
					if (line.split(" ")[0].toLowerCase().contains("model_file"))
					{
						info[0] = line.replace("model_file ", "");
					} else if (line.split(" ")[0].toLowerCase().contains("diffuse_texture"))
					{
						info[1] = line.replace("diffuse_texture ", "");
					} else if (line.split(" ")[0].toLowerCase().contains("gloss_texture"))
					{
						info[2] = line.replace("gloss_texture ", "");
					} else if (line.split(" ")[0].toLowerCase().contains("normal_texture"))
					{
						info[3] = line.replace("normal_texture ", "");
					} else if (line.split(" ")[0].toLowerCase().contains("displacement_texture"))
					{
						info[4] = line.replace("displacement_texture ", "");
					} else if (line.split(" ")[0].toLowerCase().contains("specular_intensity"))
					{
						info[5] = line.split(" ")[1].replaceAll("\"", "");
					} else if (line.split(" ")[0].toLowerCase().contains("specular_dampening"))
					{
						info[6] = line.split(" ")[1].replaceAll("\"", "");
					} else if (line.split(" ")[0].toLowerCase().contains("parallax_scale"))
					{
						info[7] = line.split(" ")[1].replaceAll("\"", "");
					} else if (line.split(" ")[0].equalsIgnoreCase("parallax_offset"))
					{
						info[8] = line.split(" ")[1].replaceAll("\"", "");
					}
				}
			}
			reader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return new RenderComponent(info[0], new Material(info[1], info[2], info[3], info[4], Float.valueOf(info[5]),
				Float.valueOf(info[6]), Float.valueOf(info[7]), Float.valueOf(info[8])));
	}
}
