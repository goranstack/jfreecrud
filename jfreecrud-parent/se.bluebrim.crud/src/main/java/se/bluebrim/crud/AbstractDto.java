package se.bluebrim.crud;

import se.bluebrim.crud.esox.DirtyPredicateModel;


/**
 * Abstract class for all DTOs (Data Transfer Objects) in the system 
 * @author OPalsson
 *
 */
public class AbstractDto extends DirtyPredicateModel
{

	@Override
	public boolean equals(Object obj)
	{
		throw new UnsupportedOperationException("You probably don't want to do identity equals in a distributed environment. "
				+ (obj != null ? obj.getClass() : ""));
	}

}
