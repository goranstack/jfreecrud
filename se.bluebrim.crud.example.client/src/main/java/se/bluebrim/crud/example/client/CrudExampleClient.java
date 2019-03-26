package se.bluebrim.crud.example.client;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.Locale;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import se.bluebrim.crud.client.command.AccumulativeCursorHandler;
import se.bluebrim.crud.client.command.DefaultAction;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.client.command.DefaultCommand;
import se.bluebrim.crud.client.command.DefaultExceptionHandler;
import se.bluebrim.crud.client.command.ProgressVisualizer;
import se.bluebrim.crud.client.command.WaitCursorFrame;
import se.bluebrim.crud.client.remote.RemoteExceptionHandler;
import se.bluebrim.crud.example.common.ServiceLocator;


/**
 * The client part of the Crud Example application. Run the server before
 * running this class.
 * 
 * @author GStack
 *
 */
public class CrudExampleClient
{

	private static Logger logger = Logger.getLogger(CrudExampleClient.class.getName());

	public static void main(String[] args)
	{
		new CrudExampleClient().run();		
	}
	
	public CrudExampleClient()
	{
		// Initialize Spring framework
		ServiceLocator.init("application-context.xml");
		DefaultCommand.setDefaultProgressVisualizer(new ProgressVisualizer(){

			@Override
			public void startProgressAnimation()
			{
			}

			@Override
			public void stopProgressAnimation()
			{
			}});
	}
	
	private void run()
	{
		UserCrudPanel userCrudPanel = new UserCrudPanel();
		userCrudPanel.initialize();
		userCrudPanel.refresh();
		openInWindow(userCrudPanel, new Point(50, 50));
		logger.info("Client started");
	}
	
	protected WaitCursorFrame openInWindow(Component panel, Point location)
	{
		WaitCursorFrame window = new WaitCursorFrame("Test of: " + this.getClass().getName() + " in locale: " + Locale.getDefault());
		initCommands(window);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().add(panel);
		window.setLocation(location);
		window.setSize(700, 500);
		window.setSize(window.getWidth(), Math.min(window.getHeight(), GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - 40));

		window.setVisible(true);
		return window;
	}

	/**
	 * In a real production environment the server name to the RemoteExceptionHandler is retrieved
	 * from the server.
	 */
	private void initCommands(WaitCursorFrame mainFrame)
	{
		AccumulativeCursorHandler accumulativeCursorHandler = new AccumulativeCursorHandler(mainFrame);
		DefaultExceptionHandler defaultExceptionHandler = new RemoteExceptionHandler(mainFrame, "Crud Example Server");
		DefaultAction.setDefaultCursorHandler(accumulativeCursorHandler);
		DefaultAction.setDefaultExceptionHandler(defaultExceptionHandler);
		DefaultBackgroundCommand.setDefaultCursorHandler(accumulativeCursorHandler);
		DefaultBackgroundCommand.setDefaultExceptionHandler(defaultExceptionHandler);
	}


}
