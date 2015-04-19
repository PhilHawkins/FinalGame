package gameEngine.networking;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.util.UUID;

import sage.scene.SceneNode;
import sage.scene.shape.Pyramid;

public class GhostAvatar {
	private Pyramid thisAvatar;
	private UUID id;
	
	public GhostAvatar(UUID id, String[] pos){
		this.id = id;
		thisAvatar = new Pyramid();
		thisAvatar.scale(.5f, .5f, .5f);
		thisAvatar.translate(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));		
	}
	
	public UUID getID(){
		return id;
	}
	
	public void move(String[] ghostMovement){
		thisAvatar.translate(Float.parseFloat(ghostMovement[0]), Float.parseFloat(ghostMovement[1]), Float.parseFloat(ghostMovement[2]));
	}

	public void setPosition(String[] ghostPosition) {
		// TODO Auto-generated method stub
		Matrix3D worldCoords = new Matrix3D();
		worldCoords.translate(Float.parseFloat(ghostPosition[0]), Float.parseFloat(ghostPosition[1]), Float.parseFloat(ghostPosition[2]));
		thisAvatar.setWorldTranslation(worldCoords);
	}
	
	public SceneNode getSceneNode(){
		return thisAvatar;
	}
}
