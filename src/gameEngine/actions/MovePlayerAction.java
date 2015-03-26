package gameEngine.actions;

import enums.MoveDirections;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

public class MovePlayerAction extends AbstractInputAction
{
	private SceneNode player;
	private float MOVE_AMOUNT = .005f;
	private float THRESHOLD = .3f;
	private MoveDirections defaultDirection;
	
	public MovePlayerAction(SceneNode player, MoveDirections diretionToMove)
	{
		this.player = player;
		this.defaultDirection = diretionToMove;
	}
	
	@Override
	public void performAction(float time, Event event)
	{
		Matrix3D currentTranslation = player.getLocalTranslation();
		Vector3D currentDirection = new Vector3D(0, 0, 0);
		currentDirection = currentDirection.mult(currentTranslation);
		
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
			currentDirection = new Vector3D(0, 0, 1);
			currentDirection.scale(MOVE_AMOUNT * time);
			break;
		case BACKWARD:
			currentDirection = new Vector3D(0, 0, 1);
			currentDirection.scale(MOVE_AMOUNT * time * -1);
			break;
		case LEFT:
			currentDirection = new Vector3D(1, 0, 0);
			currentDirection.scale(MOVE_AMOUNT * time);
			break;
		case RIGHT:
			currentDirection = new Vector3D(1, 0, 0);
			currentDirection.scale(MOVE_AMOUNT * time * -1);
			break;
		case NONE:
		default:
			currentDirection = new Vector3D(0, 0, 0);
			break;
		}

		player.translate((float)currentDirection.getX(), (float)currentDirection.getY(), (float)currentDirection.getZ());
	}
}
