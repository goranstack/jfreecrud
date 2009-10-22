package se.bluebrim.crud.example.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nu.esox.gui.model.ObservableListTableModel;
import nu.esox.util.ObservableIF;
import nu.esox.util.ObservableList;
import se.bluebrim.crud.ServerList;
import se.bluebrim.crud.ValidationResult;
import se.bluebrim.crud.client.AbstractCrudPanel;
import se.bluebrim.crud.client.AbstractMasterDetailPanel;
import se.bluebrim.crud.client.DirtyPredicatePanel;
import se.bluebrim.crud.client.UiUtil;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.example.common.ServiceLocator;
import se.bluebrim.crud.example.model.User;
import se.bluebrim.crud.example.remote.UserService;
/**
 * Create, read, update and delete of User's
 * 
 * @author GStack
 *
 */
public class UserCrudPanel extends AbstractCrudPanel
{
	private static final String USER_NAME_ALREADY_EXISTS = "user.nameAlreadyExists";
	private static final String USER_NAME_CAN_NOT_EMPTY = "user.nameCanNotEmpty";
	private static final Icon userIcon = UiUtil.getIcon("user.png", UserPanel.class);
	private static final Icon administratorIcon = UiUtil.getIcon("administrator.png", UserPanel.class);
	private final static ResourceBundle userBundle = ResourceBundle.getBundle("se.bluebrim.crud.example.client.user");
	private UserService userService;

  public static MasterDetailPanelFactory getPanelFactory()
  {
  	return new MasterDetailPanelFactory(){

			@Override
			protected AbstractMasterDetailPanel createMasterDetailPanel()
			{
				return new UserCrudPanel();
			}};
  }
 
	public UserCrudPanel()
	{
		super(false, false);
		userService = ServiceLocator.getUserService();
		setMasterList(new UsersList());
	}

	@Override
	protected String getConfirmDeleteMessage()
	{
		return userBundle.getString("user.confirmDelete");
	}

	@Override
	protected String getConfirmDeleteSeveralMessage()
	{
		return userBundle.getString("user.confirmDeleteSeveral");
	}

	@Override
	public String getObjectName(Object object)
	{
		return ((User) object).getUsername();
	}

	@Override
	protected void remove(Object object)
	{
		userService.removeUser(((User) object).getId());
	}

	@Override
	protected void saveChanges(Object object)
	{
		userService.changeUser((User) object);
	}
			
	@Override
	protected ObservableIF saveNew(ObservableIF object) throws RemoteException
	{
		User user = (User) object;
		int id = userService.addUser(user);
		user.setId(id);
		return user;
	}

	@Override
	protected DirtyPredicatePanel createDetailPanel()
	{
		return new UserPanel();
	}
	
	@Override
	protected ObservableIF createNew()
	{
		User user = new User();
		setDefaultValues(user);
		user.cleanDirty();
		return user;
	}
	
	private void setDefaultValues(User user)
	{
		user.setUsername(getDefaultUserName());
		user.setPassword("");
		user.setRole(User.Role.User);
	}

	private String getDefaultUserName()
	{
		return userBundle.getString("user.noName");
	}
	
	@Override
	protected void preAdd()
	{
		if (userNameAlreadyExists(getDefaultUserName(), null))
		{
			String message = MessageFormat.format(userBundle.getString(USER_NAME_ALREADY_EXISTS), new Object[]{getDefaultUserName()});
			showErrorDialog(this, message);
			throw new DefaultBackgroundCommand.CancelCommandException("User name already exists");
		}
	}
	
	@Override
	protected ValidationResult validate(Object object)
	{
		ValidationResult messages = new ValidationResult(userBundle);
		User userAdminDetails = (User) object;
		if (isUserNameEmpty(userAdminDetails.getUsername()))
			messages.add(USER_NAME_CAN_NOT_EMPTY);		
		else if (userNameAlreadyExists(userAdminDetails.getUsername(), userAdminDetails))
			messages.add(USER_NAME_ALREADY_EXISTS, userAdminDetails.getUsername());
		return messages;
	}
	
	private boolean isUserNameEmpty(String userName)
	{
		if(userName == null)
			return true;
		userName = userName.trim();// may be spaces or tab provided
		return userName.isEmpty();		
	}
	
	private boolean userNameAlreadyExists(String userName, User skipThis)
	{
		for (Object user : masterList)
		{
			User userAdminDetails = (User)user;
			if (userAdminDetails != skipThis && equalNames(userAdminDetails.getUsername(), userName))
				return true;
		}
		return false;		
	}
	
	private boolean equalNames(String name1, String name2)
	{
		if (name1 == name2)
			return true;
		if (name1 == null)
			return false;
		return name1.equalsIgnoreCase(name2);
	}
	
	@Override
	protected ObservableListTableModel createTableModel(ObservableList list)
	{
		return new UserListTableModel(list);
	}
	
	@Override
	protected void installTableCellRenderers()
	{
		table.setDefaultRenderer(User.class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof User)
				{
					User user = (User) value;
					if (user.isAdministrator())
						setIcon(administratorIcon);
					else
						setIcon(userIcon);
					setText(user.getUsername());
				}
				return this;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	private class UserListTableModel extends ObservableListTableModel
	{

		public UserListTableModel(ObservableList users) 
		{
	    super( users, false );
		}

		@Override
	  protected Column [] getColumns() 
	  { 
	  	return m_columns; 
	  }

	  private User getUserAdminDetails( Object o ) 
	  { 
	  	return (User) o; 
	  }
	  
	  private String displayPasswordString(String str)
	  {
			String s = "";
			for (int i = 0; str != null && i < str.length(); i++)
				s = s + "*";
			return s;
		}
	   
	  private final Column [] m_columns =
	      new Column []
	      {
 // For testing purposes
//		        new Column( "dirty", Boolean.class, false )
//		        {
//							@Override
//		          public Object getValue( Object target ) { return getUserAdminDetails( target ).isDirty(); }
//		        },

	          new Column( userBundle.getString("user.userName"), User.class, false )
	          {
							@Override
              public Object getValue( Object target ) { return getUserAdminDetails( target ); }
	          },
	          
	          new Column( userBundle.getString("user.name"), String.class, false )
	          {
							@Override
	             public Object getValue( Object target ) { return getUserAdminDetails( target ).getName(); }
	          },
	          
	          new Column( userBundle.getString("user.password"), String.class, false )
	          {
							@Override
				 public Object getValue( Object target ) { return displayPasswordString(getUserAdminDetails( target ).getPassword()); }				 	
	          },
	          
	          new Column( userBundle.getString("user.email"), String.class, false )
	          {
							@Override
              public Object getValue( Object target ) { return getUserAdminDetails( target ).getEmail(); }
	          },
	          
	          new Column( userBundle.getString("user.phone"), String.class, false )
	          {
							@Override
	            public Object getValue( Object target ) { return getUserAdminDetails( target ).getPhone(); }
	          },
	          
	          new Column( userBundle.getString("user.mobile"), String.class, false )
	          {
							@Override
	            public Object getValue( Object target ) { return getUserAdminDetails( target ).getMobile(); }
	          },

	      };
	}
	
	private class UsersList extends ServerList
	{
		private List<User> users;
		
		public UsersList() 
		{
		}
		
		@Override
		protected List<?> getList()
		{
			return users;
		}

		@Override
		public void readFromServer()
		{
			super.readFromServer();
			users = userService.getUsers();			
		}		
	}

}
