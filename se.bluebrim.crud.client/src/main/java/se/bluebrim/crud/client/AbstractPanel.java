package se.bluebrim.crud.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import nu.esox.gui.ModelPanel;
import nu.esox.gui.aspect.AbstractAdapter;
import nu.esox.gui.aspect.ModelOwnerIF;

import org.javalobby.validation.AbstractValidator;
import org.jdesktop.swingx.border.DropShadowBorder;

import se.bluebrim.crud.client.esox.DateChooserAdapter;
import se.bluebrim.crud.client.swing.MaxLengthDocument;
import se.bluebrim.crud.client.swing.MaxLengthStyledDocument;
import se.bluebrim.crud.debug.BooleanValue;

import com.toedter.calendar.JDateChooser;

/**
 * Abstract superclass to panels supporting the EsoxGui framework
 * by implementing the <code>ModelOwnerIF</code>
 * 
 * @author GStack
 *
 */
public abstract class AbstractPanel extends ModelPanel
{	
	public interface ComponentVisitor
	{
		public void visit(Component component);
	}
	
	public static final BooleanValue SHOW_PANEL_NAME = new BooleanValue(false);
	public static final String[] SUPPORTED_LANGUAGES = new String[]{"sv", "en"};
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dateTimeFormatEditing = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected static NumberFormat integerColumnFormat = NumberFormat.getIntegerInstance();
	public static SimpleDateFormat timeOfDayFormat = new SimpleDateFormat("HH:mm");

	public AbstractPanel() 
	{
		super();
	}

	public AbstractPanel(LayoutManager layoutManager)
	{
		super(layoutManager);
	}
	

	protected void eachComponent(ComponentVisitor visitor)
	{
		eachComponent(this, visitor);
	}
	
	/**
	 * Recursive method traversing the component hierarchy
	 */
	protected void eachComponent(Container container, ComponentVisitor visitor)
	{
		for (Component component : container.getComponents())
		{
			// Makes it possible to stop recursion in a subclass. Used to
			// prevent save button to be enabled when entering text in the time series
			// of TimeSeriesGroupPanel
			if (component instanceof AbstractPanel)
				((AbstractPanel) component).eachComponent((AbstractPanel) component, visitor);
			else if (component instanceof Container)
				eachComponent((Container) component, visitor);
			visitor.visit(component);
		}
	}
	
	public static JComponent createHorizontalButtonPanel()
	{
		class HorizontalButtonPanel extends AbstractPanel{};
		JComponent buttonPanel = new HorizontalButtonPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		return buttonPanel;
	}

	/**
	 * Experimental detail panel title.
	 */
	public static JPanel createTitelPanel(String title)
	{
		JPanel panel = new JPanel(new BorderLayout()){
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D)g;
				Rectangle2D bounds = getBounds();
				Color color1 = Color.LIGHT_GRAY;
				Color color2 = color1.brighter();
				GradientPaint paint = new GradientPaint(0, 0, color1, (float)(bounds.getWidth()), 0, color2);
				g2d.setPaint(paint);
				g2d.fill(bounds);
			}
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(getWidth(), 30);
			}
		};
	
		panel.add(new JLabel(title), BorderLayout.CENTER);
		return panel;
	}
	
	protected JFormattedTextField createDateTextField()
	{
		return new JFormattedTextField(dateFormat);
	}
	
	protected JFormattedTextField createTimeOfDayField()
	{
		JFormattedTextField formattedTextField = new JFormattedTextField(timeOfDayFormat);
		formattedTextField.setColumns(5);
		return formattedTextField;
	}


	protected JFormattedTextField createDateTimeTextField()
	{
		JFormattedTextField f = new JFormattedTextField(dateTimeFormatEditing);
		f.setPreferredSize(new JTextField(15).getPreferredSize());
		f.setMinimumSize(f.getPreferredSize());
		return f;
	}
	
	/**
	 * TODO: Remove static when DateChooserDialog is rewritten.
	 */
	public static JComponent createDatePicker()
	{
		return createDatePicker(false);
	}

	/**
	 * TODO: Remove static when DateChooserDialog is rewritten.
	 */
	public static JComponent createDateTimePicker()
	{
		return createDatePicker(true);
	}
	
	/**
	 * TODO: Remove this method when DateChooserDialog is rewritten.
	 */
	public static Date getPickedDate(JComponent datePicker)
	{
		return ((JDateChooser)datePicker).getDate();
	}
	
	/**
	 * TODO: Remove this method when DateChooserDialog is rewritten.
	 */
	public static void setInitialDate(JComponent datePicker, Date initialDate)
	{
		((JDateChooser)datePicker).setDate(initialDate);
	}

	/**
	 * Other date pickers can be found at Swing Depot: Component Suites,
	 * http://www.javadesktop.org/rollups/components/index.html but be aware of
	 * that <code>com.michaelbaranov.microba.calendar.DatePicker</code> is also
	 * leaking and <code>org.jdesktop.swingx.JXDatePicker</code> do not handle
	 * time only date.
	 */
	private static JComponent createDatePicker(boolean showTime)
	{
		JDateChooser datePicker = new LeakProofDateChooser();
		SimpleDateFormat simpleDateFormat = showTime ? dateTimeFormat : dateFormat;
		datePicker.setDateFormatString(simpleDateFormat.toPattern());
		datePicker.setPreferredSize(new JTextField(showTime ? 13 : 8).getPreferredSize());
		datePicker.setMaximumSize(datePicker.getPreferredSize());
		return datePicker;
	}
		
	public static AbstractAdapter createDatePickerAdapter(JComponent datePicker, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName)
	{
		return new DateChooserAdapter((JDateChooser)datePicker, modelOwner, modelClass, getAspectMethodName, setAspectMethodName);
	}
	
	protected JTextPane createInputLimitedTextPane(int maxCharacters)
	{
		JTextPane textPane = new JTextPane(new MaxLengthStyledDocument(250));
		textPane.setBorder(new JTextField().getBorder());
		return textPane;
	}
	
	protected JTextField createInputLimitedTextField(int maxCharacters)
	{
		JTextField textField = new JTextField(new MaxLengthDocument(maxCharacters), "", maxCharacters);		
		return textField;
	}

	protected JTextField createInputLimitedTextField(int maxCharacters, int displayWidth)
	{
		JTextField textField = new JTextField(new MaxLengthDocument(maxCharacters), "", displayWidth);		
		return textField;
	}
	
	protected JPasswordField createInputLimitedPasswordField(int maxCharacters, int displayWidth)
	{
		JPasswordField textField = new JPasswordField(new MaxLengthDocument(maxCharacters), "", displayWidth);
		return textField;
	}

	
	/**
	 * TODO: Experimental usage of a validation class to check excess of max characters.
	 * For JTextFields this check is done by using a MaxLengthDocument but I have not figured out
	 * how to use this technique with a JFormattedTextField. Better that nothing is to pop up a
	 * warning at least and to prohibit the user from leaving the field. The Save button should also
	 * be disabled and there is a hook for that in the validation package but it's not obvious how
	 * to integrate with the predicate chain that controls the button in crud frame work.
	 * You should never combine predicate control with explicit setting of a components enable
	 * disable state. Instead you must describe the enable disable state with a composition of predicates  
	 */
	private JFormattedTextField createFormattedTextField(final int maxCharacters, boolean grouping)
	{
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(grouping);
		format.setMaximumFractionDigits(0);
		final JFormattedTextField formattedTextField = new JFormattedTextField(format);
		formattedTextField.setColumns(maxCharacters);
		String message = MessageFormat.format(AbstractValidator.BUNDLE.getString("validation.exceededMaxNumberOfCharacters"), maxCharacters);
		formattedTextField.setInputVerifier(new AbstractValidator(this, formattedTextField, message){

			@Override
			protected boolean validationCriteria(JComponent c)
			{
				return formattedTextField.getText().length() <=  maxCharacters;
			}});
		return formattedTextField;		
	}

	/**
	 * Create a field that only accepts integers
	 */
	protected JFormattedTextField createIntegerField(int maxDigits)
	{
		return createFormattedTextField(maxDigits, true);
	}

	
	/**
	 * Create a field that only accepts numbers and without grouping
	 */
	protected JFormattedTextField createIntegerIdField(int maxDigits)
	{
		return createFormattedTextField(maxDigits, false);
	}
	
	protected JButton createHyperLinkButton(Action action)
	{
		final JButton hyperLinkButton = new JButton(action);
		hyperLinkButton.setMargin(new Insets(0,0,0,0));
		hyperLinkButton.setBorder(null);
		hyperLinkButton.setBorderPainted(false);
		hyperLinkButton.setContentAreaFilled(false);
		hyperLinkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setHyperLinkButtonFont(hyperLinkButton, Color.BLUE);
		
		hyperLinkButton.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e)
			{
				if (hyperLinkButton.isEnabled())
					setHyperLinkButtonFont(hyperLinkButton, Color.BLUE);
				else
					setHyperLinkButtonFont(hyperLinkButton, Color.LIGHT_GRAY);
			}});

		return hyperLinkButton;
	}
	
	@SuppressWarnings("unchecked")
	private void setHyperLinkButtonFont(JButton hyperLinkButton, Color color)
	{
		Map map = hyperLinkButton.getFont().getAttributes();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
		map.put(TextAttribute.FOREGROUND, color);
		hyperLinkButton.setFont(new Font(map));		
	}
	
  public class DateRenderer extends DefaultTableCellRenderer
	{
  	public DateRenderer() 
  	{
		}
  	
		public void setValue(Object value)
		{
			setText((value == null) ? "" : dateFormat.format(value));
		}
	}
  
  public class DateTimeRenderer extends DefaultTableCellRenderer
	{
  	public DateTimeRenderer() {

		}
  	
		public void setValue(Object value)
		{
			setText((value == null) ? "" : dateTimeFormat.format(value));
		}
	}
  
  protected class IntegerRenderer extends DefaultTableCellRenderer
	{
  	public IntegerRenderer() 
  	{
  		setHorizontalAlignment(JLabel.RIGHT);
		}
  	
		public void setValue(Object value)
		{
			setText((value == null) ? "" : integerColumnFormat.format(value));
		}
	}
  
	public static void addTitleRow(Container container, String titleText)
	{
		Box titleBox = Box.createHorizontalBox();
		titleBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		JLabel title = new JLabel(titleText);
		TitledBorder titledBorder = new TitledBorder("x");
		title.setForeground(titledBorder.getTitleColor());
		title.setFont(titledBorder.getTitleFont());
		titleBox.add(title);
		titleBox.add(Box.createHorizontalGlue());
		container.add(titleBox);
	}
	
		

	public static void setDateFormat(SimpleDateFormat dateFormat) {
		AbstractPanel.dateFormat = dateFormat;
	}

	public static void setDateTimeFormat(SimpleDateFormat dateTimeFormat) {
		AbstractPanel.dateTimeFormat = dateTimeFormat;
	}

	public static void setDateTimeFormatEditing(SimpleDateFormat dateTimeFormatEditing) {
		AbstractPanel.dateTimeFormatEditing = dateTimeFormatEditing;
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		if (SHOW_PANEL_NAME.value)
		{
			Graphics2D g2d = (Graphics2D)g;
			String panelName = getClass().getName();
			System.out.println(panelName);
			Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(panelName, g2d);
			g2d.translate(20,20);
			Color prevColor = g2d.getColor();
			g2d.setColor(Color.YELLOW);
			g2d.fill(bounds);
			g2d.setColor(Color.BLACK);
			g2d.drawString(panelName, 0, 0);
			g2d.setColor(prevColor);
		}
	}
	
	public void showInformationMessage(ResourceBundle bundle, String key, Object... parameters)
	{
		String message = createMessage(bundle, key, parameters);
		JOptionPane.showMessageDialog(this, message); 
	}

	public void showErrorMessage(ResourceBundle bundle, String key, Object... parameters)
	{
		String message = createMessage(bundle, key, parameters);
		JOptionPane.showMessageDialog(this, message, UIManager.getString("OptionPane.messageDialogTitle"), JOptionPane.ERROR_MESSAGE); 
	}

	public boolean showConfirmationMessage(ResourceBundle bundle, String key, Object... parameters)
	{
		String message = MessageFormat.format(bundle.getString(key), parameters);
		int userAnswer = JOptionPane.showConfirmDialog(this, message, UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
		return userAnswer == JOptionPane.OK_OPTION;
	}

	private String createMessage(ResourceBundle bundle, String key, Object... parameters)
	{
		try
		{
			return MessageFormat.format(bundle.getString(key), parameters);
		}
		catch (MissingResourceException e)
		{
			return "?" + key + "?";
		}
	}	
	
	public static CompoundBorder createShadowBorder()
	{
		Color borderColor = UIManager.getColor("MenuBar.shadow");
		borderColor = borderColor == null ? Color.LIGHT_GRAY : borderColor;
		CompoundBorder shadowBorder = BorderFactory.createCompoundBorder(new DropShadowBorder(), BorderFactory.createLineBorder(borderColor));
		return shadowBorder;
	}

	/**
	 * Abstract superclass to commands that creates a panel
	 * 
	 */
	public abstract static class PanelFactory
	{
		public Component createPanel()
		{
			throw new RuntimeException("You must override one of the createPanel methods");
		}
	}
		
	
	
}
