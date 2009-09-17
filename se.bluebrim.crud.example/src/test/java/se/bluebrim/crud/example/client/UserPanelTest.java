package se.bluebrim.crud.example.client;

import java.util.List;

import org.junit.Test;

import se.bluebrim.crud.client.AbstractPanel;
import se.bluebrim.crud.client.AbstractPanelTest;
import se.bluebrim.crud.example.common.ServiceLocator;
import se.bluebrim.crud.example.model.User;

public class UserPanelTest extends AbstractPanelTest
{
	public UserPanelTest()
	{
		ServiceLocator.init("config-client-stubs.xml");
	}

	public static void main(String[] args)
	{
		new UserPanelTest().testCreateGui();
	}
	
	@Test
	public void testCreateGui()
	{
		List<AbstractPanel> panels = openTestWindow(UserPanel.class);
		User user = createUser();
		((UserPanel)panels.get(0)).setModel(user);
		((UserPanel)panels.get(1)).setModel(user);		
	}


	private User createUser()
	{
		User user = new User();
		return user;
	}

	
}
