package se.bluebrim.crud.example.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import se.bluebrim.crud.example.common.ServiceLocator;

/**
 * The server part of the Crud Example application.
 * 
 * @author GStack
 *
 */
public class CrudExampleServer
{
	private final static Logger logger = Logger.getLogger(CrudExampleServer.class.getName());

	public static void main(String[] args)
	{
		String loggingConfigFilname = CrudExampleServer.class.getClassLoader().getResource("log4j.properties").getFile();
		PropertyConfigurator.configureAndWatch(loggingConfigFilname);

		// Initialize Spring framework via the ServiceLocator
		ServiceLocator.init("server.xml");
		checkDatabaseConnection();
		logger.info("Server started");
	}
	
	private static void checkDatabaseConnection()
	{
		int tryCount = 0;
		boolean connected = false;
		while (!connected && (tryCount++ < 10))
		{
			try
			{
				// Just call any method that generates a database query
				ServiceLocator.getUserService().getUsers();
				connected = true;
			}
			catch (Exception e)
			{
				logger.warn("Exception when testing database connection at startup. Will wait a while and try again", e);
				try {Thread.sleep(15000);}catch (InterruptedException ignore){}
			}
		}
		if (!connected)
			throw new RuntimeException("Cannot connect to database");
	}


}
