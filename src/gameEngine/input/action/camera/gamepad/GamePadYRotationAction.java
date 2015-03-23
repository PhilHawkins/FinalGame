package gameEngine.input.action.camera.gamepad;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Component.Identifier;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class GamePadYRotationAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	private Controller controller;
	
	public GamePadYRotationAction(ICamera c, SetSpeedAction s, Controller cont){
		camera = c;
		speed = s;
		controller = cont;
	}
	
	
	public void performAction(float time, Event e) {
		Identifier ryAxis = net.java.games.input.Component.Identifier.Axis.RY;
		float ryAxisValue = controller.getComponent(ryAxis).getPollData();
		
		float rotationAmount = 0;
		if (Math.abs(ryAxisValue) > 0.3){//controller.getComponent(ryAxis).getDeadZone()) {
			if(speed.isRunning()){
				rotationAmount = (float) (-ryAxisValue * .5 * time);
			}else{
				rotationAmount = (float) (-ryAxisValue * .1 * time);
			}
		}
		else{
			rotationAmount = 0;
		}
		
		Vector3D currentViewDirection = camera.getViewDirection();
		Vector3D currentUp = camera.getUpAxis();
		Vector3D currentRight = camera.getRightAxis();
		
		Matrix3D yTrans = new Matrix3D(rotationAmount, camera.getRightAxis().normalize());	

		Vector3D newRight = currentRight.mult(yTrans);
		Vector3D newViewDir = currentViewDirection.mult(yTrans);
		Vector3D newUp = currentUp.mult(yTrans);
				
		camera.setAxes(newRight, newUp, newViewDir);
	}
	
}
