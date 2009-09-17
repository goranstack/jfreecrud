package se.bluebrim.crud.client.command;

/**
 * Wrapper that accumulates start and stops to handle simultaneous
 * processes whose progress should be visualized with a single shared
 * progress visualizer.
 * 
 * @author GStack
 *
 */
public class AccumulativeProgressVisualizer implements ProgressVisualizer
{
	private ProgressVisualizer target;
	private int count = 0;
		
	public AccumulativeProgressVisualizer(ProgressVisualizer target)
	{
		super();
		this.target = target;
	}

	@Override
	public synchronized void startProgressAnimation()
	{
		if (count < 1)
			target.startProgressAnimation();
		count++;
	}

	@Override
	public synchronized void stopProgressAnimation()
	{
		count--;
		if (count == 0)
			target.stopProgressAnimation();
		else
			if (count < 0)
			{
//				throw new RuntimeException("Unbalanced calls to ProgressVisualizer");
//	Until deprecated ActionExecuter is in use
				System.out.println("Unbalanced calls to ProgressVisualizer");
				target.stopProgressAnimation();
				count = 0;
			}

	}

}
