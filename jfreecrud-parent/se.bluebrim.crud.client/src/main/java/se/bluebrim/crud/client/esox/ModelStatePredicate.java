package se.bluebrim.crud.client.esox;

import nu.esox.gui.aspect.ModelOwnerIF;
import nu.esox.util.ObservableEvent;
import nu.esox.util.ObservableIF;
import nu.esox.util.ObservableListener;
import nu.esox.util.Predicate;

/**
 * Abstract superclass to Predicate's that reflects a state in the model of
 * a model owner.
 * 
 * @author GStack
 *
 */
public abstract class ModelStatePredicate extends Predicate implements ObservableListener, ModelOwnerIF.Listener
{
	protected ModelStatePredicate(ModelOwnerIF modelOwner)
	{
		modelOwner.addListener(this);
	}
	
	public void modelAssigned(ObservableIF oldModel, ObservableIF newModel)
	{
		if (oldModel != null)
			oldModel.removeObservableListener(this);
		if (newModel != null)
			newModel.addObservableListener(this);
		updatePredicateState();
	};

	
	@Override
	public void valueChanged(ObservableEvent ev)
	{
		updatePredicateState();
	}
	
	protected abstract void updatePredicateState();
	
}