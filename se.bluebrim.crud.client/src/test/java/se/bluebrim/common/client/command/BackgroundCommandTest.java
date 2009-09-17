package se.bluebrim.common.client.command;

import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import nu.esox.gui.layout.ColumnLayout;
import se.bluebrim.crud.client.AbstractPanel;
import se.bluebrim.crud.client.AbstractPanelTest;
import se.bluebrim.crud.client.command.AccumulativeCursorHandler;
import se.bluebrim.crud.client.command.AccumulativeProgressVisualizer;
import se.bluebrim.crud.client.command.BackgroundCommand;
import se.bluebrim.crud.client.command.DefaultAction;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.client.command.DefaultCommand;
import se.bluebrim.crud.client.command.DefaultExceptionHandler;
import se.bluebrim.crud.client.command.ProgressVisualizer;
import se.bluebrim.crud.client.command.WaitCursorFrame;

/**
 * Test of fault barrier, wait cursor and progress animation. Opens a window
 * with buttons for long running tasks and a progress bar. There are buttons
 * that execute tasks that throws exception in different stages of the task.
 * 
 * @author GStack
 * 
 */
public class BackgroundCommandTest extends AbstractPanelTest implements ProgressVisualizer
{
	private enum ExceptionStage{NONE, PREPARE, BACKGROUND, PUBLISH};

	public static void main(String[] args)
	{
		new BackgroundCommandTest();
	}
	
	private JProgressBar progressBar;
	private JLabel resultLabel;
	private DefaultBackgroundCommand lastBackgroundCommand;

	
	public BackgroundCommandTest()
	{
		AbstractPanel panel = new AbstractPanel(new ColumnLayout(20)){};
		panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
		progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    resultLabel = new JLabel();
		panel.add(progressBar);
		Action action = new BackgroundAction("No exception", ExceptionStage.NONE);
		panel.add(new JButton(action));
		action = new BackgroundAction("Throws exception in prepare", ExceptionStage.PREPARE);
		panel.add(new JButton(action));
		action = new BackgroundAction("Throws exception in background", ExceptionStage.BACKGROUND);
		panel.add(new JButton(action));
		action = new BackgroundAction("Throws exception in publish", ExceptionStage.PUBLISH);
		panel.add(new JButton(action));
		panel.add(new JButton(new CancelAction()));

		panel.add(resultLabel);
		WaitCursorFrame mainFrame = openInInternalFrame("BackgroundCommandTest", panel, new Point(120, 120));
		AccumulativeCursorHandler accumulativeCursorHandler = new AccumulativeCursorHandler(mainFrame);
		DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler(mainFrame);
		DefaultAction.setDefaultCursorHandler(accumulativeCursorHandler);
		DefaultAction.setDefaultExceptionHandler(defaultExceptionHandler);
		DefaultCommand.setDefaultProgressVisualizer(new AccumulativeProgressVisualizer(this));
		DefaultBackgroundCommand.setDefaultCursorHandler(accumulativeCursorHandler);
		DefaultBackgroundCommand.setDefaultExceptionHandler(defaultExceptionHandler);
	}
	
	public void testIsCancelledState() throws Exception
	{
		final CountDownLatch latch = new CountDownLatch(1);		
		final DefaultBackgroundCommand command = new MyBackgroundCommand(ExceptionStage.PREPARE);
		command.run(new BackgroundCommand.DoneListener(){

			@Override
			public void done()
			{
				latch.countDown();
				assertTrue(command.isCancelled());
				
			}});
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}
	
	@Override
	public void startProgressAnimation()
	{
		progressBar.setIndeterminate(true);		
	}

	@Override
	public void stopProgressAnimation()
	{
		progressBar.setIndeterminate(false);				
	}

	
	private class MyBackgroundCommand extends DefaultBackgroundCommand
	{
		private Random random = new Random();
		private ExceptionStage exceptionStage = ExceptionStage.NONE;
		private final DateFormat lapseFormat = new SimpleDateFormat("mm:ss:SSS");
		private Date startTime;
		
		/**
		 * 
		 * @param exceptionStage specifies in what stage the command should throw an exception.
		 */
		public MyBackgroundCommand(ExceptionStage exceptionStage)
		{
			super();
			this.exceptionStage = exceptionStage;
		}

		@Override
		protected void prepare()
		{
			if (exceptionStage == ExceptionStage.PREPARE)
				throw new RuntimeException("Something went wrong in the prepare method");
			startTime = new Date();
			try
			{
				Thread.sleep(500);				// Verify that the wait cursor is visible
			} catch (InterruptedException ignore){}
			
			resultLabel.setText("Waiting for result...");
		}
		
		@Override
		protected Object runInBackground()
		{
			try
			{
				int sleepTime = 4000 + Math.round(random.nextFloat() * 1000);
				Thread.sleep(sleepTime);
			} catch (InterruptedException ignore)
			{
				System.out.println("The background thread was interrupted");
			}
			if (exceptionStage == ExceptionStage.BACKGROUND)
				throw new RuntimeException("Something went wrong in the runInBackground method");
			String result = "The background task finnished at: " + new Date();
			return result;
		}
		
		@Override
		protected void publishResult(Object result)
		{
			try
			{
				Thread.sleep(300);				// Verify that the wait cursor is visible
			} catch (InterruptedException ignore){}
			if (exceptionStage == ExceptionStage.PUBLISH)
				throw new RuntimeException("Something went wrong in the publishResult method");

			Date endTime = new Date();
			Date duration = new Date(endTime.getTime() - startTime.getTime());
			String durationText = " Duration: " + lapseFormat.format(duration);
			System.out.println((String)result + durationText);
			resultLabel.setText((String)result + durationText);
		}
		
		@Override
		protected void canceled()
		{
			Date endTime = new Date();
			Date duration = new Date(endTime.getTime() - startTime.getTime());
			resultLabel.setText("The command was cancelled after: " + lapseFormat.format(duration));
//			throw new RuntimeException("Something went wrong in the canceled method");
		}
		
		@Override
		protected void handleException(Exception e)
		{
			super.handleException(e);
			resultLabel.setText(e.getMessage());
//			throw new RuntimeException("Something went wrong in the interrupted method");
		}
		
	}
	
	private class BackgroundAction extends DefaultAction
	{
		private ExceptionStage exceptionStage = ExceptionStage.NONE;

		public BackgroundAction(String name, ExceptionStage exceptionStage)
		{
			super();
			this.exceptionStage = exceptionStage;
			setActionName(name);
		}
						
		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			lastBackgroundCommand = new MyBackgroundCommand(exceptionStage);
			lastBackgroundCommand.run();
		}			
	}
	
	private class CancelAction extends DefaultAction
	{
		public CancelAction()
		{
			setActionName("Cancel last");
		}
		
		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			lastBackgroundCommand.cancel();
		}
	}
			
}

