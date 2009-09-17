package se.bluebrim.crud.client.esox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nu.esox.gui.aspect.ModelOwnerIF;
import nu.esox.util.ObservableIF;

/**
 * Used to bind sub panels that share model instance with the parent panel.
 * 
 * @author GStack
 * @deprecated since the same thing can be achieved with SubModelAdaptor
 */
public class SubPanelAdapter implements ModelOwnerIF.Listener
{
	private Object subPanel;
	private Method subPanelSetModelMethod;
	
	public SubPanelAdapter(Object subPanel, String subPanelSetModelMethodName, Class modelClass, ModelOwnerIF parentPanel) 
	{
		parentPanel.addListener(this);
		this.subPanel = subPanel;
		try
		{
			subPanelSetModelMethod = subPanel.getClass().getMethod(subPanelSetModelMethodName, new Class[]{modelClass});
		} catch (SecurityException e)
		{
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void modelAssigned(ObservableIF oldModel, ObservableIF newModel)
	{
		try
		{
			subPanelSetModelMethod.invoke(subPanel, new Object[]{newModel});
		} catch (IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		} catch (InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}	
	}
	
	

}
