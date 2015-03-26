package gameEngine.actions;

import enums.MoveDirections;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class PitchCameraAction extends AbstractInputAction
{
	private ICamera camera;
	MoveDirections defaultDirection;
	private float MOVE_AMOUNT = .05f;
	private float THRESHOLD = .3f;
	
	public PitchCameraAction(ICamera camera, MoveDirections direction)
	{
		this.camera = camera;
		this.defaultDirection = direction;
	}
	
	public void performAction(float time, Event event)
	{
		Matrix3D rotationAmount = new Matrix3D();
		Vector3D viewDirection = camera.getViewDirection();
		Vector3D upAxisDirection = camera.getUpAxis();
		Vector3D rightAxisDirection = camera.getRightAxis();
		
		MoveDirections directionToMove = defaultDirection;
		
		if (defaultDirection == MoveDirections.RIGHTSTICKY )
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
		
		if (directionToMove == MoveDirections.BACKWARD)
		{
			rotationAmount.rotate(-MOVE_AMOUNT * time, rightAxisDirection);
		}
		else if (directionToMove == MoveDirections.FORWARD)
		{
			rotationAmount.rotate(MOVE_AMOUNT * time, rightAxisDirection);
		}
		
		viewDirection = viewDirection.mult(rotationAmount);
		upAxisDirection = upAxisDirection.mult(rotationAmount);
		camera.setUpAxis(upAxisDirection.normalize());
		camera.setViewDirection(viewDirection.normalize());
		
		directionToMove = null;
	}
}
