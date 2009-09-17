package se.bluebrim.crud.client.command;

import java.awt.Component;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

/**
 * Abstract superclass to commands that are run in the current thread.
 * 
 * @author GStack
 *
 */
public abstract class DefaultForegroundCommand extends DefaultCommand
{
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(DefaultForegroundCommand.class.getPackage().getName() + ".Command");

	/**
	 * Let the user make a confirmation. Centered on parent.
	 * 
	 * @param message Info to the user.
	 * @param parent Parent to put the dialog on top.
	 * @return boolean True if ok, otherwise false.
	 */
	public static boolean confirm(String message, Component parent)
	{
		Object[] options = { BUNDLE.getString("ok"),
				BUNDLE.getString("cancel") };
		return (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(parent, message, BUNDLE
				.getString("question"), JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
				options[1]));
	}

	public static boolean userConfirmsOverwriteIfExists(File file, Component dialogParent)
	{
		if (file.exists())
		{
			if (confirm("<html><strong>" + file.getName() + "</strong> finns redan. Vill du ersätta?</html>",
					dialogParent))
				if (file.canWrite())
					return true;
				else
				{
					error("Det är inte tillåtet att ersätta: " + file.getName(), dialogParent);
					return false;
				}
			return false;
		}
		else
			return true;
	}
	
	/**
	 * Show an error with only an ok button. The dialog is centered on the screen.
	 * 
	 * @param message
	 *          The message to the user
	 */
	public static boolean error(String message) 
	{
		return error(message, null);
	}

	/**
	 * Show an error with only an ok button.
	 */
	public static boolean error(String message, Component parent) 
	{
		return error(message, BUNDLE.getString("ok"), parent);
	}
	
	/**
	 * Show an error with only an ok button.
	 */
	public static boolean error(String message, String buttonText, Component parent) 
	{
		Object[] options = { buttonText };
		return (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(parent, message, 
				BUNDLE.getString("error"), 
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]));
	}


	@Override
	public final void run()
	{
		try
		{
			tryRun();
		}
		catch (Exception e)
		{
			defaultExceptionHandler.handleException(e);
		}
	}
	
	protected abstract void tryRun() throws Exception;

}
