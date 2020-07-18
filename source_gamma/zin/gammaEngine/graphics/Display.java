package zin.gammaEngine.graphics;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.DoubleBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import zin.gammaEngine.graphics.core.GraphicsEngine;
import zin.gammaEngine.graphics.utils.DisplayState;

public class Display
{
	private static long identifier;

	private static String title;
	private static int width, height;
	private static DisplayState state;

	private static int monitorWidth, monitorHeight, monitorRefreshRate;

	private static Vector2f scrollWheel = new Vector2f();

	private static boolean currentKeys[];
	private static boolean currentButtons[];

	private static boolean grabbed = false, focused = true;

	private static double newX, newY, prevX, prevY, dx, dy;
	private static boolean rotX, rotY;

	public static void create(String title, int width, int height, DisplayState state)
	{
		Display.title = title;
		Display.width = width;
		Display.height = height;
		Display.state = state;

		if (!glfwInit())
			throw new IllegalStateException("Failed to initialize GLFW.");

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		monitorWidth = videoMode.width();
		monitorHeight = videoMode.height();
		monitorRefreshRate = videoMode.refreshRate();

		glfwWindowHint(GLFW_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_VERSION_MINOR, 3);

		if (state == DisplayState.WINDOWED)
		{
			identifier = glfwCreateWindow(width, height, title, 0, 0);
			glfwSetWindowPos(identifier, (monitorWidth / 2) - width / 2, (monitorHeight / 2) - height / 2);
			glfwSetFramebufferSizeCallback(identifier, new GLFWFramebufferSizeCallback()
			{
				public void invoke(long identifier, int width, int height)
				{
					Display.width = width;
					Display.height = height;
					GL11.glViewport(0, 0, width, height);
					GraphicsEngine.updateProjectionMatrix();
				}
			});
		} else if (state == DisplayState.BORDERLESS)
		{
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
			identifier = glfwCreateWindow(monitorWidth, monitorHeight, title, 0, 0);

			Display.width = monitorWidth;
			Display.height = monitorHeight;
		} else if (state == DisplayState.FULLSCREEN)
		{
			identifier = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), 0);
		}

		glfwSetScrollCallback(identifier, new GLFWScrollCallback()
		{
			public void invoke(long identifier, double xOffset, double yOffset)
			{
				Display.scrollWheel.x += (float) xOffset;
				Display.scrollWheel.y += (float) yOffset;
			}
		});

		glfwSetWindowFocusCallback(identifier, new GLFWWindowFocusCallback()
		{
			@Override
			public void invoke(long identifier, boolean focused)
			{
				Display.focused = focused;
			}
		});

		glfwMakeContextCurrent(identifier);
		GL.createCapabilities();

		glfwSwapInterval(0);

		currentKeys = new boolean[GLFW_KEY_LAST];
		for (int i = 0; i < GLFW_KEY_LAST; i++)
			currentKeys[i] = false;

		currentButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
			currentButtons[i] = false;

		newX = Display.getWidth() / 2;
		newY = Display.getHeight() / 2;

		prevX = 0;
		prevY = 0;

		rotX = false;
		rotY = false;
	}

	public static void updateInput()
	{
		for (int i = 32; i < GLFW_KEY_LAST; i++)
			currentKeys[i] = isKeyDown(i);

		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++)
			currentButtons[i] = isMouseButtonDown(i);

		if (grabbed)
		{
			DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

			glfwGetCursorPos(Display.getIdentifier(), x, y);
			x.rewind();
			y.rewind();

			newX = x.get();
			newY = y.get();

			double deltaX = newX - Display.getWidth() / 2;
			double deltaY = newY - Display.getHeight() / 2;

			rotX = newX != prevX;
			rotY = newY != prevY;

			if (rotX)
			{
				dx = deltaX;
			}
			if (rotY)
			{
				dy = deltaY;
			}

			prevX = newX;
			prevY = newY;

			glfwSetCursorPos(Display.getIdentifier(), Display.getWidth() / 2, Display.getHeight() / 2);
		} else
		{
			dy = dx = 0;
		}
	}

	public static void update()
	{
		glfwPollEvents();
		glfwSwapBuffers(identifier);
	}

	public static void destroy()
	{
		glfwDestroyWindow(identifier);
		glfwTerminate();
	}

	public static void setTitle(String title)
	{
		Display.title = title;
		glfwSetWindowTitle(identifier, title);
	}

	public static String getTitle()
	{
		return title;
	}

	public static void setWidth(int width)
	{
		Display.width = width;
		glfwSetWindowSize(identifier, width, height);
	}

	public static int getWidth()
	{
		return width;
	}

	public static void setHeight(int height)
	{
		Display.height = height;
		glfwSetWindowSize(identifier, width, height);
	}

	public static int getHeight()
	{
		return height;
	}

	public static DisplayState getState()
	{
		return state;
	}

	public static int getMonitorWidth()
	{
		return monitorWidth;
	}

	public static int getMonitorHeight()
	{
		return monitorHeight;
	}

	public static int getMonitorRefreshRate()
	{
		return monitorRefreshRate;
	}

	public static boolean isKeyDown(int keyCode)
	{
		return glfwGetKey(Display.getIdentifier(), keyCode) == 1;
	}

	public static boolean isKeyPressed(int keyCode)
	{
		return (isKeyDown(keyCode) && !currentKeys[keyCode]);
	}

	public static boolean isKeyReleased(int keyCode)
	{
		return (!isKeyDown(keyCode) && currentKeys[keyCode]);
	}

	public static boolean isMouseButtonDown(int keyCode)
	{
		return glfwGetMouseButton(Display.getIdentifier(), keyCode) == 1;
	}

	public static boolean isMouseButtonPressed(int keyCode)
	{
		return (isMouseButtonDown(keyCode) && !currentButtons[keyCode]);
	}

	public static boolean isMouseButtonReleased(int keyCode)
	{
		return (!isMouseButtonDown(keyCode) && currentButtons[keyCode]);
	}

	public static double getDX()
	{
		return dx;
	}

	public static double getDY()
	{
		return dy;
	}

	public static void setMouseGrabbed(boolean grabbed)
	{
		Display.grabbed = grabbed;

		if (grabbed)
		{
			glfwSetInputMode(Display.getIdentifier(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		} else
		{
			glfwSetInputMode(Display.getIdentifier(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		glfwSetCursorPos(Display.getIdentifier(), Display.getWidth() / 2, Display.getHeight() / 2);
	}

	public static boolean isMouseGrabbed()
	{
		return grabbed;
	}

	public static Vector2f getMousePosition()
	{
		double[] x = new double[1], y = new double[1];
		glfwGetCursorPos(Display.getIdentifier(), x, y);
		return new Vector2f((float) x[0], (float) y[0]);
	}

	public static Vector2f getMouseWheel()
	{
		return scrollWheel;
	}

	public static boolean isFocused()
	{
		return focused;
	}

	public static void setOpacity(float opacity)
	{
		glfwSetWindowOpacity(identifier, opacity);
	}

	public static float getOpacity()
	{
		return glfwGetWindowOpacity(identifier);
	}

	public static long getIdentifier()
	{
		return identifier;
	}

	public static boolean shouldClose()
	{
		return glfwWindowShouldClose(identifier);
	}

	public static float getAspectRatio()
	{
		return (float) width / (float) height;
	}
}
