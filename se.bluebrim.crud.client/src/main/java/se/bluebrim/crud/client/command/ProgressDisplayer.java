package se.bluebrim.crud.client.command;

import javax.swing.JOptionPane;


/** This interface is implemented by classes that do some kind of progress reporting
 * for an action, when such an action needs to be able to pause/resume the reporting.
 * 
 * <p>
 * Reporting is paused when necessary with a call to the <code>pause()</code> method,
 * and then resumed later with a call to <code>resume()</code>.
 * 
 * <p>
 * The principal implementor is ActionExecuter, and clients are various actions.
 * 
 * @author ebrink (2007-03-23)
 * @see ActionExecuter
 *
 */
public interface ProgressDisplayer {
	/** Pause the display of progress. This is mainly useful when opening up a modal dialog,
	 * since it's confusing to have a progress animation going on behind it.
	 * <p>The following code illustrates the intended usage pattern for the pause/resume
	 * functionality:
	 * <pre>
	 * public void actionPerformed(ActionEvent e, ProgressDisplayer p) {
	 *  p.pause();
	 *  // Ask if user really wants to do the processing.
	 *  int response = JOptionPane.showConfirmDialog(...);
	 *  p.resume();
	 *  if(response == JOptionPane.YES_OPTION) {
	 *   ... lengthy processing ...
	 *  }
	 * }
	 * </pre>
	 * 
	 * @see JOptionPane
	 */
	public void pause();

	/** Resume the display of progress. Typically used after closing an initial modal dialog,
	 * before starting to the some (possibly lengthy) actual processing.
	 * @see #pause
	 */
	public void resume();
};
