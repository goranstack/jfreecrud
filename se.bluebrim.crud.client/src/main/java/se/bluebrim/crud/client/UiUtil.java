package se.bluebrim.crud.client;

import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.springframework.core.io.ClassPathResource;

public class UiUtil
{
	public static Icon getIcon(String path, Class clazz)
	{
		Icon icon;
		try
		{
			icon = new ImageIcon(new ClassPathResource(path, clazz).getURL());
		} catch (IOException e)
		{
			throw new RuntimeException("Unable to find icon: \"" + path + "\"");
		}
		return icon;
	}
}
