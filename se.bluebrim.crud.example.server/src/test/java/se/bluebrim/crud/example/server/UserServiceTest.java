package se.bluebrim.crud.example.server;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import se.bluebrim.crud.example.common.ServiceLocator;
import se.bluebrim.crud.example.model.User;

public class UserServiceTest
{
	User user;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Resource[] overridingFiles = {new ClassPathResource("application-context.properties")};
		ServiceLocator.init("application-context.xml", overridingFiles);
	}

	@After
	public void tearDown() throws Exception
	{
		if (user != null)
			ServiceLocator.getUserService().removeUser(user.getId());
	}

	@Test
	public void testAdd() throws Exception
	{
		user = new User(-1, "Donald", "1234", "Donald Duck", "", "", "", User.Role.Administrator);
		int id = ServiceLocator.getUserService().addUser(user);
		user.setId(id);
		assertTrue(user.getId() > -1);
	}

}
