package se.bluebrim.crud.client.esox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.SwingUtilities;

import nu.esox.gui.aspect.AbstractAdapter;
import nu.esox.gui.aspect.ModelOwnerIF;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Connects a JXDatePicker with a Date property of an Observable model
 * 
 * @author GStack
 *
 */
public class DatePickerAdapter extends AbstractAdapter implements ActionListener
{
	private final JXDatePicker m_datePicker;
	private transient boolean m_isUpdating = false;

	public DatePickerAdapter(JXDatePicker dateChooser, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName) 
	{
		this(dateChooser, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, Date.class, null, null);
	}

	public DatePickerAdapter(JXDatePicker dateChooser, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName,
			Class aspectClass, String nullValue, String undefinedValue) 
	{
		super(modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, nullValue, undefinedValue, null);

		m_datePicker = dateChooser;
		m_datePicker.addActionListener(this);
		m_datePicker.setEnabled(setAspectMethodName != null);
		update();
	}

	/**
	 * Without invoke later you get an exception with message: <br>
	 * "try to mutate in notification" from Swing
	 */
	protected void update(final Object projectedValue)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				m_isUpdating = true;
				m_datePicker.setDate((Date) projectedValue);
				m_isUpdating = false;
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (m_isUpdating)
			return;
		setAspectValue(m_datePicker.getDate());		
	}
}
