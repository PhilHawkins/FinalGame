package gameEngine.actions;

import gameEngine.camera.OrbitCameraController;
import enums.ZoomTypes;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

public class ZoomCameraAction extends AbstractInputAction
{
	OrbitCameraController orbitCamera;
	ZoomTypes zoomType;
	private float ZOOM_AMOUNT = .7f;
	
	public ZoomCameraAction(OrbitCameraController orbitCamera, ZoomTypes zoomType)
	{
		this.orbitCamera = orbitCamera;
		this.zoomType = zoomType;
	}

	@Override
	public void performAction(float time, Event event)
	{
		float distance = orbitCamera.getDistance();
		
		switch (zoomType)
		{
		case ZOOM_IN:
			distance += ZOOM_AMOUNT;
			break;
		case ZOOM_OUT:
			distance -= ZOOM_AMOUNT;
		default:
			break;
		}
		
		orbitCamera.setDistance(distance);		
	}	
}
