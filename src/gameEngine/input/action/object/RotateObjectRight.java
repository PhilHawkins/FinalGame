package gameEngine.input.action.object;

import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.IAction;
import sage.scene.SceneNode;

public class RotateObjectRight implements IAction {

	private SceneNode object;
	
	public RotateObjectRight(SceneNode o){
		object = o;
	}
	
	@Override
	public void performAction(float time, Event e) {
		object.rotate((float) (-.2 * time), new Vector3D(0, 1, 0));
	}
	
	
	
}
