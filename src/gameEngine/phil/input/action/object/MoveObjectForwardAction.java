package gameEngine.phil.input.action.object;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.terrain.TerrainBlock;

public class MoveObjectForwardAction implements IAction {
	private SceneNode object;
	private TerrainBlock terrain;
	
	public MoveObjectForwardAction(SceneNode o, TerrainBlock ter){
		object = o;
		terrain = ter;
	}
	@Override
	public void performAction(float time, Event e) {
		Matrix3D rotation = object.getLocalRotation();
		Vector3D nextLocation = new Vector3D(0, 0, 1).mult(rotation);
		nextLocation.scale(time * .025);
		object.translate((float) nextLocation.getX(), (float) nextLocation.getY(), (float) nextLocation.getZ());
		
		Point3D avLoc = new Point3D(object.getLocalTranslation().getCol(3));
		float x = (float) avLoc.getX();
		float z = (float) avLoc.getZ();
//		System.out.println(terrain.getHeight(x, z));
		float terHeight = terrain.getHeight(x,z);
		float desiredHeight = terHeight + (float)terrain.getOrigin().getY() + 1f;
//		System.out.println(desiredHeight);
		object.getLocalTranslation().setElementAt(1, 3, desiredHeight);
	}

}
