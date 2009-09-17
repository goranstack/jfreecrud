package se.bluebrim.crud.client.command;


/**
 * Interface implemented by objects that has a component that is
 * capable of visualizing indeterminate progress for example a progress bar or
 * a spinning wheel.
 * 
 * @author GStack
 *
 */
public interface ProgressVisualizer
{
	public void startProgressAnimation();
	public void stopProgressAnimation();
}
