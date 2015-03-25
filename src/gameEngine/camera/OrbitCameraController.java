package gameEngine.camera;

import gameEngine.input.action.camera.OrbitLeftAction;
import gameEngine.input.action.camera.OrbitRightAction;
import gameEngine.input.action.camera.ZoomInOrbitCameraAction;
import gameEngine.input.action.camera.ZoomOutOrbitCameraAction;
import gameEngine.input.action.camera.gamepad.OrbitAroundAction;
import gameEngine.input.action.camera.gamepad.ZoomOrbitCamera;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class OrbitCameraController {
	private ICamera camera;
	private SceneNode target; 
	private float cameraAzimuth; 
	private float cameraElevation; 
	private float cameraDistance;
	private Point3D targetPosition; 
	private Vector3D worldUp;
	private float minDistance;
	private float maxDistance;
	
	public OrbitCameraController(ICamera c, IInputManager i, SceneNode sn, String cn, boolean isKB){
		camera = c;
		target = sn;
		worldUp = new Vector3D(0, 1, 0);
		cameraDistance = 5.0f;
		cameraAzimuth = 180;
		cameraElevation = 20.0f;
		minDistance = 1.5f;
		maxDistance = 10f;
		update(0.0f);
		setupInput(i, cn, isKB);
	}
	
	 public void update(float time)
	 {
		 updateTarget();
		 updateCameraPosition();
		 camera.lookAt(targetPosition, worldUp); 
	 }
	 
	 private void updateTarget()
	 { 
		 targetPosition = new Point3D(target.getWorldTranslation().getCol(3)); 
	 }
	 
	 private void updateCameraPosition()
	 {
		 double theta = cameraAzimuth;
		 double phi = cameraElevation ;
		 double r = cameraDistance;

		 Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		 Point3D desiredCameraLoc = relativePosition.add(targetPosition);
		 camera.setLocation(desiredCameraLoc);
	 }

	 private void setupInput(IInputManager im, String cn, boolean isKB)
	 { 
		 if(isKB){
			 IAction orbitLeft = new OrbitLeftAction(this);
			 IAction orbitRight = new OrbitRightAction(this);
			 IAction zoomIn = new ZoomInOrbitCameraAction(this);
			 IAction zoomOut = new ZoomOutOrbitCameraAction(this);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Key.LEFT, 
					 orbitLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Key.RIGHT, 
					 orbitRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Key.UP, 
					 zoomIn, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Key.DOWN, 
					 zoomOut, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			 
		 }
		 else{
			 IAction orbit = new OrbitAroundAction(this);
			 IAction zoom = new ZoomOrbitCamera(this);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RX, 
					 orbit, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			 im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.RY, 
					 zoom, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		 }
	 }

	 public double getAzimuth(){
		 return cameraAzimuth;
	 }
	 
	 public void setAzimuth(double a){
		 cameraAzimuth = (float) a;
	 }
	 
	 public void zoom(float amount){
		 float newDistance = cameraDistance - amount;
		 if(newDistance > minDistance && newDistance < maxDistance){
			 cameraDistance = newDistance;
		 }
	 }
	 
}

