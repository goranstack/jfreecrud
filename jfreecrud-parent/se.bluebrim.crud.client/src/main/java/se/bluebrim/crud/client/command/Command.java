package se.bluebrim.crud.client.command;

/**
 * Interface implemented by objects that implements the Command Pattern.
 * Commands are separated from our Swing Actions for various reasons. Actions
 * are singletons and a single action instance can be shared between several
 * Swing components. A command is instantiated every time the action is triggered.
 * The command keep any state associated with the task. <br>
 *  
 * @author GStack
 * 
 */
public interface Command
{	
	public abstract void run();
}
