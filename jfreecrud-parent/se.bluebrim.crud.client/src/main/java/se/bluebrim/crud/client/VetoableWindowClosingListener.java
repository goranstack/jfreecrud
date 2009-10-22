package se.bluebrim.crud.client;

/**
 * Implemented by window content that has an interest in window closing and
 * also sometimes reject window closing.
 * The interface is designed to handle background tasks that should be performed
 * before window closes. The DoneListener callback interface makes it possible to
 * stall the window closing until the background process is finished.
 *  
 * @author GStack
 *
 */
public interface VetoableWindowClosingListener
{
	public interface DoneListener
	{
		public void done(boolean acceptClosing);
	}
	
	/**
	 * Implementing objects perform the things necessary before window closing
	 * and calls the doneListener with a boolean argument specifying if closing
	 * is accepted or rejected
	 */
	public void internalFrameClosing(DoneListener doneListener);
}
