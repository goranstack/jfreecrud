package se.bluebrim.crud.client.esox;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import nu.esox.util.Observable;
import nu.esox.util.PredicateIF;

/** A simple predicate that lets things depend on the number of rows (called just the "size") of a JTable.
 * The logic behind the predicate is simple: let <i>n</i> be the number of rows in the table, and <i>m</i>
 * and <i>M</i> define the minimum and maximum values. The predicate will then be true if and only if the
 * following equation holds true:
 * <pre><i>n</i> &gt;= <i>m</i> and <i>n</i> &lt; <i>M</i></pre>
 * In the API, <i>m</i> is called the minimum value, while <i>M</i> is called the maximum. The two form an
 * open-ended interval.
 * </p>
 * <p>
 * To use the predicate to control the enabled-state of an action that adds a row to the table, simply
 * use the TableSizePredicate(JTable, int) constructor, passsing it the maximum number of rows that the
 * table should allow.
 * </p>
 * 
 * @see javax.swing.JTable
 * 
 * @author ebrink
 */
public class TableSizePredicate extends Observable implements TableModelListener, PredicateIF {
	protected JTable table;
	protected int minSize, maxSize;
	protected boolean value;

	/** Creates a new predicate, with an interval of [0, <i>maxSize</i>). */
	public TableSizePredicate(JTable table, int maxSize) {
		this(table, 0, maxSize);
	}

	/** Creates a new predicate, with an interval of [<i>minSize</i>, <i>maxSize</i>). */
	public TableSizePredicate(JTable table, int minSize, int maxSize) {
		this.table = table;
		this.minSize = minSize;
		this.maxSize = maxSize;
		table.getModel().addTableModelListener(this);
	}

	public void tableChanged(TableModelEvent ev) {
		int sign = 0;
		switch(ev.getType()) {
		case TableModelEvent.INSERT: sign = 1; break;
		case TableModelEvent.DELETE: sign = -1; break;
		case TableModelEvent.UPDATE: sign = 0; break;
		}
		int n = table.getRowCount() + sign * (ev.getLastRow() - ev.getFirstRow() + 1);
		value = n >= minSize && n < maxSize; 
		fireValueChanged("tablesize", new Integer(n));
	}

	/** Returns <code>true</code> iff the length of the watched list is inside the allowed range,
	 * <code>false</code> otherwise.
	 */
	public boolean isTrue() {
		return value;
	}
}
