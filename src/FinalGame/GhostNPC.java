package FinalGame;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.util.UUID;

import sage.scene.SceneNode;
import sage.scene.shape.Sphere;

public class GhostNPC {
	int id;
	Sphere body;
	
	public GhostNPC(int id, Vector3D position){
		this.id = id;
		body = new Sphere();
		body.setColor(Color.blue);
		body.setName(String.valueOf(id));
		setPosition(position);
	}
	
	public void setPosition(Vector3D position){
		Matrix3D trans = new Matrix3D();
		trans.translate(position.getX(), position.getY(), position.getZ());
		body.setLocalTranslation(trans);
	}

	public SceneNode getBody() {
		return body;
	}
}
