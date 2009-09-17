package se.bluebrim.crud;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Super class to ServiceLocator's used in both client and server to look up services (DAO's).
 * 
 * @author OPalsson
 *
 */
public class ServiceLocatorTemplate {

	protected static Logger logger = Logger.getLogger(ServiceLocatorTemplate.class);
	protected static GenericApplicationContext ctx;
	
	/**
	 * Initialize the ServiceLocator
	 * @param fileName name of the configuration file that can be found on the class path
	 */
	public static ApplicationContext init(String filename) {
		Resource[] overridingPropertyFiles = findOverridingPropertyFiles(filename);
		return init(filename, overridingPropertyFiles);
	}

	/**
	 * Initialize the ServiceLocator
	 * @param fileName name of the configuration file that can be found on the class path
	 * @param overridingPropertyFiles property files that overriding wild cards in the configuration file. Should be on the class path 
	 */
	public static ApplicationContext init(String filename, Resource[] overridingPropertyFiles) {
		logger.info("Initialize ServiceLocator with file " + filename);

		XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource(filename));
	
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setLocations(overridingPropertyFiles);
		ppc.postProcessBeanFactory(beanFactory);		

		ctx = new GenericApplicationContext(beanFactory);
		ctx.refresh();
		return ctx;
	}
	
	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
	
	private static Resource[] findOverridingPropertyFiles(String filename) {
		String baseFilename = filename.substring(0, filename.length() - ".xml".length());
		String username =System.getProperty("user.name");
		
		ArrayList<Resource> files = new ArrayList<Resource>();
		addPropertyFile(baseFilename + ".properties", files);
		addPropertyFile(baseFilename + ".host.properties", files);
		addPropertyFile(baseFilename + "." + username + ".properties", files);
		return files.toArray(new Resource[files.size()]);
	}

	private static void addPropertyFile(String filename, ArrayList<Resource> files)
	{
		Resource propertyFile = new ClassPathResource(filename);
		if (propertyFile.exists())
			files.add(propertyFile);
	}
}
