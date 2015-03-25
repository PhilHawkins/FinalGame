package gameEngine.input.action.camera;

import gameEngine.input.action.object.SetSpeedAction;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.action.AbstractInputAction;

public class POVMovement extends AbstractInputAction {

	private ICamera camera;
	private SetSpeedAction speed;
	
	public POVMovement(ICamera c, SetSpeedAction s){
		camera = c;
		speed = s;
	}
	
	
	@Override
	public void performAction(float arg0, Event arg1) {
		// TODO Auto-generated method stub
		//if( net.java.games.input.)
		
	}

}
