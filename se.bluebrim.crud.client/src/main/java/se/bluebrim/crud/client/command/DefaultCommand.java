package se.bluebrim.crud.client.command;


/**
 * Abstract super class to all command objects. A command is an object that performs
 * a certain task often triggered by an user gesture. Commands are usually created
 * and run every time an action is triggered. <br> 
 * A DefaultCommand is running in the EDT(Event dispatching thread) and is 
 * therefore blocking the UI while running. Should not be used for long running tasks 
 * involving server calls.
 * 
 * @author GStack
 *
 */
public abstract class DefaultCommand implements Command
{
	protected static ExceptionHandler defaultExceptionHandler = DefaultAction.NULL_EXCEPTION_HANDLER;
	protected static CursorHandler defaultCursorHandler = DefaultAction.NULL_CURSOR_HANDLER;
	private static ProgressVisualizer defaultProgressVisualizer;
	private ProgressVisualizer progressVisualizer;
	
	public DefaultCommand()
	{
		super();
	}

	@Override
	public abstract void run();

	private ProgressVisualizer getProgressVisualizer()
	{
		return progressVisualizer != null ? progressVisualizer : defaultProgressVisualizer;
	}

	public void setProgressVisualizer(ProgressVisualizer progressVisualizer)
	{
		this.progressVisualizer = progressVisualizer;
	}

	public static void setDefaultProgressVisualizer(ProgressVisualizer defaultProgressVisualizer)
	{
		DefaultCommand.defaultProgressVisualizer = defaultProgressVisualizer;
	}
	
	public static void setDefaultExceptionHandler(ExceptionHandler exceptionHandler)
	{
		DefaultCommand.defaultExceptionHandler = exceptionHandler;
	}

	public static void setDefaultCursorHandler(CursorHandler defaultCursorHandler)
	{
		DefaultCommand.defaultCursorHandler = defaultCursorHandler;
	}

	protected void startProgressAnimation()
	{
		getProgressVisualizer().startProgressAnimation();
	}
	
	protected void stopProgressAnimation()
	{
		getProgressVisualizer().stopProgressAnimation();
	}


}
