package gameEngine.actions;

import gameEngine.camera.OrbitCameraController;
import enums.MoveDirections;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

public class OrbitCameraAction extends AbstractInputAction
{
	private OrbitCameraController orbitCamera;
	private MoveDirections defaultDirection;
	private float THRESHOLD = .3f;
	private float ROTATION_SPEED = .7f;
	
	public OrbitCameraAction(OrbitCameraController orbitCamera, MoveDirections directionToMove)
	{
		this.orbitCamera = orbitCamera;
		this.defaultDirection = directionToMove;
	}
	
	public void performAction(float time, Event event)
	{
		float rotationAmount = 0;
		
		MoveDirections directionToMove = defaultDirection;
		
		if (defaultDirection == MoveDirections.LEFTSTICKX || defaultDirection == MoveDirections.RIGHTSTICKX )
		{
			if (event.getValue() > THRESHOLD)
			{
				directionToMove = MoveDirections.RIGHT;
			}
			else if (event.getValue() < -THRESHOLD)
			{
				directionToMove = MoveDirections.LEFT;
			}
			else 
			{
				directionToMove = MoveDirections.NONE;
			}
		}
		
		if (defaultDirection == MoveDirections.LEFTSTICKY || defaultDirection == MoveDirections.RIGHTSTICKY )
		{
			if (event.getValue() > THRESHOLD)
			{
				directionToMove = MoveDirections.BACKWARD;
			}
			else if (event.getValue() < -THRESHOLD)
			{
				directionToMove = MoveDirections.FORWARD;
			}
			else 
			{
				directionToMove = MoveDirections.NONE;
			}
		}		
		
		float azimuth = 0;
		float elevation = 0;
		
		switch (directionToMove)
		{
			case FORWARD:
				rotationAmount = -ROTATION_SPEED;
				elevation = orbitCamera.getElevation();
				elevation += rotationAmount;
				elevation = elevation % 360;
				orbitCamera.setElevation(elevation);
				break;
			case BACKWARD:
				rotationAmount = ROTATION_SPEED;
				elevation = orbitCamera.getElevation();
				elevation += rotationAmount;
				elevation = elevation % 360;
				orbitCamera.setElevation(elevation);
			case LEFT:
				rotationAmount = -ROTATION_SPEED;
				azimuth = orbitCamera.getAzimuth();
				azimuth += rotationAmount;
				azimuth = azimuth % 360;
				orbitCamera.setAzimuth(azimuth);
				break;			
			case RIGHT:
				rotationAmount = ROTATION_SPEED;
				azimuth = orbitCamera.getAzimuth();
				azimuth += rotationAmount;
				azimuth = azimuth % 360;
				orbitCamera.setAzimuth(azimuth);
				break;
			default:
				break;
		}
	}	
}