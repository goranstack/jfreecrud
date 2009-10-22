package se.bluebrim.crud.client.command;

/**
 * Used to handle conditions in CommandChain's
 * 
 * @author GStack
 *
 */
public class ConditionalCommand implements ChainableCommand
{
	public enum Condition{TRUE_COMMAND, FALSE_COMMAND, NO_COMMAND};
	
	private ChainableCommand trueCommand;
	private ChainableCommand falseCommand;
	private Condition condition;
	
	/**
	 * Accepts null in the arguments
	 */
	public ConditionalCommand(ChainableCommand trueCommand, ChainableCommand falseCommand)
	{
		super();
		this.trueCommand = trueCommand;
		this.falseCommand = falseCommand;
		if (this.trueCommand == null)
			this.trueCommand = new NoOperationBackgroundCommand();
		if (this.falseCommand == null)
			this.falseCommand = new NoOperationBackgroundCommand();
	}

	@Override
	public void run(DoneListener doneListener)
	{
		if (condition == Condition.TRUE_COMMAND)
			trueCommand.run(doneListener);
		else
			if (condition == Condition.FALSE_COMMAND)
				falseCommand.run(doneListener);
			else
				doneListener.done();
	}

	@Override
	public void run()
	{
		if (condition == Condition.TRUE_COMMAND)
			trueCommand.run();
		else
			if (condition == Condition.FALSE_COMMAND)
				falseCommand.run();
	}

	public void setCondition(Condition condition)
	{
		this.condition = condition;
	}

	@Override
	public boolean hadException()
	{
		if (condition == Condition.TRUE_COMMAND)
			return trueCommand.hadException();
		else if (condition == Condition.FALSE_COMMAND)
			return falseCommand.hadException();
		else
			return false;
	}

	@Override
	public boolean isCancelled()
	{
		if (condition == Condition.TRUE_COMMAND)
			return trueCommand.isCancelled();
		else if (condition == Condition.FALSE_COMMAND)
			return falseCommand.isCancelled();
		else
			return false;
	}	
	
}
