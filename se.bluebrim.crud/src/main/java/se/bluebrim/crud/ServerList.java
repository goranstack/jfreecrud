package se.bluebrim.crud;

import java.util.Collection;
import java.util.List;

import nu.esox.util.ObservableList;

/**
 * Abstract superclass to objects that represents a collection
 * that is loaded from server. When loaded the collection can be
 * filtered through a filter that is supplied in the constructor or by a setter. <br>
 * The loading and the following updating of the observable list is
 * divided to enable server access from a Swing Worker thread followed
 * by an update in a AWT thread.
 * 
 * @author GStack
 *
 */
public abstract class ServerList extends ObservableList
{
	
	private boolean dirty = false;

	/**
	 * Can be called from a SwingWorker background thread since no Swing access
	 * is performed in this method.
	 */
	public void readFromServer()
	{
		dirty = true;
	}

	protected abstract List<?> getList();
	
	/**
	 * This method must only be called from the AWT thread. When using a Swing Worker
	 * thread call the readFromServer method in the background thread and then
	 * the update method from the AWT thread.
	 */
	public void reloadFromServer()
	{
		readFromServer();
		update();
	}

	/**
	 * This method is not thread safe since the endTransaction call will
	 * notify listeners that probably are swing components.
	 */
	public void update()
	{
		if (dirty)
		{
			beginTransaction();
			clear();
			List<?> list = getList();
			if (list != null)
				addAll(list);
			endTransaction("reload elements from server", null);
			dirty = false;
		}
	}
	
	@Override
	/**
	 * The fireValueChanged for each added element in the superclass cost
	 * to much CPU.
	 */
	public boolean addAll(Collection c)
	{
		boolean b = m_collection.addAll(c);
		if (b)
		{
			for (Object o : c)
				listenTo(o);
		}
		return b;
	}

}
