package se.bluebrim.crud.client.remote;

import java.awt.Component;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.springframework.remoting.RemoteConnectFailureException;

import se.bluebrim.crud.client.command.DefaultExceptionHandler;
import se.bluebrim.crud.exception.InternalServerException;

/**
 * Subclass that handles specific remote method invocation exceptions
 * 
 * @author GStack
 *
 */
public class RemoteExceptionHandler extends DefaultExceptionHandler
{
	private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("se.bluebrim.crud.client.remote.Remote");
	private String hostName;
	
	public RemoteExceptionHandler(Component dialogOwner, String hostName)
	{
		super(dialogOwner);
		this.hostName = hostName;
	}
	
	@Override
	public void handleException(Throwable t)
	{
		if (t instanceof RemoteException || t instanceof RemoteConnectFailureException)
		{
			String title = resourceBundle.getString("server.connection.lost.title");
			String basicErrorMessage = MessageFormat.format(resourceBundle.getString("server.connection.lost"), hostName);
			showExceptionDialog(title, basicErrorMessage, null, null, t);
			return;
		} 
		InternalServerException internalServerException = unwrapInternalServerException(t);
		if (internalServerException != null)
		{
			String title = resourceBundle.getString("server.exception.title");
			String basicErrorMessage = resourceBundle.getString("server.exception.message");
			String detailedErrorMessage = MessageFormat.format(resourceBundle.getString("server.exception.detail"), t.getMessage());
			showExceptionDialog(title, basicErrorMessage, detailedErrorMessage, null, t);
			return;
		}
		super.handleException(t);
	}
	
	private InternalServerException unwrapInternalServerException(Throwable t)
	{
		Throwable current = t;
		while(current != null) {
			if (current instanceof InternalServerException)
			{
				return (InternalServerException)current;					
			}			
			current = current.getCause();
		}
		return null;
	}


}
