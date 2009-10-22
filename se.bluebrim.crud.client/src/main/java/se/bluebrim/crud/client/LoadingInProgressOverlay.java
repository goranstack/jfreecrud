package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLayeredPane;

/**
 * Instances of this class is used to display a LoadingInProgressPanel
 * on top of a dimmed target component.<br>
 * JLayeredPane won't work if any layout is set according to:
 * http://forum.java.sun.com/thread.jspa?forumID=57&threadID=770737 <br>
 * Therefore we have to do all layouting by our self.
 * 
 * @author GStack
 */
public class LoadingInProgressOverlay extends JLayeredPane
{
	private Component targetComponent;
	private LoadingInProgressPanel loadingInProgressPanel;
	
	public LoadingInProgressOverlay()
	{
		loadingInProgressPanel = new LoadingInProgressPanel();		
	}
	
	public LoadingInProgressOverlay(Component targetComponent)
	{
		this();
		setTargetComponent(targetComponent);
	}
	
	public void startLoadingInProgressFeedback()
	{
		add(loadingInProgressPanel, JLayeredPane.DRAG_LAYER);
		loadingInProgressPanel.startProgressAnimation();
		repaint();
	}
			
	public void stopLoadingInProgressFeedback()
	{
		loadingInProgressPanel.stopProgressAnimation();
		remove(loadingInProgressPanel);
		repaint();
	}

	@Override
	public Dimension getPreferredSize()
	{
		return targetComponent == null ? super.getPreferredSize() : targetComponent.getPreferredSize();
	}
				
	@Override
	public void doLayout()
	{
		super.doLayout();
		if (targetComponent != null)
		{
			targetComponent.setSize(getSize());
			loadingInProgressPanel.setSize(getSize());
		}
	}

	/**
	 * Remove previous target component if there is one
	 */
	public void setTargetComponent(Component targetComponent)
	{
		if (this.targetComponent != null)
			remove(this.targetComponent);
		this.targetComponent = targetComponent;
		add(targetComponent, JLayeredPane.DEFAULT_LAYER);
	}
}