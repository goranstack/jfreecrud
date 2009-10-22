package se.bluebrim.crud.esox;

import java.io.IOException;
import java.io.ObjectInputStream;

import nu.esox.util.Observable;
import nu.esox.util.ObservableEvent;
import nu.esox.util.ObservableListener;
import nu.esox.util.PersistantObservableListener;
import nu.esox.util.Predicate;

/**
 * Abstract superclass to objects that is stored in a database and
 * there for can be in a state where there is unsaved changes.
 * Listen to it self for detecting changed values. The listener object
 * is serializable to survive the transport from server to client.
 *  
 * @author GStack
 *
 */
public abstract class DirtyPredicateModel extends Observable
{
	private static final long serialVersionUID = 1L;
	
	public static boolean equals(Object o1, Object o2)
	{
		if (o1 == o2)
			return true;
		if (o1 == null)
			return false;
		return o1.equals(o2);
	}

	private Predicate dirtyPredicate = new Predicate(false);

	public DirtyPredicateModel()
	{
		ObservableListener listener = new PersistantObservableListener()
		{
			private static final long serialVersionUID = 1L;

			public void valueChanged(ObservableEvent e)
			{
				if (!isTransient(propertyName(e)))
					setDirty();
			}

			private String propertyName(ObservableEvent e)
			{
				return e.getInfo();
			}
		};
		addObservableListener(listener);
	}
	
	public void setDirty()
	{
		dirtyPredicate.set(true);
	}
			
	public boolean isDirty()
	{
		return dirtyPredicate.isTrue();
	}

	public void cleanDirty()
	{
		dirtyPredicate.set(false);
	}
	
	/**
	 * Override this method if you have a property that fires value change
	 * to update GUI but the property should not enable the save button by
	 * making the model dirty.
	 */
	protected boolean isTransient(String propertyName)
	{
		return false;
	}


	public Predicate getDirtyPredicate()
	{
		return dirtyPredicate;
	}

	/**
	 * The dirty flag is always off after transfer from server to client or vice versa
	 */
  private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException
	{
		stream.defaultReadObject();
		dirtyPredicate.set(false);
	}


	
}
