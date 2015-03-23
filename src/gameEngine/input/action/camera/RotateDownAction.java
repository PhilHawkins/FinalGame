package gameEngine.input.action.camera;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class RotateDownAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	
	public RotateDownAction(ICamera c, SetSpeedAction s){
		camera = c;
		speed = s;
	}

	@Override
	public void performAction(float time, Event e) {
		Vector3D currentViewDirection = camera.getViewDirection();
		Vector3D currentUp = camera.getUpAxis();
		Vector3D currentRight = camera.getRightAxis();
		
		Matrix3D downTrans;
		
		if(speed.isRunning()){
			downTrans = new Matrix3D(-.9, camera.getRightAxis().normalize());
		}
		else
		{
			downTrans = new Matrix3D(-.3, camera.getRightAxis().normalize());
		}

		Vector3D newRight = currentRight.mult(downTrans);
		Vector3D newViewDir = currentViewDirection.mult(downTrans);
		Vector3D newUp = currentUp.mult(downTrans);
		
		
		camera.setAxes(newRight, newUp, newViewDir);
	}

}
