package gameEngine.phil.input.action.camera.keyboard;

import gameEngine.phil.camera.OrbitCameraController;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.IAction;

public class OrbitRightAction implements IAction {

	private OrbitCameraController cameraController;
	
	public OrbitRightAction(OrbitCameraController c){
		cameraController = c;
	}
	
	@Override
	public void performAction(float time, Event e) {
		// TODO Auto-generated method stub
		float rotationAmt = (float) (0.2 * time);
		
		double cameraAzimuth = cameraController.getAzimuth();
		cameraAzimuth += rotationAmt; 
		cameraAzimuth = cameraAzimuth % 360;
		cameraController.setAzimuth(cameraAzimuth);
		cameraController.update(time);
	}

}
