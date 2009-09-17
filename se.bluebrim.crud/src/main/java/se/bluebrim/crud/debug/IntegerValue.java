package se.bluebrim.crud.debug;

/**
 * A mutable integer value.
 * 
 * @author goran
 *
 */
public class IntegerValue extends MutableValue
{	
	public int value;

	public IntegerValue(int value)
	{
		this.value = value;
	}
	
	public Integer getIntegerValue()
	{
		return new Integer(value);
	}
}
