package gameEngine.actions;

import enums.MoveDirections;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class MoveCameraAction extends AbstractInputAction
{
	private ICamera camera;
	MoveDirections defaultDirection;
	private float MOVE_AMOUNT = .001f;
	private float THRESHOLD = .3f;
	
	public MoveCameraAction(ICamera camera, MoveDirections direction)
	{
		this.camera = camera;
		this.defaultDirection = direction;
	}
	
	public void performAction(float time, Event event)
	{
		Vector3D viewDirection;
		Vector3D currentLocationVector = new Vector3D(camera.getLocation());
		Vector3D newLocationVector;
		
		double newX, newY, newZ;
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
		
		switch (directionToMove)
		{
		case FORWARD:
			viewDirection = camera.getViewDirection().normalize();
			newLocationVector = currentLocationVector.add(viewDirection.mult(MOVE_AMOUNT * time));
			newX = newLocationVector.getX();
			newY = newLocationVector.getY();
			newZ = newLocationVector.getZ();
			break;
		case BACKWARD:
			viewDirection = camera.getViewDirection().normalize();
			newLocationVector = currentLocationVector.add(viewDirection.mult(-MOVE_AMOUNT * time));
			newX = newLocationVector.getX();
			newY = newLocationVector.getY();
			newZ = newLocationVector.getZ();
			break;
		case LEFT:
			viewDirection = camera.getRightAxis().normalize();
			newLocationVector = currentLocationVector.add(viewDirection.mult(-MOVE_AMOUNT * time));
			newX = newLocationVector.getX();
			newY = newLocationVector.getY();
			newZ = newLocationVector.getZ();
			break;
		case RIGHT:
			viewDirection = camera.getRightAxis().normalize();
			newLocationVector = currentLocationVector.add(viewDirection.mult(MOVE_AMOUNT * time));
			newX = newLocationVector.getX();
			newY = newLocationVector.getY();
			newZ = newLocationVector.getZ();
			break;
		case NONE:
		default:
			newLocationVector = currentLocationVector;
			newX = newLocationVector.getX();
			newY = newLocationVector.getY();
			newZ = newLocationVector.getZ();
			break;
		}
		
		Point3D newLocation = new Point3D(newX, newY, newZ);
		
		camera.setLocation(newLocation);
		directionToMove = null;
	}
}
