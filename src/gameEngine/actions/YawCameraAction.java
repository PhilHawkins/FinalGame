package gameEngine.actions;

import enums.MoveDirections;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class YawCameraAction extends AbstractInputAction
{
	private ICamera camera;
	MoveDirections defaultDirection;
	private float MOVE_AMOUNT = .05f;
	private float THRESHOLD = .3f;
	
	public YawCameraAction(ICamera camera, MoveDirections direction)
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
		
		if (defaultDirection == MoveDirections.RIGHTSTICKX )
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
				
		if (directionToMove == MoveDirections.RIGHT)
		{
			rotationAmount.rotate(-MOVE_AMOUNT * time, upAxisDirection);
		}
		else if (directionToMove == MoveDirections.LEFT)
		{
			rotationAmount.rotate(MOVE_AMOUNT * time, upAxisDirection);
		}
		
		viewDirection = viewDirection.mult(rotationAmount);
		rightAxisDirection = rightAxisDirection.mult(rotationAmount);
		
		camera.setRightAxis(rightAxisDirection.normalize());
		camera.setViewDirection(viewDirection.normalize());
		
		directionToMove = null;
	}
}
