package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import se.bluebrim.crud.client.command.CursorHandler;
import se.bluebrim.crud.client.command.DefaultAction;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.client.command.DefaultCommand;
import se.bluebrim.crud.client.command.ExceptionHandler;
import se.bluebrim.crud.client.command.ProgressVisualizer;
import se.bluebrim.crud.client.command.WaitCursorFrame;

public class AbstractPanelTest
{

	public AbstractPanelTest()
	{
		initCommands();

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
	
	/**
	 * Opens the panel in a window once for each supported locale
	 */
	public List<AbstractPanel> openTestWindow(Class panelClass)
	{
		List<AbstractPanel> panels = new ArrayList<AbstractPanel>();
		try
		{
			Point location = new Point(50, 50);
			for (int i = 0; i < AbstractPanel.SUPPORTED_LANGUAGES.length; i++)
			{
				Locale locale = new Locale(AbstractPanel.SUPPORTED_LANGUAGES[i]);
				Locale.setDefault(locale);
				AbstractPanel panel = ((AbstractPanel) panelClass.newInstance());
				if (panel instanceof AbstractCrudPanel)
				{
					((AbstractCrudPanel)panel).initialize();
					((AbstractCrudPanel)panel).refresh();
				}
				panels.add(panel);
				openInWindow(panel, location);
				location.x = location.x + 30;
				location.y = location.y + 30;
			}
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
		return panels;
	}

	protected WaitCursorFrame openInWindow(Component panel, Point location)
	{
		WaitCursorFrame window = new WaitCursorFrame("Test of: " + this.getClass().getName() + " in locale: " + Locale.getDefault());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().add(panel);
		window.setLocation(location);
		window.pack();
		setWindowSize(window);

		window.setVisible(true);
		return window;
	}

	protected void setWindowSize(Window window)
	{
		window.setSize(window.getWidth(), Math.min(window.getHeight(), GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - 40));
	}
	
	private void initCommands()
	{
		CursorHandler cursorHandler = new CursorHandler(){
			@Override
			public void resetWaitCursor(){}
			@Override
			public void setWaitCursor(){}};
		ExceptionHandler exceptionHandler = new ExceptionHandler(){
			@Override
			public void handleException(Throwable t)
			{				
			}};
		DefaultAction.setDefaultCursorHandler(cursorHandler);
		DefaultAction.setDefaultExceptionHandler(exceptionHandler);
		DefaultBackgroundCommand.setDefaultCursorHandler(cursorHandler);
		DefaultBackgroundCommand.setDefaultExceptionHandler(exceptionHandler);
	}

	protected WaitCursorFrame openInInternalFrame(String title, Component panel, Point location)
	{
		JDesktopPane desktop = new JDesktopPane();
		JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
		frame.getContentPane().add(panel);
		frame.setLocation(location);
		frame.setSize(600, 400);
		desktop.add(frame);
		frame.setVisible(true);
		WaitCursorFrame window = openInWindow(desktop, location);
		window.setSize(800, 600);
		return window;
	}

}
