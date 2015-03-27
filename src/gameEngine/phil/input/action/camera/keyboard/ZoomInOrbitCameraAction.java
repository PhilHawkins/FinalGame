package gameEngine.phil.input.action.camera.keyboard;

import net.java.games.input.Event;
import sage.input.action.IAction;
import gameEngine.phil.camera.OrbitCameraController;

public class ZoomInOrbitCameraAction implements IAction {
	OrbitCameraController cameraController;
	
	public ZoomInOrbitCameraAction(OrbitCameraController c){
		cameraController = c;
	}

	@Override
	public void performAction(float time, Event e) {
		float zoomAmount = (float) (.02 * time);
		cameraController.zoom(zoomAmount);
		cameraController.update(time);
	}
	
	
	
	
	
}
