package se.bluebrim.crud.tutorial;

import java.io.Serializable;

import se.bluebrim.crud.AbstractDto;

public class Artist extends AbstractDto implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String biography;
	

	public Artist(int id, String name, String biography)
	{
		super();
		this.id = id;
		this.name = name;
		this.biography = biography;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (!equals(name, this.name))
		{
			this.name = name;
			fireValueChanged("name", this.name);
		}
	}

	public String getBiography()
	{
		return biography;
	}

	public void setBiography(String biography)
	{
		this.biography = biography;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj == null ? false : id == ((Artist) obj).id;
	}
	
	
}
