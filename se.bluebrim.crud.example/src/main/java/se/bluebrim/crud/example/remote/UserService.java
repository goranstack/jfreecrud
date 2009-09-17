package se.bluebrim.crud.example.remote;

import java.util.List;

import se.bluebrim.crud.example.model.User;

public interface UserService
{
	public List<User> getUsers();
	public int addUser(User user);
	public void changeUser(User user);
	public void removeUser(int userId);
 
}
