package se.bluebrim.crud.example.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import se.bluebrim.crud.example.model.User;
import se.bluebrim.crud.example.remote.UserService;
import se.bluebrim.crud.server.AbstractJdbcDao;


public class UserServiceImpl extends AbstractJdbcDao implements UserService
{
	
	private ParameterizedRowMapper<User> userMapper = new ParameterizedRowMapper<User>() 
	{
		@Override
		@Transactional(rollbackFor=Exception.class)
		public User mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			User user = new User(
				      rs.getInt("Id"),
				      rs.getString("UserName"),
				      rs.getString("Password"),
				      rs.getString("Name"),
				      rs.getString("Email"),
				      rs.getString("Phone"),
				      rs.getString("Mobile"),
				      User.Role.values()[rs.getInt("Role")]			      
				      );
			return user;
		}
	};

	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<User> getUsers()
	{
		String sql = "SELECT * FROM IP_User";		
		return jdbcTemplate.query(sql, userMapper);		
	}
		
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int addUser(User user)
	{
		int userId = 0;
		String sql = "INSERT INTO IP_User" + 
		" (UserName, Password, Name, Email, Phone, Mobile, Role)" + 
		"  VALUES(?, ?, ?, ?, ?, ?, ?) ";		
		jdbcTemplate.update(sql, user.getUsername(),
										user.getPassword(),
										user.getName(),
										user.getEmail(),
										user.getPhone(),
										user.getMobile(),
										user.getRole().ordinal()
										);
		//Now get the maximum value of the Id
		sql = "SELECT MAX(Id) FROM IP_User";
		userId =  jdbcTemplate.queryForInt(sql);
		return userId;
	}
		
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void changeUser(User user)
	{
		String sql = "UPDATE IP_User " +
		             " SET UserName = ?, Password = ?, Name = ?, Email = ?, Phone = ?, Mobile = ?, Role = ? " +
		             " WHERE  Id = ?  ";	
		jdbcTemplate.update(sql, user.getUsername(),
				user.getPassword(),
				user.getName(),
				user.getEmail(),
				user.getPhone(),
				user.getMobile(),
				user.getRole().ordinal(),
				user.getId()
				);		
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void removeUser(int userId)
	{
		String sql = "DELETE FROM IP_User WHERE  Id = ?";
		jdbcTemplate.update(sql, userId);
	}

	@Override
	protected int getMaxNumberOfRowsFromDb()
	{
		return 20000;
	}
}
