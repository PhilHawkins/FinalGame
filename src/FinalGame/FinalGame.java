package FinalGame;

import gameEngine.actions.QuitGameAction;
import gameEngine.networking.FinalGameClient;
import gameEngine.networking.GhostAvatar;
import gameEngine.phil.input.action.object.MoveObjectAction;
import gameEngine.phil.input.action.object.MoveObjectBackwardAction;
import gameEngine.phil.input.action.object.MoveObjectForwardAction;
import gameEngine.phil.input.action.object.RotateObjectAction;
import gameEngine.phil.input.action.object.RotateObjectLeft;
import gameEngine.phil.input.action.object.RotateObjectRight;
import gameEngine.phil.camera.OrbitCameraController;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.IInputManager.INPUT_ACTION_TYPE;
import sage.input.action.IAction;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SkyBox;
import sage.scene.SkyBox.Face;
import sage.scene.shape.Cylinder;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Sphere;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.HillHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class FinalGame extends BaseGame {
	
	private IDisplaySystem display;
	private IRenderer renderer;
	private IEventManager eventManager;
	private IInputManager inputManager;
	
	private boolean foundController;
	
	private IAction quitGameAction;
	private IAction movePlayerForward, movePlayerBackward, movePlayerLeft, movePlayerRight;
	private IAction p2movePlayerForwardBackward, p2movePlayerLeftRight;
	private IAction orbitCameraLeft, orbitCameraRight, orbitCameraForward, orbitCameraBackward;
	private IAction p2orbitCameraLeftRight, p2orbitCameraUpDown;
	private IAction zoomInCamera, zoomOutCamera, p2ZoomInCamera, p2ZoomOutCamera;
	
	private ICamera camera1, camera2;
	private SceneNode player1, player2;
	private Group scene;
	private OrbitCameraController camera1Controller, camera1GPController;
	private String keyboardName, gamepadName;
	SkyBox skyBox;
	
	private static String imagesDirectory = "." + File.separator + "bin" + File.separator + "images" + File.separator;
	private static String scriptsDirectory = "." + File.separator + "bin" + File.separator + "scripts" + File.separator;
	
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	
	private FinalGameClient thisClient;
	private boolean isConnected;
	
	private HashMap<UUID, GhostAvatar> ghostAvatars;
	
	public TerrainBlock hillTerrain;
	
	public FinalGame(String serverAddr, int sPort){
		super();
		isConnected = false;
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
		ghostAvatars = new HashMap<UUID, GhostAvatar>();
		
	}

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
		IDisplaySystem display = new UltraDisplaySystem(1920, 1080, 24, 20, false, "sage.renderer.jogl.JOGLRenderer");
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
		try
		 { 
			thisClient = new FinalGameClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this); 
		 }
		 catch (UnknownHostException e) { 
			 e.printStackTrace(); 
		}
		 catch (IOException e) { 
			 e.printStackTrace(); 
		}
		 
		if (thisClient != null) 
		{ 
			thisClient.sendJoinMessage(); 
		}
		
		createScene();
		createEssentialObjects();
		createPlayers();
		initTerrain();
		initGameElements();
		//linkActionsToControls();
		createGameWorldObjects();
	}

	private void initTerrain() {
//		HillHeightMap hhm = new HillHeightMap(50, 15, 15.0f, 16.0f,(byte)2, 12345);
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap(imagesDirectory + "/circle2.jpg");
//		hhm.setHeightScale(0.1f);
		 hillTerrain = createTerrainBlock(heightMap);
		 // create texture and texture state to color the terrain
		 TextureState groundState;
		 Texture groundTexture = TextureManager.loadTexture2D(imagesDirectory + "/Craterscape.jpg");
		 groundTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		 groundState = (TextureState) display.getRenderer().createRenderState(RenderStateType.Texture);
		 groundState.setTexture(groundTexture,0);
		 groundState.setEnabled(true);
		 // apply the texture to the terrain
		 hillTerrain.setRenderState(groundState);
		 addGameWorldObject(hillTerrain);
		
	}

	private TerrainBlock createTerrainBlock(ImageBasedHeightMap heightMap) {
		float heightScale = 0.1f;
		Vector3D terrainScale = new Vector3D(1, heightScale, 1);
		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();
		// specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(-0, 0.0f, -0);
		// create a terrain block using the height map
		String name = "Terrain:" + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
		return tb;
	}

	private void createEssentialObjects() {
		// Get renderer
		renderer = display.getRenderer();
		
		// For managing collisions
		eventManager = EventManager.getInstance();
	}

	private void createPlayers() {
		player1 = new Sphere();
		player1.scale(.5f, .5f, .5f);
		player1.translate(0, 3f, 5);
		player1.rotate(180, new Vector3D(0, 1, 0));
		//addGameWorldObject(player1);
		scene.addChild(player1);
	
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(130, 2, .1, 1000);
		camera1.setViewport(0f, 1f, 0f, 1f);
	
//		player2 = new Pyramid("PLAYER2");
//		player2.scale(.5f, .5f, .5f);
//		player2.translate(5, .5f, 0);
//		player2.rotate(-90, new Vector3D(0, 1, 0));
////		addGameWorldObject(player2);
//		scene.addChild(player2);
//	
//		camera2 = new JOGLCamera(renderer);
//		camera2.setPerspectiveFrustum(90, 2, .1, 1000);
//		camera2.setViewport(0f, 1f, 0f, .45f);
	}

	private void initGameElements(){
		// Get the display objects
		display = getDisplaySystem();
		display.setTitle("SUMO! Phil Hawkins and Kevin Jones");

		// Get the input manager for inputs.
		inputManager = getInputManager();

		// Get keyboard, gamepad and mouse
		keyboardName = inputManager.getKeyboardName();
		gamepadName = inputManager.getFirstGamepadName();
		
		if(gamepadName != null){
			foundController = true;
		}
		
//		String mouseName = inputManager.getMouseName();
		
		// Setup both camera controllers
//		camera1Controller = new OrbitCameraController(camera1, inputManager, player1, mouseName);
//		camera2Controller = new OrbitCameraController(camera2, inputManager, player2, gamepadName);
		
		// Setup Phil's camera controllers
		camera1Controller = new OrbitCameraController(camera1, player1, inputManager, keyboardName, true);
		
//		if(foundController)
//			camera1GPController = new OrbitCameraController(camera1, player1, inputManager, keyboardName, false);
		
		//camera2Controller = new OrbitCameraController(camera2, player2, inputManager, gamepadName, false);
		
		// initialize Phil's actions
			

		RotateObjectLeft rotatePlayer1Left = new RotateObjectLeft(player1);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.A, 
				rotatePlayer1Left, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		RotateObjectRight rotatePlayer1Right = new RotateObjectRight(player1);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.D, 
				rotatePlayer1Right, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		MoveObjectForwardAction movePlayer1Forward = new MoveObjectForwardAction(player1, hillTerrain, thisClient);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.W, 
				movePlayer1Forward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		MoveObjectBackwardAction movePlayer1Backward = new MoveObjectBackwardAction(player1, hillTerrain, thisClient);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.S,
				movePlayer1Backward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
//		if(gamepadName != null){
//			RotateObjectAction rotateP2 = new RotateObjectAction(player2);
//			MoveObjectAction moveP2 = new MoveObjectAction(player2);
//			inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Axis.X,
//					rotateP2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//			inputManager.associateAction(gamepadName, net.java.games.input.Component.Identifier.Axis.Y, 
//					moveP2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
//		}
		
		quitGameAction = new QuitGameAction(this);
		inputManager.associateAction(keyboardName, Identifier.Key.ESCAPE, quitGameAction, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
		
		// Initalize actions
//		quitGameAction = new QuitGameAction(this);
//		
//		orbitCameraLeft = new OrbitCameraAction(camera1Controller, MoveDirections.LEFT);
//		orbitCameraRight = new OrbitCameraAction(camera1Controller, MoveDirections.RIGHT);
//		orbitCameraForward = new OrbitCameraAction(camera1Controller, MoveDirections.FORWARD);
//		orbitCameraBackward = new OrbitCameraAction(camera1Controller, MoveDirections.BACKWARD);
//		
//		zoomInCamera = new ZoomCameraAction(camera1Controller, ZoomTypes.ZOOM_IN);
//		zoomOutCamera = new ZoomCameraAction(camera1Controller, ZoomTypes.ZOOM_OUT);
//	
//		movePlayerForward = new MovePlayerAction(player1, MoveDirections.FORWARD);
//		movePlayerBackward = new MovePlayerAction(player1, MoveDirections.BACKWARD);
//		movePlayerLeft = new MovePlayerAction(player1, MoveDirections.LEFT);
//		movePlayerRight = new MovePlayerAction(player1, MoveDirections.RIGHT);
//		
//		p2movePlayerForwardBackward = new MovePlayerAction(player2, MoveDirections.LEFTSTICKX);
//		p2movePlayerLeftRight = new MovePlayerAction(player2, MoveDirections.LEFTSTICKY);
//		p2orbitCameraUpDown = new OrbitCameraAction(camera2Controller, MoveDirections.RIGHTSTICKY);
//		p2orbitCameraLeftRight = new OrbitCameraAction(camera2Controller, MoveDirections.RIGHTSTICKX);	
//		
//		p2ZoomInCamera = new ZoomCameraAction(camera2Controller, ZoomTypes.ZOOM_IN);
//		p2ZoomOutCamera = new ZoomCameraAction(camera2Controller, ZoomTypes.ZOOM_OUT);
	}
	
	private void linkActionsToControls()
	{
		// Escape to quit button
		inputManager.associateAction(keyboardName, Identifier.Key.ESCAPE, quitGameAction, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		// Keyboard Move
		inputManager.associateAction(keyboardName, Identifier.Key.W, movePlayerForward, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.S, movePlayerBackward, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.A, movePlayerLeft, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.D, movePlayerRight, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		// Keyboard Orbit
		inputManager.associateAction(keyboardName, Identifier.Key.UP, orbitCameraForward, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.DOWN, orbitCameraBackward, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.LEFT, orbitCameraLeft, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(keyboardName, Identifier.Key.RIGHT, orbitCameraRight, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		//Keyboard Zoom In/Out
		inputManager.associateAction(keyboardName, Identifier.Key.Z, zoomInCamera, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		inputManager.associateAction(keyboardName, Identifier.Key.X, zoomOutCamera, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	
		// Gamepad back button escape. First gamepad code. If gamepad not found exit game.
		try
		{
			inputManager.associateAction(gamepadName, Identifier.Button._6, quitGameAction, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		}
		catch (Exception e)
		{
			System.err.println("Turn your controller back on!");
			System.exit(0);
		}
	
		// Gamepad Move
		inputManager.associateAction(gamepadName, Identifier.Axis.X, p2movePlayerForwardBackward, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(gamepadName, Identifier.Axis.Y, p2movePlayerLeftRight, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// Gamepad pitch and yaw
		inputManager.associateAction(gamepadName, Identifier.Axis.RX, p2orbitCameraLeftRight, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		inputManager.associateAction(gamepadName, Identifier.Axis.RY, p2orbitCameraUpDown, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// Gamepad Zoom In/out
		inputManager.associateAction(gamepadName, Identifier.Button._4, p2ZoomInCamera, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		inputManager.associateAction(gamepadName, Identifier.Button._5, p2ZoomOutCamera, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	private void createGameWorldObjects() {
		createWorldAxes();
		
		Cylinder ground = new Cylinder();
		ground.setCullMode(CULL_MODE.NEVER);
		ground.setRadius(100);
		ground.setSlices(20);
		ground.setSolid(true);
		ground.setColor(Color.gray);
		ground.rotate(90, new Vector3D(1, 0, 0));
		ground.translate(50, 2f, 50);
		//addGameWorldObject(ground);
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

	private void createScene()
	{		
		ScriptEngineManager factory = new ScriptEngineManager(); 
		String sceneFileName = scriptsDirectory + "CreateScene.js";	
		
		// get the JavaScript engine
		ScriptEngine jsEngine = factory.getEngineByName("js");
		
		// run the script
		this.executeScript(jsEngine, sceneFileName);
		
		scene = (Group)jsEngine.get("scene");
		skyBox = (SkyBox)jsEngine.get("skyBox");
		
		addGameWorldObject(scene);
	}
	
	private void executeScript(ScriptEngine engine, String scriptFileName) 
	{
		try
		{ 
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader);
			fileReader.close(); 
		}
		//execute the script statements in the file
		catch (FileNotFoundException e1)
		{ 
			System.out.println(scriptFileName + " not found " + e1); 
		}
		catch (IOException e2)
		{ 
			System.out.println("IO problem with " + scriptFileName + e2); 
		} 
		catch (ScriptException e3)
		{ 
			System.out.println("ScriptException in " + scriptFileName + e3); 
		} 
		catch (NullPointerException e4)
		{ 
			System.out.println ("Null ptr exception in " + scriptFileName + e4); 
		}
	}

	@Override
	protected void update(float elapsedTimeMS)
	{		
		Point3D cameraLocation = camera1.getLocation();
		Matrix3D cameraTranslation = new Matrix3D();
		cameraTranslation.translate(cameraLocation.getX(), cameraLocation.getY(), cameraLocation.getZ());
		skyBox.setLocalTranslation(cameraTranslation);		
		
		camera1Controller.update(elapsedTimeMS);
//		camera2Controller.update(elapsedTimeMS);
		super.update(elapsedTimeMS);
		
		if(thisClient != null){
			thisClient.processPackets();
			//thisClient.sendMoveMessage(getPlayerPosition());
		}
		
	}
	
	@Override
	protected void render()
	{
		renderer.setCamera(camera1);
		super.render();
	}

	@Override
	protected void shutdown()
	{
		display.close();
		super.shutdown();
		if(thisClient != null)
		 { 
			thisClient.sendByeMessage();
			 try { 
				 	thisClient.shutdown(); 
				} // shutdown() is inherited
			catch (IOException e) { 
				e.printStackTrace(); 
			}
		} 

	}

	public void setIsConnected(boolean b) {
		// TODO Auto-generated method stub
		isConnected = b;
	}

	public Vector3D getPlayerPosition() {
		// TODO Auto-generated method stub
		
		Vector3D pos = player1.getWorldTransform().getCol(3);
		
		return new Vector3D(pos.getX(), pos.getY(), pos.getZ());
	}

	public void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub

		if(ghostAvatars.containsKey(ghostID)){
			removeGameWorldObject(ghostAvatars.get(ghostID).getSceneNode());
			ghostAvatars.remove(ghostID);
		}
	}

	public void createGhostAvatar(UUID ghostID, String[] ghostPosition) {
		// TODO Auto-generated method stub
		GhostAvatar newAvatar = new GhostAvatar(ghostID, ghostPosition);
		ghostAvatars.put(ghostID, newAvatar);
		addGameWorldObject(newAvatar.getSceneNode());
	}

	public void updateGhostAvatar(UUID ghostID, String[] ghostPosition) {
		// TODO Auto-generated method stub
		GhostAvatar ghost = ghostAvatars.get(ghostID);
		if(ghost != null){
			ghost.setPosition(ghostPosition);
		}
	}
	
	public boolean getGhost(UUID id){
		if(ghostAvatars.containsKey(id)){
			return true;
		}
		else{
			return false;
		}
	}



}
