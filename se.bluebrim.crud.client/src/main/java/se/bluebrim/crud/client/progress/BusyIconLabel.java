package se.bluebrim.crud.client.progress;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import se.bluebrim.crud.client.command.ProgressVisualizer;


/**
 * Used to animate long running tasks by displaying an icon sequence. The icons
 * are borrowed from the Swing Application Framework JSR (JSR 296)
 * 
 * @author GStack
 * 
 */
public class BusyIconLabel extends JLabel implements ProgressVisualizer
{
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;

	public BusyIconLabel()
	{
		busyIconTimer = new Timer(30, new UpdateBusyIcon());
		idleIcon = loadIcon("idle-icon.png");
		for (int i = 0; i < busyIcons.length; i++) {
		    busyIcons[i] = loadIcon("busy-icon" + i + ".png");
		}
		setIcon(idleIcon);
		setIconTextGap(0);
		setPreferredSize(new Dimension(16,16));
	}

	private ImageIcon loadIcon(String name)
	{
		return new ImageIcon(getClass().getResource(name));
	}

	private class UpdateBusyIcon implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
			setIcon(busyIcons[busyIconIndex]);
		}
	}	

	public void startProgressAnimation()
	{
		if (!busyIconTimer.isRunning())
		{
			setIcon(busyIcons[0]);
			busyIconIndex = 0;
			busyIconTimer.start();
		}
	}

	public void stopProgressAnimation()
	{
		busyIconTimer.stop();
		setIcon(idleIcon);
	}

}
