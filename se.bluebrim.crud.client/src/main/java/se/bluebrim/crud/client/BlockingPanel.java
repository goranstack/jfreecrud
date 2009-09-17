package se.bluebrim.crud.client;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

/**
 * Used to block user input and dim under laying components in a JLayeredPane.
 * Could probably be improved based on the facts in: <br>
 * http://weblogs.java.net/blog/alexfromsun/archive/2008/01/disabling_swing.html
 * 
 * @author GStack
 * 
 */
public class BlockingPanel extends JPanel
{
	public BlockingPanel() 
	{
		setOpaque(false);

		// Suck up the events
		addKeyListener((new KeyAdapter()
		{
		}));
		addMouseListener((new MouseAdapter()
		{
		}));
		addMouseMotionListener((new MouseMotionAdapter()
		{
		}));
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g2d.setColor(getBackground());
		g2d.fill(getBounds());
		super.paint(g);
	}

}
