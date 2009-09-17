package se.bluebrim.crud.client.esox;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import nu.esox.gui.aspect.ModelOwnerIF;
import nu.esox.gui.aspect.TextFieldAdapter;

/** An adapter for TextFields which will update the adapted aspect whenever the field's contents
 * change. This is different from the default <code>TextFieldAdapter</code> supplied with esox,
 * since that requires the user to press Return (i.e., to send an Action event to the textfield).
 * <p>
 * With this adapter in place, a user can click the text field, do a quick edit, and then click
 * elsewhere without pressing Return, and not lose the edit.
 * </p>
 * @see TextFieldAdapter
 * @author ebrink
 */
public class TextFieldChangedAdapter extends TextFieldAdapter {
	
	private JTextField textField;
	
  public TextFieldChangedAdapter(JTextField textField, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName)
  {
      this(textField, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, String.class, "", "");
      this.textField = textField;
  }
  
  public TextFieldChangedAdapter(JTextField textField, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName, Class aspectClass)
  {
      this(textField, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, "", "");
  }

  public TextFieldChangedAdapter(final JTextField textField, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName,
  		String setAspectMethodName, Class aspectClass, String nullValue, String undefinedValue)
  {
  	super(textField, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, nullValue, undefinedValue, null);
  	textField.getDocument().addDocumentListener(new DocumentListener() {
  		public void insertUpdate(DocumentEvent arg0) {
  			setAspectValue(textField.getText());
  		}
  		public void changedUpdate(DocumentEvent arg0) {
  			setAspectValue(textField.getText());
  		}
  		public void removeUpdate(DocumentEvent arg0) {
  			setAspectValue(textField.getText());
  		}
  	});
   	this.textField = textField;
 }

  /** Update, while filtering out duplicates. It is <strong>crucial</strong> that we never do set the
   * textfield to the same value it already is holding, since doing so would trigger an infinite number
   * of DocumentEvents, each leading back here for more.
   */
  protected void update(Object projectedValue) {
  	if(textField == null || ((String) projectedValue).equals(textField.getText()))
  		return;
  	super.update(projectedValue);
  }
}
