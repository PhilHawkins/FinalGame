package FinalGame;

import events.SinkEvent;
import events.SinkListener;
import gameEngine.actions.QuitGameAction;
import gameEngine.networking.FinalGameClient;
import gameEngine.networking.GhostAvatar;
import gameEngine.phil.input.action.object.MoveObjectBackwardAction;
import gameEngine.phil.input.action.object.MoveObjectForwardAction;
import gameEngine.phil.input.action.object.RotateObjectLeft;
import gameEngine.phil.input.action.object.RotateObjectRight;
import gameEngine.phil.camera.OrbitCameraController;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.IInputManager.INPUT_ACTION_TYPE;
import sage.input.action.IAction;
import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SkyBox;
import sage.scene.shape.Cylinder;
import sage.scene.shape.Line;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class FinalGame extends BaseGame
{
	private CollisionDispatcher collDispatcher;
	private BroadphaseInterface broadPhaseHandler;
	private ConstraintSolver solver;
	private CollisionConfiguration collConfig;
	private RigidBody physicsGround;
	private RigidBody physicsBall;
	private int maxProxies = 1024;
	private Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
	private Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
	private DynamicsWorld physicsWorld;

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
	private SceneNode player1;
	private boolean IsPlayerAlive;
	private Group scene;
	private OrbitCameraController camera1Controller, camera1GPController;
	private String keyboardName, gamepadName;
	private HashMap<UUID, GhostAvatar> ghostAvatars;

	SkyBox skyBox;
	OBJLoader objectLoader;
	private Cylinder ground;
	public TerrainBlock hillTerrain;
	private float groundHeight;

	private static String imagesDirectory = "." + File.separator + "bin" + File.separator + "images" + File.separator;
	private static String scriptsDirectory = "." + File.separator + "bin" + File.separator + "scripts" + File.separator;
	private static String modelsDirectory = "." + File.separator + "bin" + File.separator + "models" + File.separator;
	private static String soundsDirectory = "." + File.separator + "bin" + File.separator + "sounds" + File.separator;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private FinalGameClient thisClient;
	private boolean isConnected;

	private IPhysicsEngine physicsEngine;
	private IPhysicsObject ballP, groundP;
	private boolean running;
	private float gameTime;
	private float dropTime;
	
	Group model;
	Model3DTriMesh alien;

	HUDString playersSunkString, timeRemainingString;
	int timeElaspsed;
	boolean IsDeathTime = false;
	private static final int TimeUntilDeath = 10;
	
	private String planetChoice;

	private int playersSunk;

	// Sounds
	IAudioManager audioManager;
	Sound backgroundSound, countdownSound;

	public FinalGame(String serverAddr, int sPort)
	{
		super();
		
		planetChoice = getPlantetChoice();
		
		isConnected = false;
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
		ghostAvatars = new HashMap<UUID, GhostAvatar>();
		dropTime = 0;
		playersSunk = 0;
	}

	private String getPlantetChoice()
	{
		int selectedPlanet = 1;
		System.out.println("Select a planet:\n 1. Earth\n 2. Saturn\n 3. Mercury\n 4. Jupiter\n 5. Uranus\n 6. Venus\n 7. Neptune\n 8. Mars\n 9. Pluto");
		Scanner scanner = new Scanner(System.in);
		selectedPlanet = scanner.nextInt();
		scanner.close();
		
		switch (selectedPlanet)
		{
		case 1:
			return "earth.png";	
		case 2:
			return "saturn.jpg";
		case 3:
			return "mercury.jpeg";
		case 4:
			return "jupiter.jpeg";
		case 5: 
			return "uranus.jpg";
		case 6: 
			return "venus.jpeg";
		case 7: 
			return "neptune.jpg";
		case 8:
			return "mars.jpg";
		case 9: 
			return "pluto.jpeg";
		default:
			return "earth.png";
		}
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
		 thisClient = new
		 FinalGameClient(InetAddress.getByName(serverAddress), serverPort,
		 serverProtocol, this);
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
		createPlayerHUDs();
		initTerrain();
		initGameElements();
		// linkActionsToControls();
		createGameWorldObjects();

		gameTime = 0;
		dropTime = 0;

		createPhysicsWorld();
		initAudio();
	}

	private void createPlayerHUDs()
	{
		playersSunkString = new HUDString("Players Sunk: 0");
		playersSunkString.setLocation(0.01, 0.06);
		playersSunkString.setColor(Color.YELLOW);
		playersSunkString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		playersSunkString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera1.addToHUD(playersSunkString);

		timeRemainingString = new HUDString("Time Until Death: " + TimeUntilDeath + " Seconds");
		timeRemainingString.setLocation(.85, 0.06);
		timeRemainingString.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		timeRemainingString.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		timeRemainingString.setColor(Color.YELLOW);
		camera1.addToHUD(timeRemainingString);
	}

	private void initAudio()
	{
		AudioResource audioResource1, audioResource2;
		audioManager = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");

		if (!audioManager.initialize())
		{
			System.out.println("Audio Manager Failed to Initialize!");
			return;
		}

		audioResource1 = audioManager.createAudioResource(soundsDirectory + "BackgroundMusic.wav", AudioResourceType.AUDIO_SAMPLE);
		backgroundSound = new Sound(audioResource1, SoundType.SOUND_MUSIC, 100, true);
		backgroundSound.initialize(audioManager);
		backgroundSound.setMaxDistance(50.0f);
		backgroundSound.setMinDistance(3.0f);
		backgroundSound.setRollOff(5.0f);
		backgroundSound.setLocation(new Point3D(0, 0, 0));

		audioResource2 = audioManager.createAudioResource(soundsDirectory + "Countdown.wav", AudioResourceType.AUDIO_SAMPLE);
		countdownSound = new Sound(audioResource2, SoundType.SOUND_EFFECT, 50, false);
		countdownSound.initialize(audioManager);
		countdownSound.setMaxDistance(20.0f);
		countdownSound.setMinDistance(3.0f);
		countdownSound.setRollOff(5.0f);
		countdownSound.setLocation(new Point3D(0, 0, 0));

		setEarParameters();

		backgroundSound.play();
		countdownSound.play();
	}

	private void setEarParameters()
	{
		Matrix3D avatarDirection = (Matrix3D) player1.getWorldRotation().clone();
		float cameraAzimuth = (float) camera1Controller.getAzimuth();
		avatarDirection.rotateY(180.0f - cameraAzimuth);
		Vector3D cameraDirection = new Vector3D(0, 0, 1);
		cameraDirection = cameraDirection.mult(avatarDirection);

		audioManager.getEar().setLocation(camera1.getLocation());
		audioManager.getEar().setOrientation(cameraDirection, new Vector3D(0, 1, 0));

	}

	private void createPhysicsWorld()
	{
		Transform myTransform;
		// define the broad-phase collision to be used (Sweep-and-Prune)
		broadPhaseHandler = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		// set up the narrow-phase collision handler ("dispatcher")
		collConfig = new DefaultCollisionConfiguration();
		collDispatcher = new CollisionDispatcher(collConfig);
		// create a constraint solver
		solver = new SequentialImpulseConstraintSolver();
		// create a physics world utilizing the above objects
		physicsWorld = new DiscreteDynamicsWorld(collDispatcher, broadPhaseHandler, solver, collConfig);
		physicsWorld.setGravity(new Vector3f(0, -10, 0));
		// define physicsGround plane: normal vector = 'up', dist from origin =
		// 1
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), groundHeight + 1);
		// set position and orientation of physicsGround's transform
		myTransform = new Transform();
		myTransform.origin.set(new Vector3f(0, -1, 0));
		myTransform.setRotation(new Quat4f(0, 0, 0, 1));
		// define construction info for a 'physicsGround' rigid body
		DefaultMotionState groundMotionState = new DefaultMotionState(myTransform);
		RigidBodyConstructionInfo groundCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		groundCI.restitution = 0.8f;
		// create the physicsGround rigid body and add it to the physics world
		physicsGround = new RigidBody(groundCI);
		physicsWorld.addRigidBody(physicsGround);
		// define a collision shape for a physicsBall
		CollisionShape fallShape = new SphereShape(1);
		// define a transform for position and orientation of ball collision
		// shape
		myTransform = new Transform();
		myTransform.origin.set(new Vector3f(0, 20, 5));
		myTransform.setRotation(new Quat4f(0, 0, 0, 1));
		// define the parameters of the collision shape
		DefaultMotionState fallMotionState = new DefaultMotionState(myTransform);
		float myFallMass = 1;
		Vector3f myFallInertia = new Vector3f(0, 0, 0);
		fallShape.calculateLocalInertia(myFallMass, myFallInertia);
		// define construction info for a 'physicsBall' rigid body
		RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(myFallMass, fallMotionState, fallShape, myFallInertia);
		fallRigidBodyCI.restitution = 0.8f;
		// create the physicsBall rigid body and add it to the physics world
		physicsBall = new RigidBody(fallRigidBodyCI);
		physicsWorld.addRigidBody(physicsBall);
	}

	private void initTerrain()
	{
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap(imagesDirectory + "/circle2.jpg");

		hillTerrain = createTerrainBlock(heightMap);
		// create texture and texture state to color the terrain
		TextureState groundState;
		Texture groundTexture = TextureManager.loadTexture2D(imagesDirectory + "/Craterscape.jpg");
		groundTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		groundState = (TextureState) display.getRenderer().createRenderState(RenderStateType.Texture);
		groundState.setTexture(groundTexture, 0);
		groundState.setEnabled(true);
		// apply the texture to the terrain
		hillTerrain.setRenderState(groundState);
		addGameWorldObject(hillTerrain);

	}

	private TerrainBlock createTerrainBlock(ImageBasedHeightMap heightMap)
	{
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

	private void createEssentialObjects()
	{
		// Get renderer
		renderer = display.getRenderer();

		// For managing collisions
		eventManager = EventManager.getInstance();
		SinkListener sinkListener = new SinkListener(this);
		eventManager.addListener(sinkListener, SinkEvent.class);

		// For loading Blender objects
		objectLoader = new OBJLoader();
	}

	private void createPlayers()
	{
		player1 = objectLoader.loadModel(modelsDirectory + "world2.obj");

		Texture ballTexture = TextureManager.loadTexture2D(modelsDirectory + planetChoice);
		TextureState ballTextureState = (TextureState) renderer.createRenderState(RenderStateType.Texture);
		ballTextureState.setTexture(ballTexture);
		ballTextureState.setEnabled(true);
		player1.setRenderState(ballTextureState);

		player1.scale(1f, 1f, 1f);
		player1.translate(0, 20f, 5);
		player1.rotate(180, new Vector3D(0, 1, 0));
		player1.updateGeometricState(1f, true);
		// addGameWorldObject(player1);
		scene.addChild(player1);

		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(130, 2, .1, 1000);
		camera1.setViewport(0f, 1f, 0f, 1f);

		IsPlayerAlive = true;
	}

	private void initGameElements()
	{
		// Get the display objects
		display = getDisplaySystem();
		display.setTitle("SUMO! Phil Hawkins and Kevin Jones");

		// Get the input manager for inputs.
		inputManager = getInputManager();

		// Get keyboard, gamepad and mouse
		keyboardName = inputManager.getKeyboardName();
		gamepadName = inputManager.getFirstGamepadName();

		if (gamepadName != null)
		{
			foundController = true;
		}

		camera1Controller = new OrbitCameraController(camera1, player1, inputManager, keyboardName, true);

		RotateObjectLeft rotatePlayer1Left = new RotateObjectLeft(player1);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.A, rotatePlayer1Left,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		RotateObjectRight rotatePlayer1Right = new RotateObjectRight(player1);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.D, rotatePlayer1Right,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		MoveObjectForwardAction movePlayer1Forward = new MoveObjectForwardAction(player1, hillTerrain, thisClient, this);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.W, movePlayer1Forward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		MoveObjectBackwardAction movePlayer1Backward = new MoveObjectBackwardAction(player1, hillTerrain, thisClient, this);
		inputManager.associateAction(keyboardName, net.java.games.input.Component.Identifier.Key.S, movePlayer1Backward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		quitGameAction = new QuitGameAction(this);
		inputManager.associateAction(keyboardName, Identifier.Key.ESCAPE, quitGameAction, INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	private void createGameWorldObjects()
	{
		createWorldAxes();

		ground = new Cylinder("ground");
		ground.setCullMode(CULL_MODE.NEVER);
		ground.setRadius(100);
		ground.setHeight(70f);
		ground.setSlices(20);
		ground.setSolid(true);
		ground.setColor(Color.red);
		ground.rotate(90, new Vector3D(1, 0, 0));
		ground.translate(50, 5f, 50);
		ground.setShowBound(true);
		groundHeight = 5f;
		addGameWorldObject(ground);
		
		OgreXMLParser loader = new OgreXMLParser();
		try
		{
			model = loader.loadModel(modelsDirectory + "Cube.mesh.xml", modelsDirectory + "Material.material", modelsDirectory + "Cube.skeleton.xml");
			model.updateGeometricState(0, true);
			java.util.Iterator<SceneNode> modelIteartor = model.iterator();
			alien = (Model3DTriMesh) modelIteartor.next();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		alien.translate(35, 30f, 30);
		addGameWorldObject(alien);
	}

	private void createWorldAxes()
	{
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

		scene = (Group) jsEngine.get("scene");
		skyBox = (SkyBox) jsEngine.get("skyBox");

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
		// execute the script statements in the file
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
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
	}

	@Override
	protected void update(float elapsedTimeMS)
	{
		timeElaspsed += elapsedTimeMS;
		if (!IsDeathTime && (TimeUntilDeath - (timeElaspsed / 1000)) < 0)
		{
			IsDeathTime = true;
			timeRemainingString.setText("RUN!");
			alien.startAnimation("my_animation");
		}
		else
		{
			timeRemainingString.setText("Time Until Death: " + (TimeUntilDeath - (timeElaspsed / 1000)));
		}

		if (IsDeathTime)
		{
			ground.translate(0, .001f, 0);
			ground.updateLocalBound();
			ground.updateWorldBound();
		}

		gameTime += elapsedTimeMS;
		if (gameTime > 3000)
		{
			running = true;
		}
		if (gameTime > 8000)
		{
			running = false;
		}
		if (running)
		{
			{
				physicsWorld.stepSimulation(1.0f / 60.0f, 8); // 1/60th sec, 8
																// steps
				// read and display the updated physicsBall position
				Transform pBallTransform = new Transform();
				physicsBall.getMotionState().getWorldTransform(pBallTransform);
				// update the graphics ball location from the physics ball
				float[] vals = new float[16];
				pBallTransform.getOpenGLMatrix(vals);
				Matrix3D gBallXform = new Matrix3D(vals);
				player1.setLocalTranslation(gBallXform);
				dropTime += elapsedTimeMS;
				if (dropTime > 50)
				{
					 thisClient.sendMoveMessage(getPlayerPosition());
					dropTime = 0;
				}
			}
		}

		if (IsPlayerAlive)
		{
			// Check for collisions only while the lava is rising
			if (IsDeathTime)
			{
				if (player1.getLocalTranslation().getCol(3).getY() < ground.getLocalTranslation().getCol(3).getY())
				{
					SinkEvent playerSunk = new SinkEvent(++playersSunk);
					eventManager.triggerEvent(playerSunk);
				}
			}

			// Check if player is alive again, he may have tragically died in
			// the event.
			if (IsPlayerAlive)
			{
				Point3D cameraLocation = camera1.getLocation();
				Matrix3D cameraTranslation = new Matrix3D();
				cameraTranslation.translate(cameraLocation.getX(), cameraLocation.getY(), cameraLocation.getZ());
				skyBox.setLocalTranslation(cameraTranslation);

				camera1Controller.update(elapsedTimeMS);
				backgroundSound.setLocation(new Point3D(player1.getWorldTranslation().getCol(3)));
				countdownSound.setLocation(new Point3D(player1.getWorldTranslation().getCol(3)));
				setEarParameters();
			}
		}

		super.update(elapsedTimeMS);
		alien.updateAnimation(elapsedTimeMS);

		if (thisClient != null)
		{
			thisClient.processPackets();
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
		if (thisClient != null)
		{
			thisClient.sendByeMessage();
			try
			{
				thisClient.shutdown();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setIsConnected(boolean b)
	{
		// TODO Auto-generated method stub
		isConnected = b;
	}

	public Vector3D getPlayerPosition()
	{
		// TODO Auto-generated method stub

		Vector3D pos = player1.getWorldTransform().getCol(3);

		return new Vector3D(pos.getX(), pos.getY(), pos.getZ());
	}

	public void removeGhostAvatar(UUID ghostID)
	{
		if (ghostAvatars.containsKey(ghostID))
		{
			removeGameWorldObject(ghostAvatars.get(ghostID).getSceneNode());
			ghostAvatars.remove(ghostID);
		}
	}

	public void createGhostAvatar(UUID ghostID, String[] ghostPosition)
	{
		GhostAvatar newAvatar = new GhostAvatar(ghostID, ghostPosition);
		ghostAvatars.put(ghostID, newAvatar);
		addGameWorldObject(newAvatar.getSceneNode());
	}

	public void updateGhostAvatar(UUID ghostID, String[] ghostPosition)
	{
		GhostAvatar ghost = ghostAvatars.get(ghostID);
		if (ghost != null)
		{
			ghost.setPosition(ghostPosition);
		}
	}

	public boolean getGhost(UUID id)
	{
		if (ghostAvatars.containsKey(id))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public float getGroundHeight()
	{
		return groundHeight;
	}

	public void addGhostNPCtoWorld(GhostNPC npc)
	{
		addGameWorldObject(npc.getBody());
	}

	public float getTerrainHeightAtLoc(float x, float y)
	{
		return hillTerrain.getHeight(x, y);
	}

	public void RemovePlayer()
	{
		removeGameWorldObject(player1);
		IsPlayerAlive = false;
		scene.removeChild(player1);
		player1.updateLocalBound();
		player1.updateWorldBound();
	}

	public HUDString GetPlayersSunkHUD()
	{
		return playersSunkString;
	}
}
