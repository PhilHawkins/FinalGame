package gameEngine.input.action.camera;

import net.java.games.input.Event;
import sage.input.action.IAction;
import gameEngine.camera.OrbitCameraController;

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
