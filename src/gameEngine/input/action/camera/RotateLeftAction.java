package gameEngine.input.action.camera;

import gameEngine.input.action.object.SetSpeedAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class RotateLeftAction extends AbstractInputAction {
	private ICamera camera;
	private SetSpeedAction speed;
	
	public RotateLeftAction(ICamera c, SetSpeedAction s){
		camera = c;
		speed = s;
	}

	@Override
	public void performAction(float time, Event e) {
		Vector3D currentViewDirection = camera.getViewDirection();
		Vector3D currentUp = camera.getUpAxis();
		Vector3D currentRight = camera.getRightAxis();
		
		Matrix3D leftTrans;
		
		if(speed.isRunning()){
			leftTrans = new Matrix3D(.9, new Vector3D(0,1,0));
		}
		else{
			leftTrans = new Matrix3D(.3, new Vector3D(0,1,0));
		}
			

		Vector3D newRight = currentRight.mult(leftTrans);
		Vector3D newViewDir = currentViewDirection.mult(leftTrans);
		Vector3D newUp = currentUp.mult(leftTrans);
		
		
		camera.setAxes(newRight, newUp, newViewDir);
	}

}
