package zin.gammaEngine.core;

import java.util.ArrayList;
import java.util.List;

import zin.gammaEngine.graphics.Display;
import zin.gammaEngine.graphics.core.GraphicsEngine;

public class CoreEngine
{
	public static final String ENGINE_VERSION = "Alpha";

	private boolean isRunning;
	private Game game;
	private int width, height;
	private double frameTime;

	private boolean printInfo = false;

	private List<Integer> frameAvg = new ArrayList<>();
	private int totalFrames;
	private int ups, fps;
	private double passedTime;

	private GraphicsEngine graphicsEngine;

	public CoreEngine(double frameRate, Game game)
	{
		this.isRunning = false;
		this.game = game;
		this.frameTime = 1.0 / frameRate;
		this.width = game.getWidth();
		this.height = game.getHeight();

		graphicsEngine = new GraphicsEngine(this);

		game.setCoreEngine(this);
		game.setGraphicsEngine(graphicsEngine);
	}

	public void createDisplay()
	{
		Display.create(game.getTitle(), width, height, game.getState());
	}

	public void startEngine()
	{
		if (isRunning)
			return;

		isRunning = true;

		runEngine();
	}

	private void runEngine()
	{
		int updates = 0;
		int frames = 0;
		double frameCounter = 0;

		graphicsEngine.initialize();
		game.initialize();
		graphicsEngine.initializePostProcessing();

		if (printInfo)
		{
			Logger.info("Engine version: " + getVersion());
			Logger.info("Engine loop starting.");
		}

		double lastTime = getTime();
		double unprocessedTime = 0;

		while (isRunning)
		{
			boolean render = false;

			double startTime = getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime)
			{
				render = true;

				unprocessedTime -= frameTime;

				if (game.shouldClose() || Display.shouldClose())
					stopEngine();

				game.getRootObject().input(frameTime);
				game.input();
				Display.updateInput();

				updates++;
				game.getRootObject().update(frameTime);
				game.update();

				if (frameCounter >= 1.0)
				{
					if (printInfo)
					{
						frameAvg.add(frames);
						Logger.info("Game FPS: " + frames + ", " + " Game UPS: " + updates);
					}
					totalFrames += frames;
					this.passedTime = passedTime;
					this.ups = updates;
					this.fps = frames;
					updates = 0;
					frames = 0;
					frameCounter = 0;
				}
			}
			if (render)
			{
				graphicsEngine.render();
				Display.update();
				frames++;
			} else
			{
				try
				{
					Thread.sleep(1);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		destroyEngine();
	}

	public void stopEngine()
	{
		if (!isRunning)
			return;

		isRunning = false;
	}

	private void destroyEngine()
	{
		if (printInfo)
		{
			if (frameAvg.size() != 0)
			{
				int fpsAvg = 0;

				for (int fps : frameAvg)
					fpsAvg += fps;

				Logger.info("Engine has stopped with an avarage of " + (double) fpsAvg / (double) frameAvg.size()
						+ " frames per second and a total of " + totalFrames + " frames drawn.");
			} else
			{
				Logger.info("Engine has stopped.");
			}
		}

		graphicsEngine.destroy();
		game.getRootObject().destroy();
		System.exit(0);
	}

	public int getTotalFrames()
	{
		return totalFrames;
	}

	public double getPassedTime()
	{
		return passedTime;
	}

	public int getFPS()
	{
		return fps;
	}

	public int getUPS()
	{
		return ups;
	}

	public Game getGame()
	{
		return game;
	}

	public double getTime()
	{
		return (double) System.nanoTime() / (double) 1000000000L;
	}

	public void setPrintInfo(boolean printInfo)
	{
		this.printInfo = printInfo;
	}

	public static String getVersion()
	{
		return ENGINE_VERSION;
	}

	public GraphicsEngine getGraphicsEngine()
	{
		return graphicsEngine;
	}
}