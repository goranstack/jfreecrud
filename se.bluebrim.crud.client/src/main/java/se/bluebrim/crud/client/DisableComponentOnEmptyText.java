package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

/**
 * Can be used to disable a button when a text is empty.
 * 
 * @author Göran Stäck 2004-01-04
 *
 */
public class DisableComponentOnEmptyText implements TextListener {
	private Component component;
	private TextComponent textComponent;

	public DisableComponentOnEmptyText(Component component, TextComponent textComponent) {
		this.textComponent = textComponent;
		this.component = component;
		textComponent.addTextListener(this);
		enableDisable();
	}

	public void textValueChanged(TextEvent e) {
		enableDisable();		
	}
	
	private void enableDisable() {
		component.setEnabled(textComponent.getText().length() > 0);						
	}
	
	public void release() {
		component.setEnabled(true);
		textComponent.removeTextListener(this);
	}
}