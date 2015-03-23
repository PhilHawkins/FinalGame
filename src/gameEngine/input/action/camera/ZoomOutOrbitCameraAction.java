package gameEngine.input.action.camera;

import gameEngine.camera.OrbitCameraController;
import net.java.games.input.Event;
import sage.input.action.IAction;

public class ZoomOutOrbitCameraAction implements IAction {
OrbitCameraController cameraController;
	
	public ZoomOutOrbitCameraAction(OrbitCameraController c){
		cameraController = c;
	}

	@Override
	public void performAction(float time, Event e) {
		float zoomAmount = (float) (-.02 * time);
		cameraController.zoom(zoomAmount);
		cameraController.update(time);
	}

}
