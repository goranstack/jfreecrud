package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * Abstract superclass to panels that contains pairs of label and value.
 * 
 * @author GStack
 *
 */
public abstract class AbstractFormPanel extends AbstractPanel
{
	
	public AbstractFormPanel()
	{
	}
	
	public AbstractFormPanel(LayoutManager layoutManager)
	{
		super(layoutManager);
	}

	/**
	 * WrapLabel = true is used when the component is much higher than the label and the the label should
	 * vertically align to the top of the component.
	 */
	public abstract JComponent addFormRow(String labelText, Component component, boolean wrapLabel);
	
	public abstract JComponent addFormRow(JLabel label, Component component, boolean wrapLabel);

	public abstract JComponent addFormRow(Component component1 , Component component2);
	
	public abstract JComponent addFormRow(String labelText, Component component1 , Component component2);
	
	public abstract JComponent addFormRow(String labelText, Component component);
	
	public abstract JComponent addFormRow(JLabel label, Component component);
		
	public abstract void adjustLabelWidthsToLargest();
	
	public abstract double getMaxLabelWidth();
	
	public abstract void setLabelWidths(double labelWidth);
	
}
