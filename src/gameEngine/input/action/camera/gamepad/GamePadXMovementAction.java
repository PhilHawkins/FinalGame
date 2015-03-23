package gameEngine.input.action.camera.gamepad;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class GamePadXMovementAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	private Controller controller;

	public GamePadXMovementAction(ICamera c, SetSpeedAction s, Controller cont) {
		camera = c;
		speed = s;
		controller = cont;
	}

	@Override
	public void performAction(float time, Event e) {
		float moveXAmount = 0;
		Identifier xAxis = net.java.games.input.Component.Identifier.Axis.X;
		
		//System.out.println(controller.getComponent(xAxis).getDeadZone());

		float xAxisValue = controller.getComponent(xAxis).getPollData();
		if (Math.abs(xAxisValue) > .3){//controller.getComponent(xAxis).getDeadZone()) {
			if (speed.isRunning()) {
				moveXAmount = (float) (xAxisValue * 0.05 * time);
			} else {
				moveXAmount = (float) (xAxisValue * 0.01 * time);
			}
		}

		Vector3D rightDirection = camera.getRightAxis().normalize();
		Vector3D currentLocationVector = new Vector3D(camera.getLocation());
		Vector3D newLocationVector = currentLocationVector.add(rightDirection
				.mult(moveXAmount));

		double newX = newLocationVector.getX();
		double newY = newLocationVector.getY();
		double newZ = newLocationVector.getZ();
		Point3D newLoc = new Point3D(newX, newY, newZ);
		camera.setLocation(newLoc);

	}

}
