package gameEngine.phil.input.action.object;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.IAction;
import sage.scene.SceneNode;

public class MoveObjectForwardAction implements IAction {
	private SceneNode object;
	
	public MoveObjectForwardAction(SceneNode o){
		object = o;
	}
	@Override
	public void performAction(float time, Event e) {
		Matrix3D rotation = object.getLocalRotation();
		Vector3D nextLocation = new Vector3D(0, 0, 1).mult(rotation);
		nextLocation.scale(time * .025);
		object.translate((float) nextLocation.getX(), (float) nextLocation.getY(), (float) nextLocation.getZ());
	}

}