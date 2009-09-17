package se.bluebrim.crud.client;

import com.toedter.calendar.JDateChooser;

/**
 * 
 * Workaround that removes another workaround in the superclass since that workaround
 * causes a memory leak. The misbehavior that the workaround in JDateCooser tries
 * to fix do not appear any more what I can see. Maybe fixed
 * by changes in the JVM. See {@link JDateChooser#cleanup()}. Ugly
 * but after trying two other date pickers that also had problems this will have
 * to do. <br>
 * <code>com.michaelbaranov.microba.calendar.DatePicker</code> is also leaking
 * and <code>org.jdesktop.swingx.JXDatePicker</code> do not handle time only
 * date. <br>
 * The others I found are commercial and did'nt look good enough to spend money
 * for. <br>
 * See: Swing Depot: Component Suites,
 * http://www.javadesktop.org/rollups/components/index.html
 * 
 * @author GStack
 * 
 */
public class LeakProofDateChooser extends JDateChooser
{

	public LeakProofDateChooser()
	{
		super();
		cleanup();
	}

}
