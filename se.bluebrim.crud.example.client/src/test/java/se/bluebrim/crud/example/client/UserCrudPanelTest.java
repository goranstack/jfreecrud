package se.bluebrim.crud.example.client;

import java.util.List;

import org.junit.Test;

import se.bluebrim.crud.client.AbstractPanel;
import se.bluebrim.crud.client.AbstractPanelTest;
import se.bluebrim.crud.example.common.ServiceLocator;

public class UserCrudPanelTest extends AbstractPanelTest
{
	public UserCrudPanelTest()
	{
		ServiceLocator.init("config-client-stubs.xml");
	}

	public static void main(String[] args)
	{
		new UserCrudPanelTest().testCreateGui();
	}
	
	@Test
	public void testCreateGui()
	{
		List<AbstractPanel> panels = openTestWindow(UserCrudPanel.class);
	}


	
}
