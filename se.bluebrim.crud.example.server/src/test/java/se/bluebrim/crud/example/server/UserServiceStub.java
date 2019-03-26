package se.bluebrim.crud.example.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import se.bluebrim.crud.example.model.User;
import se.bluebrim.crud.example.remote.UserService;

/**
 * Fake user DAO for testing.
 * 
 * @author GStack
 *
 */
public class UserServiceStub implements UserService
{
	private List<User> users;
	private int lastUsedId;
	
	public UserServiceStub()
	{
		users = new ArrayList<User>();
		User user = new User();
		user.setUsername("Test user 1");
		addUser(user);
	}
	
	@Override
	public int addUser(User user)
	{
		user.setId(lastUsedId++);
		users.add(user);
		return user.getId();
	}

	@Override
	public void changeUser(User user)
	{
		User userToUpdate = getUser(user.getId());
		if (userToUpdate == null)
			throw new RuntimeException("The user is removed");
		
		// Mimic a database update by replacing the user in the list
		removeUser(user.getId());
		users.add(user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsers()
	{
		try
		{
			Thread.sleep(3000);			// Give us a change to see the loading in progress animation
		} catch (InterruptedException ignore){}
		return (List<User>) copyUsingSerialization(users);
	}

	@Override
	public void removeUser(int userId)
	{
		User userToRemove = getUser(userId);
		if (userToRemove == null)
			throw new RuntimeException("The user is already removed");
		users.remove(userToRemove);
	}
	
	private User getUser(int id)
	{
		for (User user : users)
		{
			if (user.getId() == id)
				return user;
		}
		return null;
	}
	
	/**
	 * Used to mimic the client server environment where object
	 * are copied when transferred from server to client. It's important
	 * for example to verifying that the equals method in the model objects are
	 * based an a database id.
	 */
	protected Object copyUsingSerialization(Object original)
	{
		Object copy;
		try
		{
			//	Serialize to a byte array
			    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			    ObjectOutputStream out = new ObjectOutputStream(bos) ;
			    out.writeObject(original);
			    out.close();
					
			// 	Deserialize from a byte array
			    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			    copy =  in.readObject();
			    in.close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}	
		return copy; 
		
	}


}
