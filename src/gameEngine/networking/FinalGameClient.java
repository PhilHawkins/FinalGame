package gameEngine.networking;

import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import FinalGame.FinalGame;
import sage.networking.client.GameConnectionClient;

public class FinalGameClient extends GameConnectionClient {
	private FinalGame game;
	private UUID id;

	public FinalGameClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, FinalGame game)
			throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
	}
	
	protected void processPacket (Object msg) // override
	{ // extract incoming message into substrings. Then process:
		 String message = (String)msg;
		 String[] msgTokens = message.split(",");
		 if(msgTokens[0].compareTo("join") == 0) // receive “join”
		 { // format: join, success or join, failure
			if(msgTokens[1].compareTo("success") == 0)
			{ 
				game.setIsConnected(true);
				sendCreateMessage(game.getPlayerPosition());
			}
			else if(msgTokens[1].compareTo("failure") == 0)
			{
				game.setIsConnected(false);
			}
		}
		if(msgTokens[0].compareTo("bye") == 0) // receive “bye”
		{ // format: bye, remoteId
			UUID ghostID = UUID.fromString(msgTokens[1]);
			game.removeGhostAvatar(ghostID);
		}
		if (msgTokens[0].compareTo("dsfr") == 0 ) // receive “details for”
		{ // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			UUID ghostID = UUID.fromString(msgTokens[1]);
			if(!game.getGhost(ghostID)){
				String[] ghostPosition = {msgTokens[2], msgTokens[3], msgTokens[4]};
				game.createGhostAvatar(ghostID, ghostPosition);
			}
		}
		if(msgTokens[0].compareTo("create") == 0) // receive “create…”
		{ // etc….. 
			UUID ghostID = UUID.fromString(msgTokens[1]);
			String[] ghostPosition = {msgTokens[2], msgTokens[3], msgTokens[4]};
			game.createGhostAvatar(ghostID, ghostPosition);
		}
		if(msgTokens[0].compareTo("wsds") == 0) // receive “wants…”
		{ // etc….. 
			UUID remId = UUID.fromString(msgTokens[1]);
			Vector3D playerPosition = game.getPlayerPosition();
			sendDetailsForMessage(remId, playerPosition);
		}
		if(msgTokens[0].compareTo("move") == 0) // receive “move”
		{ // etc….. }
			UUID ghostID = UUID.fromString(msgTokens[1]);
			String[] ghostMovement = {msgTokens[2], msgTokens[3], msgTokens[4]};
			game.updateGhostAvatar(ghostID, ghostMovement);
		}
	}
	
	public void sendCreateMessage(Vector3D pos)
	{ // format: (create, localId, x,y,z)
		try
		 { 
			String message = new String("create," + id.toString());
			message += "," + String.valueOf(pos.getX()) +"," + String.valueOf(pos.getY()) + "," + String.valueOf(pos.getZ());
			sendPacket(message);
		 }
		 catch (IOException e) { 
			 e.printStackTrace(); 
		 }
	}

	public void sendJoinMessage()
	{ // format: join, localId
	 try
	 { 
		 sendPacket(new String("join," + id.toString())); 
	 }
	 catch (IOException e) { 
		 e.printStackTrace(); 
	 }
	 
	}
	
	public void sendByeMessage()
	{ // etc….. }
		try {
			sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendDetailsForMessage(UUID remId, Vector3D pos)
	{ // etc….. }

		String message = new String("dsfr," + id.toString() + "," + remId.toString());
		message += "," + String.valueOf(pos.getX()) +"," + String.valueOf(pos.getY()) + "," + String.valueOf(pos.getZ());
		try {
			sendPacket(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMoveMessage(Vector3D pos)
	{ // etc….. }
		String message = new String("move," + id.toString());
		message += "," + pos.getX() +"," + pos.getY() + "," + pos.getZ();
		try {
			sendPacket(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
