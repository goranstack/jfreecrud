package se.bluebrim.crud.client.command;

import java.awt.Component;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * Basic implementation of the ExceptionHandler interface.
 * Subclasses perform more specific exception handling.
 * 
 * @author GStack
 *
 */
public class DefaultExceptionHandler implements ExceptionHandler
{
	protected final static ResourceBundle commandBundle = ResourceBundle.getBundle(DefaultExceptionHandler.class.getPackage().getName() + ".Command");
	private static Logger logger = Logger.getLogger(DefaultExceptionHandler.class);

	private Component dialogOwner;
	
	
	public DefaultExceptionHandler(Component dialogOwner)
	{
		super();
		this.dialogOwner = dialogOwner;
	}

	@Override
	public void handleException(Throwable t)
	{
    showExceptionDialog(commandBundle.getString("command.exception.title"), commandBundle.getString("command.exception"), null, null, t);
 	}
	
	/**
	 * 
	 * @param title must be specified
	 * @param message if null the cause from the exception will be used
	 * @param detailMessage	if null the stack trace will be used
	 * @param email
	 */
	public void showExceptionDialog(final String title, final String message, final String detailMessage, String emailAddress, Throwable t)
	{		
	  logger.error(message, t);
		JXErrorPane.showDialog(dialogOwner, new ErrorInfo(title, message, detailMessage, null, t, Level.SEVERE, null));
	}


}
