package se.bluebrim.crud;

import java.io.Serializable;

/**
 * Represents an unlocalized error message including possible parameters
 * to be included by using MessageFormat when localizing the message.
 * 
 * @author GStack
 *
 */
public class ValidationMessage implements Serializable
{
	public enum Type {ERROR, WARNING};
	private static final long serialVersionUID = 1L;

	private Type type;
	private String key;
	private Object[] parameters;
	
	public ValidationMessage(String key) 
	{
		this(key, Type.ERROR);
	}

	public ValidationMessage(String key, Type type) 
	{
		this (key, type, new Object[]{});
	}

	public ValidationMessage(String key, Object... parameters) 
	{
		this (key, Type.ERROR, parameters);
	}

	public ValidationMessage(String key, Type type, Object... parameters) 
	{
		super();
		this.key = key;
		this.type = type;
		this.parameters = parameters;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Object[] getParameters()
	{
		return parameters;
	}

	public void setParameters(Object[] parameters)
	{
		this.parameters = parameters;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
}
