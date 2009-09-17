package se.bluebrim.crud.client.esox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.SwingUtilities;

import nu.esox.gui.aspect.AbstractAdapter;
import nu.esox.gui.aspect.ModelOwnerIF;

import com.toedter.calendar.JDateChooser;

/**
 * Connects a JDateChooser with a Date property of an Observable model
 * 
 * @author GStack
 *
 */
public class DateChooserAdapter extends AbstractAdapter implements PropertyChangeListener
{
	private final JDateChooser m_dateChooser;
	private transient boolean m_isUpdating = false;

	public DateChooserAdapter(JDateChooser dateChooser, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName) 
	{
		this(dateChooser, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, Date.class, null, null);
	}

	public DateChooserAdapter(JDateChooser dateChooser, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName,
			Class aspectClass, String nullValue, String undefinedValue) 
	{
		super(modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, nullValue, undefinedValue, null);

		m_dateChooser = dateChooser;
		m_dateChooser.getDateEditor().addPropertyChangeListener("date", this);
		m_dateChooser.setEnabled(setAspectMethodName != null);
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
				m_dateChooser.setDate((Date) projectedValue);
				m_isUpdating = false;
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		if (m_isUpdating)
			return;
		setAspectValue(m_dateChooser.getDate());
	}
}
