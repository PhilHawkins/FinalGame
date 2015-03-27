package gameEngine.phil.input.action.camera.gamepad;

import gameEngine.phil.input.action.object.SetSpeedAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Component.Identifier;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class GamePadXRotationAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	private Controller controller;
	
	public GamePadXRotationAction(ICamera c, SetSpeedAction s, Controller cont){
		camera = c;
		speed = s;
		controller = cont;
	}
	
	
	public void performAction(float time, Event e) {
		Identifier rxAxis = net.java.games.input.Component.Identifier.Axis.RX;
		float rxAxisValue = controller.getComponent(rxAxis).getPollData();
		
		float rotationAmount = 0;
		if (Math.abs(rxAxisValue) > 0.3){//controller.getComponent(rxAxis).getDeadZone()) {
			if(speed.isRunning()){
				rotationAmount = (float) (-rxAxisValue * .5 * time);
			}
			else{
				rotationAmount = (float) (-rxAxisValue * .1 * time);
			}
		}
		else{
			rotationAmount = 0;
		}
		
		Vector3D currentViewDirection = camera.getViewDirection();
		Vector3D currentUp = camera.getUpAxis();
		Vector3D currentRight = camera.getRightAxis();
		
		Matrix3D xTrans = new Matrix3D(rotationAmount, new Vector3D(0,1,0));	

		Vector3D newRight = currentRight.mult(xTrans);
		Vector3D newViewDir = currentViewDirection.mult(xTrans);
		Vector3D newUp = currentUp.mult(xTrans);
				
		camera.setAxes(newRight, newUp, newViewDir);
	}
	
}
