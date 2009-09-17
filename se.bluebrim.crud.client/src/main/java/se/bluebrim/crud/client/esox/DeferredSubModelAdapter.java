package se.bluebrim.crud.client.esox;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import nu.esox.gui.aspect.ModelOwnerIF;
import nu.esox.gui.aspect.SubModelAdapter;

/**
 * A DeferredSubModelAdapter is used to defer the update of a tab component until the the tab is shown.
 * Some master detail ui's has a detail panel consisting of several tabs. Some tabs shows information
 * that requires additional database access that is unnecessary to run before the tab is actually showing. <br>
 * Assumes that the subModelTarget is the tab component.
 *  
 * @author GStack
 *
 */
public class DeferredSubModelAdapter extends SubModelAdapter
{
	private Object deferredValue;
	private Component tabComponent;
		
	public DeferredSubModelAdapter(Object subModelTarget, String setSubModelMethodName, Class subModelClass, ModelOwnerIF modelOwner)
	{
    this( subModelTarget, setSubModelMethodName, subModelClass, modelOwner, null, null );
	}

	public DeferredSubModelAdapter(Object subModelTarget, String setSubModelMethodName, Class subModelClass, ModelOwnerIF modelOwner, Class modelClass,
			String getAspectMethodName)
	{
		super(subModelTarget, setSubModelMethodName, subModelClass, modelOwner, modelClass, getAspectMethodName, null);
		this.tabComponent = (Component)subModelTarget;
		tabComponent.addComponentListener(new ComponentAdapter(){
			
			@Override
			public void componentShown(ComponentEvent e)
			{
				DeferredSubModelAdapter.super.update(deferredValue);
			}
		});
	}

	
	@Override
	/**
	 * Since this method is called from super class constructor we must handle null in tabComponent
	 */
	protected void update(Object projectedValue)
	{
		deferredValue = projectedValue;
		if (tabComponent != null && tabComponent.isVisible())
			super.update(projectedValue);
	}
}
