package se.bluebrim.crud.client.esox;

import java.math.BigDecimal;

import javax.swing.JFormattedTextField;

import nu.esox.gui.aspect.FormattedTextFieldAdapter;
import nu.esox.gui.aspect.ModelOwnerIF;

public class XFormattedTextFieldAdapter extends FormattedTextFieldAdapter
{
  private Class m_aspectClass;
  
	public XFormattedTextFieldAdapter(JFormattedTextField tf, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName,
			String setAspectMethodName, Class aspectClass, Object nullValue, Object undefinedValue) {
		super(tf, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, null, nullValue, undefinedValue);
		
    m_aspectClass = aspectClass;
	}

	/**
	 * TODO: Does anyone use BigInteger?
	 */
	protected Object deriveAspectValue(Object projectedValue)
	{
		Object value = super.deriveAspectValue(projectedValue);
		if (value instanceof Number)
		{
			if (m_aspectClass == int.class || m_aspectClass == Integer.class)
				return new Integer(((Number) value).intValue());
			
			else if (m_aspectClass == double.class || m_aspectClass == Double.class)
				return new Double(((Number) value).doubleValue());
			
			else if (m_aspectClass == short.class || m_aspectClass == Short.class)
				return new Short(((Number) value).shortValue());
			
			else if (m_aspectClass == long.class || m_aspectClass == Long.class)
				return new Long(((Number) value).longValue());
			
			else if (m_aspectClass == float.class || m_aspectClass == Float.class)
				return new Float(((Number) value).floatValue());
			
			else if (m_aspectClass == byte.class || m_aspectClass == Byte.class)
				return new Byte(((Number) value).byteValue());
			
			else if (m_aspectClass == BigDecimal.class)
				return new BigDecimal(((Number) value).doubleValue());
		} else
			if (projectedValue != null)
				throw new RuntimeException("Expected Numeric value or null");
		return value;
	}
	
	@Override
	protected void update(Object projectedValue)
	{
		if (projectedValue == null || projectedValue instanceof Number)
			super.update(projectedValue);
		else
			throw new RuntimeException("Expected Numeric value");
	}

}
