package gameEngine.networking;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.io.File;
import java.util.UUID;

import FinalGame.FinalGame;
import sage.model.loader.OBJLoader;
import sage.scene.SceneNode;
import sage.scene.shape.Pyramid;
import sage.scene.state.TextureState;
import sage.scene.state.RenderState.RenderStateType;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class GhostAvatar {
	private FinalGame game;
	private SceneNode thisAvatar;
	private UUID id;
	private String planet;
	
	public GhostAvatar(UUID id, String[] pos, String planet, FinalGame g){
		this.id = id;
		this.planet = planet;
		this.game = g;
		
//		thisAvatar = new Pyramid();
//		thisAvatar.scale(.5f, .5f, .5f);
//		thisAvatar.translate(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));	

		thisAvatar = game.createObj(planet);
		
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
		thisAvatar.setLocalTranslation(worldCoords);
		thisAvatar.updateWorldBound();
	}
	
	public SceneNode getSceneNode(){
		return thisAvatar;
	}
}
