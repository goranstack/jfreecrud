package se.bluebrim.crud.client.command;

import java.util.concurrent.CancellationException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * A Command that runs in a background thread making it possible for the user to
 * do other things while the command is running. Should be used for long running
 * tasks involving server calls.
 * 
 * @author GStack
 * 
 */
public abstract class DefaultBackgroundCommand extends DefaultCommand implements ChainableCommand
{
	protected BackgroundCommand.DoneListener doneListener;	
	private CommandWorker commandWorker;
	private boolean hadException = false;
	
	public DefaultBackgroundCommand()
	{
	}
	
	@Override
	public final void run()
	{
		commandWorker = new CommandWorker();
		try
		{
			prepare();
			if (commandWorker.isCancelled())
			{
				canceled();
				return;
			}
			commandWorker.execute();
		} catch (DefaultBackgroundCommand.CancelCommandException e)
		{
			commandWorker.cancel(true);
		} catch (Throwable t)
		{
			defaultExceptionHandler.handleException(t);
		}
	}
	
	public void run(BackgroundCommand.DoneListener doneListener)
	{
		if (this.doneListener != null)
			throw new RuntimeException("You can't run a command twice");
		this.doneListener = doneListener;
		run();		
	}

	/**
	 * This method is overridden for example to validate command parameters
	 * entered by the user before executing the background task. The command can
	 * be canceled by calling the cancel method.
	 * 
	 */
	protected void prepare()
	{
	}

	/**
	 * Override to implement the task that should be executed in a separate
	 * thread. Typical this method contains a blocking server call. You must not
	 * perform any Swing calls in this method since Swing is not thread safe.
	 */
	protected abstract Object runInBackground() throws Exception;

	/**
	 * This method is overridden to update the Swing user interface with the
	 * result obtained from the runInBackground method.
	 */
	protected void publishResult(Object result) throws Exception
	{
	}
	
	public boolean isCancelled()
	{
		return commandWorker == null ? false: commandWorker.isCancelled();
	}
	
	
	public boolean hadException()
	{
		return hadException;
	}

	/**
	 * This method can be overridden to update the Swing user interface with the
	 * information that no result was obtained since the command was canceled
	 * by the user.
	 */
	protected void canceled()
	{
	}
	
	/**
	 * This method can be overridden to update the Swing user interface with the
	 * information that no result was obtained since the command was interrupted
	 * by an unexpected exception.
	 */
	protected void handleException(Exception e)
	{
		hadException = true;
		defaultExceptionHandler.handleException(e);
	}
	

	/**
	 * Use to cancel further execution of the command if for example the user
	 * presses a cancel button
	 */
	public void cancel()
	{
		stopProgressAnimation();
		commandWorker.cancel(true);
	}
	

	private class CommandWorker extends SwingWorker<Object, Object>
	{
		private static final String NO_RESULT_DUE_TO_EXCEPTION = "No result due to exception";

		CommandWorker()
		{
			super();
		}

		@Override
		protected final Object doInBackground() throws Exception
		{
			try
			{
				startProgressAnimationInEdt();
				Object result = runInBackground();
				return result;
			} catch (Exception e)
			{
				if (!isCancelled())				
					handleExceptionInEdt(e);
				return NO_RESULT_DUE_TO_EXCEPTION;
			} finally
			{
				if (!isCancelled())
					stopProgressAnimationInEdt();			
			}
		}
		
		private void startProgressAnimationInEdt()
		{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run()
				{
					startProgressAnimation();				
				}});
		}
		
		private void stopProgressAnimationInEdt()
		{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run()
				{
					stopProgressAnimation();					
				}});	
		}

		private void handleExceptionInEdt(final Exception e)
		{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run()
				{
					handleException(e);				
				}});			
		}


		@Override
		/**
		 * When cancel a server request this method is immediate call but when
		 * the server calls return we end up in the doInBackground method
		 */
		protected void done()
		{
			try
			{
				defaultCursorHandler.setWaitCursor();
				Object result = get();
				if (!NO_RESULT_DUE_TO_EXCEPTION.equals(result))
					publishResult(result);
			} catch (CancellationException e)
			{
				canceled();
				return;
			} catch (Exception e)
			{
				if (!isCancelled())
					handleException(e);
				return;
			} finally
			{
				defaultCursorHandler.resetWaitCursor();
				if (doneListener != null)
					doneListener.done();
			}
		}
	}


	public static class CancelCommandException extends RuntimeException
	{
		public CancelCommandException(String message)
		{
			super(message);
		}
	}


}
