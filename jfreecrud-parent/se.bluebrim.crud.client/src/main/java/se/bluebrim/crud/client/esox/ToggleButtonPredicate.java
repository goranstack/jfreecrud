package se.bluebrim.crud.client.esox;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nu.esox.util.Observable;
import nu.esox.util.PredicateIF;

/** A simple predicate to wrap a JToggleButton. Handy ... Still get the nagging feeling I'm
 * overcomplicating things by not fully understanding esox and what's already in there, but
 * that's life.
 * 
 * @author ebrink
 */
public class ToggleButtonPredicate extends Observable implements PredicateIF, ChangeListener {
	private JToggleButton button;
	
	public ToggleButtonPredicate(JToggleButton button) {
		this.button = button;
		button.addChangeListener(this);
	}

	public void stateChanged(ChangeEvent ev) {
		fireValueChanged("value", null);
	}

	public boolean isTrue() {
		return button.isSelected();
	}
}
