package gameEngine.input.action.camera.gamepad;

import gameEngine.camera.OrbitCameraController;
import net.java.games.input.Event;
import sage.input.action.IAction;

public class ZoomOrbitCamera implements IAction {
	OrbitCameraController cameraController;
	
	public ZoomOrbitCamera(OrbitCameraController c){
		cameraController = c;
	}

	@Override
	public void performAction(float time, Event e) {
		float zoomAmt;
		if( Math.abs(e.getValue()) > 0.1 ) {
			zoomAmt = -e.getValue() * .01f * time;
		}
		else {
			zoomAmt = 0;
		}

		cameraController.zoom(zoomAmt);
		cameraController.update(time);
	}

}
