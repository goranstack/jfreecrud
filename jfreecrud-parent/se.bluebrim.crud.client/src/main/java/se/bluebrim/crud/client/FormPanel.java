package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import nu.esox.gui.layout.ColumnLayout;
import nu.esox.gui.layout.RowLayout;


/**
 * Used to create forms meaning a panel with pairs of label and value
 * 
 * @author GStack
 *
 */
public class FormPanel extends AbstractFormPanel
{
	private List<JLabel> labels = new ArrayList<JLabel>();
	
	public FormPanel() 
	{
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new ColumnLayout(5));
	}
	
	public FormPanel(LayoutManager layoutManager) 
	{
		super(layoutManager);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	
	/**
	 * WrapLabel = true is used when the component is much higher than the label and the the label should
	 * vertically align to the top of the component.
	 */
	@Override
	public JComponent addFormRow(String labelText, Component component, boolean wrapLabel)
	{
		return addFormRow(new JLabel(labelText), component, wrapLabel);
	}
	
	@Override	
	public JComponent addFormRow(JLabel label, Component component, boolean wrapLabel)
	{
		JPanel labelValuePairBox = new JPanel(new RowLayout(5, true, false)); 
		labelValuePairBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labels.add(label);
		if (wrapLabel)
		{
			Box labelWrapper = Box.createVerticalBox();
			labelWrapper.add(label);
			labelWrapper.add(Box.createVerticalGlue());
			labelValuePairBox.add(labelWrapper);
		} 
		else
			labelValuePairBox.add(label);

		labelValuePairBox.add(component);
		add(labelValuePairBox);
		return labelValuePairBox;
	}
	
	@Override
	public JComponent addFormRow(Component component1 , Component component2)
	{
		JPanel panel = new JPanel(new RowLayout(5, true, false));
		panel.add(component1);
		panel.add(component2);
		add(panel);
		return panel;
	}

	
	@Override
	public JComponent addFormRow(String labelText, Component component1 , Component component2)
	{
		JPanel panel = new JPanel(new RowLayout(5, true, false));
		panel.add(component1);
		panel.add(component2);
		return addFormRow(labelText, panel, false);
	}

	@Override	
	public JComponent addFormRow(String labelText, Component component)
	{
		return addFormRow(labelText, component, false);
	}
	
	@Override
	public JComponent addFormRow(JLabel label, Component component)
	{
		return addFormRow(label, component, false);		
	}
		
	@Override
	public void adjustLabelWidthsToLargest()
	{
		setLabelWidths(getMaxLabelWidth());
	}

	public double getMaxLabelWidth()
	{
		double maxLabelWidth = 0;
		for (JLabel label : labels)
			maxLabelWidth = label.getPreferredSize().getWidth() > maxLabelWidth ? label.getPreferredSize().getWidth() : maxLabelWidth;
		return maxLabelWidth;
	}

	public void setLabelWidths(double labelWidth)
	{
		for (JLabel label : labels)
		{
			label.setMinimumSize(new Dimension((int)labelWidth, (int)label.getPreferredSize().getHeight()));
			label.setPreferredSize(new Dimension((int)labelWidth, (int)label.getPreferredSize().getHeight()));
		}
	}
	
	/**
	 * Since you can't set the with of a JTextPane by specifying a number of columns
	 * you can use this method to give a JTextPane the same width as the component
	 * above or below. 
	 */
	public void setDimension(JTextPane textPane, JComponent sameWidthAs, int noOfRows)
	{
		Dimension dim = sameWidthAs.getPreferredSize();
		textPane.setPreferredSize(new Dimension((int)dim.getWidth(), (int)dim.getHeight() * noOfRows));		
	}


}
