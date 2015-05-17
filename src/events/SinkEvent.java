package events;

import sage.event.AbstractGameEvent;

public class SinkEvent extends AbstractGameEvent
{
	private int collisionNumber;
	
	public SinkEvent(int collisionNumber)
	{
		this.collisionNumber = collisionNumber;
	}
	
	public int GetNumberOfCollisions()
	{
		return this.collisionNumber;
	}

}
