package se.bluebrim.crud.tutorial;

import se.bluebrim.crud.ServiceLocatorTemplate;
import se.bluebrim.crud.tutorial.remote.ArtistDao;

public class ServiceLocator extends ServiceLocatorTemplate
{	                 
	public static ArtistDao getArtistDao()
	{
		return (ArtistDao)ctx.getBean("artistDao");
	}
}
