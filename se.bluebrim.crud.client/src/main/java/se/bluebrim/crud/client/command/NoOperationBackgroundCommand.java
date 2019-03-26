package se.bluebrim.crud.client.command;

/**
 * A NoOperationBackgroundCommand is used in cases where you need a dummy background
 * command that do nothing.
 * 
 * @author GStack
 *
 */
public class NoOperationBackgroundCommand extends DefaultBackgroundCommand
{
	@Override
	protected Object runInBackground() throws Exception
	{
		doneListener.done();
		return null;
	}

}
