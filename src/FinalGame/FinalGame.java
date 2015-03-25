package FinalGame;

import gameEngine.camera.OrbitCameraController;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.util.ArrayList;

import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;

public class FinalGame extends BaseGame {
	
	private IDisplaySystem display;
	private IRenderer renderer;
	private IEventManager eventManager;
	private IInputManager inputManager;
	
	private ICamera camera1, camera2;
	private SceneNode player1, player2;
	private OrbitCameraController camera1Controller, camera2Controller;
	private String keyboardName, gamepadName;

	protected void initSystem()
	{
		// Call local method to create Display System object
		display = createDisplaySystem();
		setDisplaySystem(display);

		// Create an Input Manager
		IInputManager inputManager = new InputManager();
		setInputManager(inputManager);

		// Create and empty game world
		ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
		setGameWorld(gameWorld);
	}
	
	private IDisplaySystem createDisplaySystem()
	{
		IDisplaySystem display = new UltraDisplaySystem(1920, 1080, 24, 20, true, "sage.renderer.jogl.JOGLRenderer");
		System.out.print("\nWaiting for display creation...");
		int count = 0;
		// wait until display creation completes or a timeout occurs
		while (!display.isCreated())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException("Display creation interrupted");
			}

			count++;
			System.out.print("+");
			if (count % 80 == 0)
			{
				System.out.println();
			}
			// 20 seconds (approximately)
			if (count > 2000)
			{
				throw new RuntimeException("Unable to create display");
			}
		}
		System.out.println();
		return display;
	}

	@Override
	protected void initGame()
	{
		createEssentialObjects();
		createPlayers();
		createGameWorldObjects();
	}

	private void createEssentialObjects() {
		// Get renderer
		renderer = display.getRenderer();
		
		// For managing collisions
		eventManager = EventManager.getInstance();
		
		// Get the display objects
		display = getDisplaySystem();
		display.setTitle("Multiplayer Treasure Hunt - Kevin Jones");

		// Get the input manager for inputs.
		inputManager = getInputManager();

		// Get keyboard, gamepad and mouse
		keyboardName = inputManager.getKeyboardName();
		gamepadName = inputManager.getFirstGamepadName();
		String mouseName = inputManager.getMouseName();
		
		// Setup both camera controllers
		camera1Controller = new OrbitCameraController(camera1, inputManager, player1, mouseName, false);
		camera2Controller = new OrbitCameraController(camera2, inputManager, player2, gamepadName, true);
	}
	
	private void createPlayers() {
		player1 = new Cube("PLAYER1");
		player1.scale(.5f, .5f, .5f);
		player1.translate(0, .5f, 50);
		player1.rotate(180, new Vector3D(0, 1, 0));
		addGameWorldObject(player1);
	
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(60, 2, 1, 1000);
		camera1.setViewport(0f, 1f, 0f, .45f);
	
		player2 = new Pyramid("PLAYER2");
		player2.scale(.5f, .5f, .5f);
		player2.translate(50, .5f, 0);
		player2.rotate(-90, new Vector3D(0, 1, 0));
		addGameWorldObject(player2);
	
		camera2 = new JOGLCamera(renderer);
		camera2.setPerspectiveFrustum(60, 2, 1, 1000);
		camera2.setViewport(0f, 1f, .55f, 1f);
	}

	private void createGameWorldObjects() {
		createWorldAxes();
	}

	private void createWorldAxes() {
		Point3D origin = new Point3D(0, 0, 0);
		Point3D xEnd = new Point3D(100, 0, 0);
		Point3D yEnd = new Point3D(0, 100, 0);
		Point3D zEnd = new Point3D(0, 0, 100);
		Line xAxis = new Line(origin, xEnd, Color.red, 2);
		Line yAxis = new Line(origin, yEnd, Color.green, 2);
		Line zAxis = new Line(origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis);
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);
	}

	@Override
	protected void update(float elapsedTimeMS)
	{		
		camera1Controller.update(elapsedTimeMS);
		camera2Controller.update(elapsedTimeMS);
		super.update(elapsedTimeMS);
	}
	
	@Override
	protected void render()
	{
		renderer.setCamera(camera1);
		super.render();
		renderer.setCamera(camera2);
		super.render();		
	}

	@Override
	protected void shutdown()
	{
		display.close();
		super.shutdown();
	}
}
