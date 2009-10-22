package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Can be used to disable a button when there is no selection in a list.
 * Use <code>CompositeItemSelectable</code> when you have more than one <code>ItemSelectable</code> 
 * 
 * @author Göran Stäck 2004-01-04
 *
 */
public class DisableComponentOnEmptySelection implements ItemListener {
	private Component component;
	private ItemSelectable itemSelectable;

	public DisableComponentOnEmptySelection(Component component, ItemSelectable itemSelectable) {
		this.itemSelectable = itemSelectable;
		this.component = component;
		itemSelectable.addItemListener(this);
		enableDisable();
	}

	public void itemStateChanged(ItemEvent e) {
		enableDisable();		
	}
	
	private void enableDisable() {
		component.setEnabled(itemSelectable.getSelectedObjects().length > 0);						
	}
	
	public void release() {
		component.setEnabled(true);
		itemSelectable.removeItemListener(this);
	}

}