package se.bluebrim.crud.client.command;

/**
 * Implemented by objects that has the capability to set and 
 * reset wait cursor (hour glass)
 * 
 * @author GStack
 *
 */
public interface CursorHandler
{
	public void setWaitCursor();
	public void resetWaitCursor();
}
