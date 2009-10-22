package se.bluebrim.crud.client;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import nu.esox.gui.layout.RowLayout;


/**
 * Used to create forms with several columns of label component pairs.
 * Same usage as FormPanel but with the difference that you set a current
 * column that your form rows are added to.
 * 
 * @author GStack
 *
 */
public class MultiColumnFormPanel extends AbstractFormPanel
{
	private List<FormPanel> columns = new ArrayList<FormPanel>();
	private int currentColumn = 0;
	
	public MultiColumnFormPanel(int numberOfColumns) 
	{
		super(new RowLayout(15));
		for (int i = 0; i < numberOfColumns; i++)
		{
			FormPanel column = new FormPanel();
			column.setBorder(null);
			columns.add(column);
			add(column);
		}
	}
	
	public int getCurrentColumn()
	{
		return currentColumn;
	}

	public void setCurrentColumn(int currentColumn)
	{
		this.currentColumn = currentColumn;
	}
	
	@Override
	public JComponent addFormRow(JLabel label, Component component)
	{
		return columns.get(currentColumn).addFormRow(label, component);
	}
	
	@Override
	public JComponent addFormRow(JLabel label, Component component, boolean wrapLabel)
	{
		return columns.get(currentColumn).addFormRow(label, component, wrapLabel);
	}
	
	@Override
	public JComponent addFormRow(String labelText, Component component)
	{
		return columns.get(currentColumn).addFormRow(labelText, component);
	}
	
	@Override
	public JComponent addFormRow(String labelText, Component component, boolean wrapLabel)
	{
		return columns.get(currentColumn).addFormRow(labelText, component, wrapLabel);
	}
	
	@Override
	public JComponent addFormRow(String labelText, Component component1, Component component2)
	{
		return columns.get(currentColumn).addFormRow(labelText, component1, component2);
	}
	
	@Override
	public JComponent addFormRow(Component component1, Component component2)
	{
		return columns.get(currentColumn).addFormRow(component1, component2);
	}
	
	@Override
	public void adjustLabelWidthsToLargest()
	{
		columns.get(currentColumn).adjustLabelWidthsToLargest();
	}

	/**
	 * First column = 0, second column = 1 and so on
	 */
	public FormPanel getColumn(int columnIndex)
	{
		return columns.get(columnIndex);
	}

	@Override
	public double getMaxLabelWidth()
	{
		return columns.get(currentColumn).getMaxLabelWidth();
	}

	@Override
	public void setLabelWidths(double labelWidth)
	{
		columns.get(currentColumn).setLabelWidths(labelWidth);		
	}

}
