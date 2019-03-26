package se.bluebrim.crud.client.command;

/**
 * Wrapper that accumulates sets and resets to handle simultaneous
 * processes whose progress should be visualized with a single shared
 * cursor handler.
 * 
 * @author GStack
 *
 */
public class AccumulativeCursorHandler implements CursorHandler
{
	private CursorHandler target;
	private int count = 0;
		
	public AccumulativeCursorHandler(CursorHandler target)
	{
		super();
		this.target = target;
	}

	@Override
	public synchronized void setWaitCursor()
	{
		if (count < 1)
			target.setWaitCursor();
		count++;
	}

	@Override
	public synchronized void resetWaitCursor()
	{
		count--;
		if (count == 0)
			target.resetWaitCursor();
		else
			if (count < 0)
				throw new RuntimeException("Unbalanced calls to CursorHandler");

	}

}
