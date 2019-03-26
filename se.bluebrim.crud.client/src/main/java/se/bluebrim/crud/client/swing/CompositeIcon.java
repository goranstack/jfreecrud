package se.bluebrim.crud.client.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 * Use a CompositeIcon to combine several icons into one single icon. The
 * icons are drawn in the order the are added.
 * 
 * @author G Stack
 */
public class CompositeIcon implements Icon
{
	private List<Icon> icons = new ArrayList<Icon>();
	private int width = -1;
	private int height = -1; 

	public CompositeIcon(Icon icon1, Icon icon2)
	{
		if (icon1 != null)
			icons.add(icon1);
		if (icon2 != null)
			icons.add(icon2);
	}
	
	@Override
	public int getIconHeight()
	{
		if (height == -1)
		{
			height = Integer.MIN_VALUE;
			for (Icon icon : icons)
				height = Math.max(height, icon.getIconHeight());
		}
		return height;
	}

	@Override
	public int getIconWidth()
	{
		if (width == -1)
		{
			width = Integer.MIN_VALUE;
			for (Icon icon : icons)
				width = Math.max(width, icon.getIconWidth());
		}
		return width;
	}

	@Override
	/**
	 * Icons of different size is centered on top of each others
	 */
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		for (Icon icon : icons)
		{
			int xt = (getIconWidth() - icon.getIconWidth()) / 2;
			int yt = (getIconHeight() - icon.getIconHeight()) / 2;
			icon.paintIcon(c, g, x + xt, y +yt);
		}			
	}
	
	public void addIcon(Icon icon)
	{
		icons.add(icon);
		width = -1;
		height = -1;
	}
	
	/**
	 * The dimming performed by swing on icons in disabled button
	 * do not work on CompositeIcon.
	 *
	 */
	public class DimmedCompositeIcon extends CompositeIcon
	{

		public DimmedCompositeIcon(Icon icon1, Icon icon2)
		{
			super(icon1, icon2);
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			super.paintIcon(c, g, x, y);
		}
		
	}
}

