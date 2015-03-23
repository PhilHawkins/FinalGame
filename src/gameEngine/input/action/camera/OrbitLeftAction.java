package gameEngine.input.action.camera;

import gameEngine.camera.OrbitCameraController;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.IAction;

public class OrbitLeftAction implements IAction {

	private OrbitCameraController cameraController;
	
	public OrbitLeftAction(OrbitCameraController c){
		cameraController = c;
	}
	
	@Override
	public void performAction(float time, Event e) {
		float rotationAmt = (float) (-0.2 * time);
		
		double cameraAzimuth = cameraController.getAzimuth();
		cameraAzimuth += rotationAmt; 
		cameraAzimuth = cameraAzimuth % 360;
		cameraController.setAzimuth(cameraAzimuth);
		cameraController.update(time);
	}

}
