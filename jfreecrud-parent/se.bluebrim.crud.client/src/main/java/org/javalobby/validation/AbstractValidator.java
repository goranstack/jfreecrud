package org.javalobby.validation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import se.bluebrim.crud.client.UiUtil;

/**
 * Found this at: http://www.javalobby.org/java/forums/t20551.html
 * 
 * This class handles most of the details of validating a component, including
 * all display elements such as pop up help boxes and color changes.
 * 
 * @author Michael Urban, modifications by GStack
 * @version Beta 1
 * @see WantsValidationStatus
 */

public abstract class AbstractValidator extends InputVerifier implements KeyListener
{
	public static ResourceBundle BUNDLE = ResourceBundle.getBundle(AbstractValidator.class.getPackage().getName() + ".validation");

	private JDialog popup;
	private Component parent;
	private JLabel messageLabel;
	private JLabel image;
	private Point point;
	private Dimension cDim;
	private Color color;

	public AbstractValidator(JPanel parent, JComponent c, String message)
	{
		color = new Color(243, 255, 159);
		c.addKeyListener(this);
		messageLabel = new JLabel(message + " ");
		image = new JLabel(UiUtil.getIcon("exception_16x16.png", AbstractValidator.class));
		this.parent = parent;
	}

	/**
	 * Use lazy initialization since the panel has no window yet in the
	 * constructor
	 */
	private JDialog getPopup()
	{
		if (popup == null)
		{
			popup = new JDialog(SwingUtilities.windowForComponent(parent));
			popup.getContentPane().setLayout(new FlowLayout());
			popup.setUndecorated(true);
			popup.getContentPane().setBackground(color);
			popup.getContentPane().add(image);
			popup.getContentPane().add(messageLabel);
			popup.setFocusableWindowState(false);
		}
		return popup;
	}

	/**
	 * Implement the actual validation logic in this method. The method should
	 * return false if data is invalid and true if it is valid. It is also
	 * possible to set the popup message text with setMessage() before returning,
	 * and thus customize the message text for different types of validation
	 * problems.
	 * 
	 * @param c
	 *          The JComponent to be validated.
	 * @return false if data is invalid. true if it is valid.
	 */

	protected abstract boolean validationCriteria(JComponent c);

	/**
	 * This method is called by Java when a component needs to be validated. It
	 * should not be called directly. Do not override this method unless you
	 * really want to change validation behavior. Implement validationCriteria()
	 * instead.
	 */

	public boolean verify(JComponent c)
	{
		if (!validationCriteria(c))
		{

			if (parent instanceof WantsValidationStatus)
				((WantsValidationStatus) parent).validateFailed();

			c.setBackground(Color.PINK);
			JDialog popup = getPopup();
			popup.setSize(0, 0);
			popup.setLocationRelativeTo(c);
			point = popup.getLocation();
			cDim = c.getSize();
			popup.setLocation(point.x - (int) cDim.getWidth() / 2, point.y + (int) cDim.getHeight() / 2);
			popup.pack();
			popup.setVisible(true);
			return false;
		}

		c.setBackground(Color.WHITE);

		if (parent instanceof WantsValidationStatus)
			((WantsValidationStatus) parent).validatePassed();

		return true;
	}

	/**
	 * Changes the message that appears in the popup help tip when a component's
	 * data is invalid. Subclasses can use this to provide context sensitive help
	 * depending on what the user did wrong.
	 * 
	 * @param message
	 */

	protected void setMessage(String message)
	{
		messageLabel.setText(message);
	}

	/**
	 * @see KeyListener
	 */

	public void keyPressed(KeyEvent e)
	{
		getPopup().setVisible(false);
	}

	/**
	 * @see KeyListener
	 */

	public void keyTyped(KeyEvent e)
	{
	}

	/**
	 * @see KeyListener
	 */

	public void keyReleased(KeyEvent e)
	{
	}

}