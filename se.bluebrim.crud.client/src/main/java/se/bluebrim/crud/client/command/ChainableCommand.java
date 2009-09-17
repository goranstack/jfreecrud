package se.bluebrim.crud.client.command;

/**
 * Commands in a CommandChain must implement this interface
 * 
 * @author GStack
 *
 */
public interface ChainableCommand extends BackgroundCommand
{
	public boolean hadException();
	public boolean isCancelled();
}
