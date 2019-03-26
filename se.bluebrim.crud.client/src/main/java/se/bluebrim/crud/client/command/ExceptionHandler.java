package se.bluebrim.crud.client.command;

/**
 * Implemented by objects that handles exception raised in actions.
 * The handling of exception in actions vary between applications.
 * For example in the client of e client server application the exception handling 
 * detects certain remote exceptions that totally unknown for a single user desktop application.
 * 
 * @author GStack
 *
 */
public interface ExceptionHandler
{
	public abstract void handleException(Throwable t);
}
