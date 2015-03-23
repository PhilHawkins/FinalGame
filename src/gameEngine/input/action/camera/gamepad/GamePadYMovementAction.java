package gameEngine.input.action.camera.gamepad;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class GamePadYMovementAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	private Controller controller;

	public GamePadYMovementAction(ICamera c, SetSpeedAction s, Controller cont) {
		camera = c;
		speed = s;
		controller = cont;
	}

	@Override
	public void performAction(float time, Event e) {
		float moveAmount = 0;
		Identifier yAxis = net.java.games.input.Component.Identifier.Axis.Y;

		float yAxisValue = controller.getComponent(yAxis).getPollData();
		if (Math.abs(yAxisValue) > 0.3){//controller.getComponent(yAxis).getDeadZone()) {
			if (speed.isRunning()) {
				moveAmount = (float) (-yAxisValue * 0.05 * time);
			} else {
				moveAmount = (float) (-yAxisValue * 0.01 * time);
			}
		}

		Vector3D viewDirection = camera.getViewDirection().normalize();
		Vector3D currentLocationVector = new Vector3D(camera.getLocation());
		Vector3D newLocationVector = currentLocationVector.add(viewDirection
				.mult(moveAmount));

		double newX = newLocationVector.getX();
		double newY = newLocationVector.getY();
		double newZ = newLocationVector.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);

	}

}
