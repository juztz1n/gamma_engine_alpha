package zin.gammaEngine.core;

import zin.game.core.ExampleGame;

public class Main
{
	public static final int SAMPLES = 0;

	public static final double MAX_FPS = 5000.0;

	public static float FOV = 120.0f;
	public static final float Z_NEAR = 0.1f;
	public static final float Z_FAR = 1000.0f;

	public static final boolean MIPMAPPING = true;
	public static final boolean ANISOTROPIC_FILTERING = true;
	public static final int ANISOTROPIC_FILTERING_AMOUNT = 16;

	public static void main(String[] args)
	{
		CoreEngine coreEngine = new CoreEngine(MAX_FPS, new ExampleGame());
		coreEngine.createDisplay();
		coreEngine.startEngine();
	}

	public static float getFOV()
	{
		return (float) Math.toRadians(FOV);
	}
}
