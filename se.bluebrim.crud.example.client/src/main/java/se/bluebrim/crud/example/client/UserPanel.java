package se.bluebrim.crud.example.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nu.esox.gui.TextFieldFocusHandler;
import nu.esox.gui.aspect.ComboBoxAdapter;
import nu.esox.gui.aspect.TextFieldAdapter;
import nu.esox.gui.layout.ColumnLayout;
import se.bluebrim.crud.client.DirtyPredicatePanel;
import se.bluebrim.crud.client.FormPanel;
import se.bluebrim.crud.client.UiUtil;
import se.bluebrim.crud.example.model.User;

/**
 * Panel for editing Users
 * 
 * @author GStack
 *
 */
public class UserPanel extends DirtyPredicatePanel
{
	private final static ResourceBundle userBundle = ResourceBundle.getBundle("se.bluebrim.crud.example.client.user");
	private static final Icon userIcon = UiUtil.getIcon("user.png", UserPanel.class);
	private static final Icon administratorIcon = UiUtil.getIcon("administrator.png", UserPanel.class);

	private JTextField userName;
	private JTextField name;
	private JPasswordField password;
	private JTextField email;
	private JTextField phone;
	private JTextField mobile;
	private JComboBox role;
	
	public UserPanel()
	{
		initComponents();
		arrangeLayout();
		bindComponents();
		setRenderers();
	}
	

	private void initComponents()
	{
		userName = createInputLimitedTextField(15, 30);
		name = createInputLimitedTextField(40, 30);
		password = createInputLimitedPasswordField(30, 30);
		password.setEchoChar('*');
		email = createInputLimitedTextField(50, 30);
		phone = createInputLimitedTextField(20, 30);
		mobile = createInputLimitedTextField(20, 30);	
		role = new JComboBox();
	}
	
	private void arrangeLayout()
	{
		setLayout(new BorderLayout());
		FormPanel formPanel = new FormPanel();
		formPanel.setLayout(new ColumnLayout());
		formPanel.addFormRow(userBundle.getString("user.userName"), userName);
		formPanel.addFormRow(userBundle.getString("user.name"), name);
		formPanel.addFormRow(userBundle.getString("user.password"), password);
		formPanel.addFormRow(userBundle.getString("user.email"), email);
		formPanel.addFormRow(userBundle.getString("user.phone"), phone);
		formPanel.addFormRow(userBundle.getString("user.mobile"), mobile);
		formPanel.addFormRow(userBundle.getString("user.role"), role);
		formPanel.adjustLabelWidthsToLargest();
		add(formPanel, BorderLayout.CENTER);
	}

	private void bindComponents() 
	{
		new TextFieldAdapter(userName, this, User.class, "getUsername", "setUsername", null);
		new TextFieldAdapter(name, this, User.class, "getName", "setName", null);
		new TextFieldAdapter(password, this, User.class, "getPassword", "setPassword", null);
		new TextFieldAdapter(email, this, User.class, "getEmail", "setEmail", null);
		new TextFieldAdapter(phone, this, User.class, "getPhone", "setPhone", null);
		new TextFieldAdapter(mobile, this, User.class, "getMobile", "setMobile", null);
		role.setModel(new DefaultComboBoxModel(User.Role.values()));
		new ComboBoxAdapter(role, this, User.class, "getRole", "setRole", User.Role.class, null, null, null);
		TextFieldFocusHandler.add(userName);
		TextFieldFocusHandler.add(name);
		TextFieldFocusHandler.add(password);
		TextFieldFocusHandler.add(email);
		TextFieldFocusHandler.add(phone);
		TextFieldFocusHandler.add(mobile);		
	}
	
	private void setRenderers()
	{
		role.setRenderer(new UserRoleComboBoxCellRenderer(userBundle));
	}

	private static final class UserRoleComboBoxCellRenderer extends DefaultListCellRenderer
	{
		private ResourceBundle bundle;

		public UserRoleComboBoxCellRenderer(ResourceBundle bundle) 
		{
			super();
			this.bundle = bundle;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null)
			{
				User.Role userRole = (User.Role)value;
				setText(bundle.getString(userRole.name()));
				if (userRole == User.Role.Administrator)
					setIcon(administratorIcon);
				else
					setIcon(userIcon);
			} else
				setText(bundle.getString("noneUserRole"));
			return component;
		}
	}
	
}
