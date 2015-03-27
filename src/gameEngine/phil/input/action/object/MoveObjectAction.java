package gameEngine.phil.input.action.object;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.IAction;
import sage.scene.SceneNode;

public class MoveObjectAction implements IAction {
	SceneNode target;
	
	public MoveObjectAction(SceneNode s){
		target = s;
	}

	@Override
	public void performAction(float time, Event e) {
		 float moveAmount = 0;
		 if(Math.abs(e.getValue()) > 0.2){
			 moveAmount = e.getValue() * .025f * time;
		 }

			Matrix3D rotation = target.getLocalRotation();
			Vector3D nextLocation = new Vector3D(0, 0, moveAmount).mult(rotation);
			target.translate((float) nextLocation.getX(), (float) nextLocation.getY(), (float) nextLocation.getZ());
	}

}
