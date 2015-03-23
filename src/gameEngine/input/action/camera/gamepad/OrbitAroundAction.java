package gameEngine.input.action.camera.gamepad;

import gameEngine.camera.OrbitCameraController;
import net.java.games.input.Event;
import sage.input.action.IAction;

public class OrbitAroundAction implements IAction {

	private OrbitCameraController cameraController;
	
	public OrbitAroundAction(OrbitCameraController c){
		cameraController = c;
	}
	
	@Override
	public void performAction(float time, Event e) {
		// TODO Auto-generated method stub
		float rotationAmt;
		if( Math.abs(e.getValue()) > 0.2 ) {
			rotationAmt = e.getValue() * .05f * time;
		}
		else {
			rotationAmt = 0;
		}
		
		double cameraAzimuth = cameraController.getAzimuth();
		cameraAzimuth += rotationAmt; 
		cameraAzimuth = cameraAzimuth % 360;
		cameraController.setAzimuth(cameraAzimuth);
		cameraController.update(time);
	}

}
