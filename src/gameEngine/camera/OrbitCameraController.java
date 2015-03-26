package gameEngine.camera;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.ThirdPersonCameraController;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class OrbitCameraController extends ThirdPersonCameraController
{
	private ICamera camera;
	private SceneNode player;
	// Rotation around player
	private float azimuth;
	// Distance above player
	private float elevation;
	// Distance away from player
	private float distance;
	private Point3D playerPosition;
	private Vector3D worldUpVector;
	
	public OrbitCameraController(ICamera camera, IInputManager inputManager, SceneNode player, String controllerName)
	{
		super(camera, player, inputManager, controllerName);
		this.camera = camera;
		this.player = player;
		worldUpVector = new Vector3D(0, 1, 0);
		this.distance = 5f;
		this.azimuth = 180;
		this.elevation = 20f;
		update(0f);
	}
	
	public void update(float eleapsedTime)
	{
		updatePlayer();
		updateCameraPosition();
		camera.lookAt(playerPosition, worldUpVector);
	}
	
	private void updatePlayer()
	{
		playerPosition = new Point3D(player.getWorldTranslation().getCol(3));
	}
	
	private void updateCameraPosition()
	{
		Point3D cartesianPosition = MathUtils.sphericalToCartesian(azimuth, elevation, distance);
		Point3D newCameraLocation = cartesianPosition.add(playerPosition);
		camera.setLocation(newCameraLocation);
	}
	
	public float getAzimuth()
	{
		return this.azimuth;
	}
	
	public void setAzimuth(float azimuth)
	{
		this.azimuth = azimuth;
	}
	
	public float getElevation()
	{
		return this.elevation;
	}
	
	public void setElevation(float elevation)
	{
		this.elevation = elevation;
	}
	
	public float getDistance()
	{
		return this.distance;
	}
	
	public void setDistance(float distance)
	{
		this.distance = distance;
	}
}

