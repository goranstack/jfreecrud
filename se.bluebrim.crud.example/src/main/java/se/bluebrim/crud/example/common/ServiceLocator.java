package se.bluebrim.crud.example.common;

import se.bluebrim.crud.ServiceLocatorTemplate;
import se.bluebrim.crud.example.remote.UserService;

/**
 * ServiceLocator to use in both client and server to look up services (DAO's)
 * @author OPalsson
 *
 */
public class ServiceLocator extends ServiceLocatorTemplate
{	                 
	public static UserService getUserService(){
		return (UserService)ctx.getBean("userService");
	}
}
