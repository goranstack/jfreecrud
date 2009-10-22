package se.bluebrim.crud.client.command;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;


/**
 * 
 * @author GStack
 *
 */
public class WaitCursorFrame extends JFrame implements CursorHandler
{	
	public WaitCursorFrame() throws HeadlessException
	{
		super();
	}

	public WaitCursorFrame(GraphicsConfiguration gc)
	{
		super(gc);
	}

	public WaitCursorFrame(String title, GraphicsConfiguration gc)
	{
		super(title, gc);
	}

	public WaitCursorFrame(String title) throws HeadlessException
	{
		super(title);
	}

	@Override
	public void resetWaitCursor()
	{
		Component glassPane = getGlassPane();
		glassPane.setCursor(Cursor.getDefaultCursor());
		glassPane.setVisible(false);	
	}

	@Override
	public void setWaitCursor()
	{
		Component glassPane = getGlassPane();
		glassPane.setVisible(true);
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
	}

}
