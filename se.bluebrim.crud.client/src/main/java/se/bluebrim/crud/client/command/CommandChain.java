package se.bluebrim.crud.client.command;

import java.util.ArrayList;
import java.util.List;


/**
 * Background commands can be combined in to command chains that makes it possible 
 * to perform several background tasks when an action is triggered. For example
 * its common to reload data from the server after updating and since reloading
 * of data can be run separate from the refresh button its nice to combine these
 * to commands into the save action. Another example is the refresh action where
 * two commands are combined: The save-if-user-wants-command and the refresh-command. <br>
 * The commands is run in the same order as the are added to the chain.
 * A command waits for the previous command to finish before it runs. If one
 * command is canceled or had an exception all subsequent commands are skipped.
 * 
 * @author GStack
 *
 */
public class CommandChain implements ChainableCommand
{
	private List<ChainableCommand> commands;
	private BackgroundCommand.DoneListener doneListener;
	
	public CommandChain()
	{
		commands = new ArrayList<ChainableCommand>();
	}
	/**
	 * Convenience constructor
	 */
	public CommandChain(ChainableCommand command1, ChainableCommand command2)
	{
		this();
		addCommand(command1);
		addCommand(command2);
	}
	
	/**
	 * Convenience constructor
	 */
	public CommandChain(ChainableCommand command1, ChainableCommand command2, ChainableCommand command3)
	{
		this(command1, command2);
		addCommand(command3);		
	}
	
	
	public void addCommand(ChainableCommand command)
	{
		commands.add(command);
	}


	@Override
	public void run()
	{
		run(0);
	}
	
	@Override
	public void run(BackgroundCommand.DoneListener doneListener)
	{
		if (this.doneListener != null)
			throw new RuntimeException("You can't run a command twice");
		this.doneListener = doneListener;
		run(0);		
	}
	
	/**
	 * Runs the specified command from the command list. Use
	 * the callback interface to continue with the next command when
	 * the specified command is finished. Terminate the whole chain 
	 * if the previous command is canceled or had an exception.  
	 */
	private void run(final int index)
	{
		if (index > 0)
		{
			ChainableCommand previousCommand = commands.get(index - 1);
			if (previousCommand.isCancelled() || previousCommand.hadException())
			{
				if (doneListener != null)
					doneListener.done();
				return;
			}
		}
		BackgroundCommand command = commands.get(index);
		command.run(new BackgroundCommand.DoneListener()
		{
			@Override
			public void done()
			{
				if (index < commands.size() - 1)
					run(index + 1);
				else if (doneListener != null)
					doneListener.done();
			}
		});

	}
	@Override
	public boolean hadException()
	{
		boolean hadException = false;
		for (ChainableCommand command : commands)
		{
			if (command.hadException())
			{
				hadException = true;
				break;
			}
		}
		return hadException;
	}
	
	@Override
	public boolean isCancelled()
	{
		boolean isCancelled = false;
		for (ChainableCommand command : commands)
		{
			if (command.isCancelled())
			{
				isCancelled = true;
				break;
			}
		}
		return isCancelled;
	}


}
