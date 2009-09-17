package se.bluebrim.crud.client.swing;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Subclass used to limit the number of characters entered in a JTextField
 * 
 * @author GStack
 * 
 */
public class MaxLengthDocument extends PlainDocument
{

	private int limit;

	// optional uppercase conversion
	private boolean toUppercase = false;

	public MaxLengthDocument(int limit) 
	{
		super();
		this.limit = limit;
	}

	public MaxLengthDocument(int limit, boolean upper) 
	{
		super();
		this.limit = limit;
		toUppercase = upper;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
	{
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit)
		{
			if (toUppercase)
				str = str.toUpperCase();
			super.insertString(offset, str, attr);
		} else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}

}
