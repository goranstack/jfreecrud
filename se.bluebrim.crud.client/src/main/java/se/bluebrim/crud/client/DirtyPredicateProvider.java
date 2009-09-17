package se.bluebrim.crud.client;

import nu.esox.util.Predicate;

public interface DirtyPredicateProvider
{

	/**
	 * We can't do this in the constructor because all our components must be in place first.
	 */
	public abstract void initDirtyPredicatePanel();

	public abstract Predicate getDirtyPredicate();

	public abstract void cleanDirty();

	public abstract boolean isDirty();

	/**
	 * When the SaveChangesCommand is triggered by pressing the Save button the focus is transfered
	 * from the on going edited text field to the Save button and the model is updated. But
	 * when the SaveChangesCommand is triggered by closing the window this is not the case. The
	 * window closing listener call this method to achieve the same effect as saving with the save
	 * button.
	 */
	public abstract void updateModelWithOnGoingTextEditing();
	

}