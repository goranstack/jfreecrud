package se.bluebrim.crud.client.command;


/**
 * 
 * @author GStack
 *
 */
public interface BackgroundCommand extends Command
{
	public interface DoneListener
	{
		public void done();
	}

	public abstract void run(BackgroundCommand.DoneListener doneListener);
}