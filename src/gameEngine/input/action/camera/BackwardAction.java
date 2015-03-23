package gameEngine.input.action.camera;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class BackwardAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction runAction;
	
	public BackwardAction(ICamera c, SetSpeedAction s){
		camera = c;
		runAction = s;
	}

	public void MoveForwardAction(ICamera c, SetSpeedAction r) {
		camera = c;
		runAction = r;
	}

	public void performAction(float time, Event e) {
		float moveAmount;
		if (runAction.isRunning()) {
			moveAmount = (float) -0.05 * time;
		} else {
			moveAmount = (float) -0.01 * time;
		}
		Vector3D viewDir = camera.getViewDirection().normalize();
		Vector3D currentLocationVector = new Vector3D(camera.getLocation());
		Vector3D newLocationVector = currentLocationVector.add(viewDir.mult(moveAmount));
		double newX = newLocationVector.getX();
		double newY = newLocationVector.getY();
		double newZ = newLocationVector.getZ();
		Point3D newLocation = new Point3D(newX, newY, newZ);
		camera.setLocation(newLocation);
	}

}
