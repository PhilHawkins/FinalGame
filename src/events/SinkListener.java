package events;

import FinalGame.FinalGame;
import sage.event.IEventListener;
import sage.event.IGameEvent;

public class SinkListener implements IEventListener
{
	private FinalGame theGame;
	
	public SinkListener(FinalGame game)
	{
		this.theGame = game;
	}
	
	public boolean handleEvent(IGameEvent event)
	{
		SinkEvent sinkEvent = (SinkEvent)event;
		
		int collisions = sinkEvent.GetNumberOfCollisions();
		
		System.out.println("Player Sunk! Players Sunk: " + collisions);
		
		this.theGame.RemovePlayer();
		this.theGame.GetPlayersSunkHUD().setText("Players Sunk: " + collisions);
		
		return true;
	}

}
