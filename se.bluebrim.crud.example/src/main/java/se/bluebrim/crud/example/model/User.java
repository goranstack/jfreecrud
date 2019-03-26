package se.bluebrim.crud.example.model;

import se.bluebrim.crud.AbstractDto;

public class User extends AbstractDto
{
	private static final long serialVersionUID = 1L;

	public enum Role
	{
		Administrator, User
	};

	private int m_id = -1;
	private String m_username;
	private String m_password;
	private String m_name;
	private String m_email;
	private String m_phone;
	private String m_mobile;
	private Role role = Role.Administrator;
	private boolean m_isAdministrator = false;

	public User()
	{
		super();
	}

	public User(int id, String username, String password, String name, String email, String phone, String mobile, Role role)
	{
		super();
		m_id = id;
		m_username = username;
		m_password = password;
		m_name = name;
		m_email = email;
		m_phone = phone;
		m_mobile = mobile;
		this.role = role;
	}

	public String getEmail()
	{
		return m_email;
	}

	public void setEmail(String email)
	{
		if (!equals(email, this.m_email))
		{
			m_email = email;
			fireValueChanged("email", this.m_email);
		}
	}

	public int getId()
	{
		return m_id;
	}

	/**
	 * Unnecessary to fire value change for the id property since it can't be
	 * viewed or edited in any UI and we also don't want a model to be dirty after
	 * setting the id returned from the server in the AbstractCrudPanel.saveNew
	 * methods.
	 */
	public void setId(int id)
	{
		m_id = id;
	}

	public String getMobile()
	{
		return m_mobile;
	}

	public void setMobile(String mobile)
	{
		if (!equals(mobile, m_mobile))
		{
			m_mobile = mobile;
			fireValueChanged("mobile", m_mobile);
		}
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		if (!equals(name, m_name))
		{
			m_name = name;
			fireValueChanged("name", m_name);
		}
	}

	public String getPassword()
	{
		return m_password;
	}

	public void setPassword(String password)
	{
		if (!equals(password, m_password))
		{
			m_password = password;
			fireValueChanged("password", m_password);
		}
	}

	public String getPhone()
	{
		return m_phone;
	}

	public void setPhone(String phone)
	{
		if (!equals(phone, m_phone))
		{
			m_phone = phone;
			fireValueChanged("phone", m_phone);
		}
	}

	public String toString()
	{
		return m_username;
	}

	public String getUsername()
	{
		return m_username;
	}

	public void setUsername(String username)
	{
		if (!equals(username, m_username))
		{
			m_username = username;
			fireValueChanged("username", m_username);
		}
	}

	/**
	 * Only used from internal system summary thread.
	 * 
	 * @param isAdmin
	 *          true to enable admin permissions
	 */
	public void enableAdministrator(boolean isAdmin)
	{
		m_isAdministrator = isAdmin;
		if (!equals(isAdmin, m_isAdministrator))
		{
			m_isAdministrator = isAdmin;
			fireValueChanged("isAdministrator", m_isAdministrator);
		}
	}

	public boolean isAdministrator()
	{
		return m_isAdministrator || role.equals(Role.Administrator);
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		if (!equals(role, this.role))
		{
			this.role = role;
			fireValueChanged("role", this.role);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == null ? false : m_id == ((User) obj).m_id;
	}

}
