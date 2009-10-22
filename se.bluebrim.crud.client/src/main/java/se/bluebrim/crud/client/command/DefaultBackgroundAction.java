package se.bluebrim.crud.client.command;

import java.awt.event.ActionEvent;

/**
 * Convenient action superclass that can be sub classed in cases where 
 * the background process has no particular state or there is no need for sub
 * classing the background command for some other reason. See {@link BackgroundCommandTest}
 * how to handle cases where the background command has a state for example start time
 * for calculating duration.
 * 
 * @author GStack
 *
 */
public abstract class DefaultBackgroundAction extends DefaultAction
{

	public DefaultBackgroundAction(String name)
	{
		super();
		setActionName(name);
	}
	
					
	@Override
	protected final void execute(ActionEvent evt) throws Exception
	{
		new InternalBackgroundCommand().run();
	}
	
	protected void prepare()
	{
	}
	
	protected abstract Object runInBackground() throws Exception;
	
	protected abstract void publishResult(Object result) throws Exception;
	

	protected void cancelled()
	{
	}
	
	protected void handleException(Throwable t)
	{
	}
	

	private class InternalBackgroundCommand extends DefaultBackgroundCommand
	{
		
		public InternalBackgroundCommand()
		{
			super();
		}

		@Override
		protected void prepare()
		{
			DefaultBackgroundAction.this.prepare();
		}
		
		@Override
		protected Object runInBackground() throws Exception
		{
			return DefaultBackgroundAction.this.runInBackground();
		}
		
		@Override
		protected void publishResult(Object result) throws Exception
		{
			DefaultBackgroundAction.this.publishResult(result);
		}
		
		@Override
		protected void canceled()
		{
			DefaultBackgroundAction.this.cancelled();
		}
		
		@Override
		protected void handleException(Exception e)
		{
			DefaultBackgroundAction.this.handleException(e);
		}
		
		
	}
	
	
}
