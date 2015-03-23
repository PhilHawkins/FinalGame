package gameEngine.input.action.camera;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class RotateRightAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	
	public RotateRightAction(ICamera c, SetSpeedAction s){
		camera = c;
		speed = s;
	}

	@Override
	public void performAction(float time, Event e) {
		Vector3D currentViewDirection = camera.getViewDirection();
		Vector3D currentUp = camera.getUpAxis();
		Vector3D currentRight = camera.getRightAxis();
		
		Matrix3D rightTrans;
		
		if(speed.isRunning()){
			rightTrans = new Matrix3D(-.9, new Vector3D(0,1,0));	
		}
		else{
			rightTrans = new Matrix3D(-.3, new Vector3D(0,1,0));
		}

		Vector3D newRight = currentRight.mult(rightTrans);
		Vector3D newViewDir = currentViewDirection.mult(rightTrans);
		Vector3D newUp = currentUp.mult(rightTrans);
				
		camera.setAxes(newRight, newUp, newViewDir);
	}

}
