package se.bluebrim.crud.client.command;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.bushe.swing.action.BasicAction;



/**
 * Abstract super class to all actions in CRUD applications. Actions are
 * static objects created once when the a UI is created that contains menus
 * and buttons. The same instance of an action can be shared between menus
 * and buttons. In many cases a command is created that performs
 * the actual task. When the task has a background process that is always the case.<br>  
 * This class is a client fault barrier as
 * described in se.bluebrim.crud.client.command.package.html.
 * An ExceptionHandler and a CursorHandler should be injected at startup
 * by the application after the main window has been created. <br> 
 * The behavior of the DefaultExceptionHandler is to show an
 * error dialog when an action throws an exception. <br>
 * 
 * @author GStack
 *
 */
public abstract class DefaultAction extends BasicAction
{
	protected static final CursorHandler NULL_CURSOR_HANDLER = new CursorHandler(){
		@Override
		public void resetWaitCursor(){}
		@Override
		public void setWaitCursor(){}};
		
	protected static final ExceptionHandler NULL_EXCEPTION_HANDLER = new ExceptionHandler(){
		public void handleException(Throwable t)
		{				
		}};
	
	private static ExceptionHandler defaultExceptionHandler = NULL_EXCEPTION_HANDLER;
	private static CursorHandler defaultCursorHandler = NULL_CURSOR_HANDLER;	
	
	public DefaultAction()
	{
		super();
	}
	
	public DefaultAction(String actionName)
	{
		super();
		setActionName(actionName);
	}

	public DefaultAction(String actionName, Icon smallIcon)
	{
		this(actionName);
		setSmallIcon(smallIcon);
	}
	
	/**
	 * Typically overridden to do the action's work.
	 */
	protected abstract void execute(ActionEvent evt) throws Exception;

	/**
	 * Prevent accidentally override
	 */
	public final void actionPerformedTemplate(ActionEvent actionEvent)
	{
		super.actionPerformedTemplate(actionEvent);
	}
	
	@Override
	protected void actionPerformedTry()
	{
		defaultCursorHandler.setWaitCursor();
	}
	
	@Override
	protected final void actionPerformedCatch(Throwable t)
	{
    defaultExceptionHandler.handleException(t);
	}
	
	@Override
	protected final void actionPerformedFinally()
	{
		defaultCursorHandler.resetWaitCursor();
	}

	public static void setDefaultExceptionHandler(ExceptionHandler exceptionHandler)
	{
		DefaultAction.defaultExceptionHandler = exceptionHandler;
	}
	
	public static ExceptionHandler getDefaultExceptionHandler()
	{
		return DefaultAction.defaultExceptionHandler;
	}

	public static void setDefaultCursorHandler(CursorHandler defaultCursorHandler)
	{
		DefaultAction.defaultCursorHandler = defaultCursorHandler;
	}

}
