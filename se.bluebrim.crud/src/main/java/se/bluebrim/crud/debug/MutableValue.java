package se.bluebrim.crud.debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass to value objects. Value objects makes it possible to
 * keep a reference to a boolean, integer etc that change value. To use Boolean
 * and Integer would not be possible because they are immutable and we can't use
 * int or boolean because the are not objects.
 * 
 * @author goran
 */
public abstract class MutableValue
{
	
	private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	
	public interface ChangeListener
	{
		public void changed();
	}
	
  public void addChangeListener(ChangeListener listener) 
  {
  	changeListeners.add(listener); 
  }
  
  public void notifyChangeListeners()
  {
  	for (ChangeListener changeListener : changeListeners)
		{
  		changeListener.changed();
		}
  }
}
