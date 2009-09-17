package se.bluebrim.crud.client;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nu.esox.gui.layout.RowLayout;
import se.bluebrim.crud.client.command.ProgressVisualizer;
import se.bluebrim.crud.client.progress.BusyIconLabel;
import se.bluebrim.crud.client.swing.CenterLayout;

/**
 * Used to indicate on going loading of table data
 * 
 * @author GStack
 *
 */
public class LoadingInProgressPanel extends BlockingPanel implements ProgressVisualizer
{
	private static final ResourceBundle clientBundle = ResourceBundle.getBundle(AbstractCrudPanel.class.getPackage().getName() + ".crud");

	private BusyIconLabel spinningWheel;
	private JPanel loadingInProgress;
	
	public LoadingInProgressPanel()
	{
		super();
		spinningWheel = new BusyIconLabel();
		loadingInProgress = new JPanel(new RowLayout(10));
		loadingInProgress.setBorder(BorderFactory.createCompoundBorder(AbstractPanel.createShadowBorder(), BorderFactory.createEmptyBorder(16, 10, 16, 10)));
		loadingInProgress.add(spinningWheel);
		loadingInProgress.add(new JLabel(clientBundle.getString("crud.loading")));
		setLayout(new CenterLayout());
	}

	@Override
	public void startProgressAnimation()
	{
		add(loadingInProgress);
		spinningWheel.startProgressAnimation();
		
	}

	@Override
	public void stopProgressAnimation()
	{
		spinningWheel.stopProgressAnimation();
		remove(loadingInProgress);	
	}
	
	
}
