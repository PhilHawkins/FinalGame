package gameEngine.input.action.object;

import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.IAction;
import sage.scene.SceneNode;

public class RotateObjectAction implements IAction {
	SceneNode target;
	
	public RotateObjectAction(SceneNode s){
		target = s;
	}

	@Override
	public void performAction(float time, Event e) {
		float rotationAmount = 0;
		if(Math.abs(e.getValue()) > .02){
			rotationAmount = e.getValue() * .05f * time;
		}
		
		target.rotate(-rotationAmount, new Vector3D(0, 1, 0));
	}

}
