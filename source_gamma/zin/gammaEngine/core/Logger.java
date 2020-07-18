package zin.gammaEngine.core;

public class Logger
{
	public static void info(String message)
	{
		System.out.println("[Gamma|INFO] " + message);
	}

	public static void warning(String message)
	{
		System.out.println("[Gamma|WARNING] " + message);
	}

	public static void error(String message)
	{
		System.err.println("[Gamma|ERROR] " + message);
	}
}
