package gameEngine.networking;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import FinalGame.FinalGame;
import FinalGame.GhostNPC;
import sage.networking.client.GameConnectionClient;

public class FinalGameClient extends GameConnectionClient {
	private FinalGame game;
	private UUID id;
	Vector<GhostNPC> ghostNPCs;

	public FinalGameClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, FinalGame game)
			throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		ghostNPCs = new Vector<GhostNPC>();
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
				sendCreateMessage(game.getPlayerPosition(), game.getPlayerPlanet());
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
				String planet = msgTokens[5];
				game.createGhostAvatar(ghostID, ghostPosition, planet);
			}
		}
		if(msgTokens[0].compareTo("create") == 0) // receive “create…”
		{ // etc….. 
			UUID ghostID = UUID.fromString(msgTokens[1]);
			String[] ghostPosition = {msgTokens[2], msgTokens[3], msgTokens[4]};
			String planet = msgTokens[5];
			game.createGhostAvatar(ghostID, ghostPosition, planet);
		}
		if(msgTokens[0].compareTo("wsds") == 0) // receive “wants…”
		{ // etc….. 
			UUID remId = UUID.fromString(msgTokens[1]);
			Vector3D playerPosition = game.getPlayerPosition();
			String planet = game.getPlayerPlanet();
			sendDetailsForMessage(remId, playerPosition, planet);
		}
		if(msgTokens[0].compareTo("move") == 0) // receive “move”
		{ // etc….. }
			UUID ghostID = UUID.fromString(msgTokens[1]);
			String[] ghostMovement = {msgTokens[2], msgTokens[3], msgTokens[4]};
			game.updateGhostAvatar(ghostID, ghostMovement);
		}
		 if(msgTokens[0].compareTo("mnpc") == 0)
		 { 
//			 int ghostID = Integer.parseInt(msgTokens[1]);
			 Vector3D ghostPosition = new Vector3D();
			 ghostPosition.setX(Double.parseDouble(msgTokens[1]));
			 ghostPosition.setY(Double.parseDouble(msgTokens[2]));
			 ghostPosition.setZ(Double.parseDouble(msgTokens[3]));
			 updateGhostNPC(0, ghostPosition);
			 
			 Vector3D secondGhostPosition = new Vector3D();
			 secondGhostPosition.setX(Double.parseDouble(msgTokens[4]));
			 secondGhostPosition.setY(Double.parseDouble(msgTokens[5]));
			 secondGhostPosition.setZ(Double.parseDouble(msgTokens[6]));
			 updateGhostNPC(1, ghostPosition);
		 }
		 if(msgTokens[0].compareTo("npcds") == 0){
			 Vector3D firstLoc = new Vector3D();
			 Vector3D secondLoc = new Vector3D();
			 
			 firstLoc.setX(Float.valueOf(msgTokens[1]));
			 firstLoc.setY(Float.valueOf(msgTokens[2]));
			 firstLoc.setZ(Float.valueOf(msgTokens[3]));
			 
			 createNPC(0, firstLoc);
			 
			 secondLoc.setX(Float.valueOf(msgTokens[4]));
			 secondLoc.setX(Float.valueOf(msgTokens[5]));
			 secondLoc.setX(Float.valueOf(msgTokens[6]));

			 createNPC(1, secondLoc);
		 }

	}
	
	public void sendCreateMessage(Vector3D pos, String planet)
	{ // format: (create, localId, x,y,z)
		try
		 { 
			String message = new String("create," + id.toString());
			message += "," + String.valueOf(pos.getX()) +"," + String.valueOf(pos.getY()) + "," + String.valueOf(pos.getZ());
			message += "," + planet;
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
	
	public void sendDetailsForMessage(UUID remId, Vector3D pos, String planet)
	{ // etc….. }

		String message = new String("dsfr," + id.toString() + "," + remId.toString());
		message += "," + String.valueOf(pos.getX()) +"," + String.valueOf(pos.getY()) + "," + String.valueOf(pos.getZ());
		message += "," + planet;
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

	private void createNPC(int id, Vector3D position){
		GhostNPC newNPC = new GhostNPC(id, position);
		ghostNPCs.add(newNPC);
		addNPCtoGameWorld(newNPC);
	}
	
	private void updateGhostNPC(int id, Vector3D position){
		float x = (float) position.getX();
		float z = (float) position.getZ();
		float terHeight = game.getTerrainHeightAtLoc(x, z);
		if(! (terHeight > 0)){
			terHeight = game.getGroundHeight();
		}
		float desiredHeight = terHeight + 1f;
//		System.out.println(desiredHeight);

		position.setY(desiredHeight);
		
		
		if(ghostNPCs.size() > id){
			ghostNPCs.get(id).setPosition(position);
		}
	}

	private void addNPCtoGameWorld(GhostNPC npc){
		game.addGhostNPCtoWorld(npc);
	}


}
