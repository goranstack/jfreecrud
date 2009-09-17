package se.bluebrim.crud.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import nu.esox.gui.aspect.EnablePredicateAdapter;
import nu.esox.gui.list.ListSelectionPredicate;
import nu.esox.util.ObservableIF;
import nu.esox.util.OrPredicate;
import nu.esox.util.Predicate;
import nu.esox.util.PredicateIF;
import se.bluebrim.crud.ValidationMessage;
import se.bluebrim.crud.ValidationResult;
import se.bluebrim.crud.client.command.ChainableCommand;
import se.bluebrim.crud.client.command.CommandChain;
import se.bluebrim.crud.client.command.ConditionalCommand;
import se.bluebrim.crud.client.command.DefaultAction;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.esox.DirtyPredicateModel;

/**
 * Abstract super class to panels that implements a CRUD user interface. Even
 * though CRUD stands for create, read, update and delete we use a different
 * vocabulary for the methods implementing the CRUD operations to comply with
 * java.util.List . <br>
 * The panel has three parts: <br>
 * A table at the top, a detail panel that shows the selected object and down at
 * the bottom buttons for create, update and delete. <br>
 * 
 * @author GStack
 * 
 */
public abstract class AbstractCrudPanel extends AbstractMasterDetailPanel
{
	public static final Icon SAVE_ICON = UiUtil.getIcon("save.gif", AbstractCrudPanel.class);
	private static final ResourceBundle clientBundle = ResourceBundle.getBundle(AbstractCrudPanel.class.getPackage().getName() + ".crud");
	
	protected AbstractAddAction addAction;
	protected Action removeAction;
	protected Action saveChangesAction;
	private JButton addButton;
	private JButton deleteButton;

	
	public class StringIcon
	{
		public String text;
		public Icon icon;
		
		public StringIcon(String text)
		{
			this(text, null);
		}

		public StringIcon(String text, Icon icon)
		{
			this.text = text;
			this.icon = icon;
		}
		
	}
	
	/**
	 * Command that saves changes in an object. Before saving the object is validated and
	 * possible errors are presented in a modal dialog as a bulleted list. The validation
	 * can return warnings as well and the user can continue to save after confirming
	 * the warnings. Errors always cancel the command.
	 */
	private class SaveChangesCommand extends DefaultBackgroundCommand
	{
		protected ObservableIF objectToSave;
		private ValidationResult validationResult;
		
		public SaveChangesCommand()
		{
			super();
			this.objectToSave = getDetailModelOwner().getModel();
		}
		
		@Override
		public void prepare()
		{
			if (objectToSave == null) // Just in case disabling of button is out of order
				return;

			validationResult = validate();
			if (validationResult.hasMessagesOfType(ValidationMessage.Type.ERROR)) 
			{
				showValidationMessageDialog(AbstractCrudPanel.this, validationResult, ValidationMessage.Type.ERROR);
				throw new DefaultBackgroundCommand.CancelCommandException("Validation failed");
			}
			else if (validationResult.hasMessagesOfType(ValidationMessage.Type.WARNING))
			{
				boolean ok = showValidationMessageDialog(AbstractCrudPanel.this, validationResult, ValidationMessage.Type.WARNING);
				if (!ok)
					throw new DefaultBackgroundCommand.CancelCommandException("Cancelled due to validation warnings");
			}
		}
		
		private boolean showValidationMessageDialog(Component parent, ValidationResult result, ValidationMessage.Type type)
		{
			StringBuilder sb = new StringBuilder();
			for (String s : result.getLocalizedMessagesOfType(type))
			{
				sb.append("<li>" + s + "</li>");
			}
			if (type == ValidationMessage.Type.ERROR)
			{
				showErrorMessage(clientBundle, "validation.error.message", sb.toString());
				return true;
			}
			else
				return showConfirmationMessage(clientBundle, "validation.warning.message", sb.toString());
		}

		private ValidationResult validate() 
		{
			return AbstractCrudPanel.this.validate(objectToSave);
		}

		@Override
		public Object runInBackground() throws Exception
		{
			saveChanges(objectToSave);
			if (objectToSave instanceof DirtyPredicateModel)
				((DirtyPredicateModel) objectToSave).cleanDirty();
			return objectToSave;
		}
	}
	
	/**
	 * Ask the user if he wants to save in case the objects to save is dirty.
	 * If the user don't want to save the command is canceled. The ConditionalCommand
	 * that is specified in the constructor is supposed to be a subsequent command in
	 * a command chain that this command also is a part of
	 *
	 */
	private class SaveChangesIfUserWantsCommand extends DefaultBackgroundCommand
	{
    private ConditionalCommand saveOnTrueRefreshOnFalseCommand;
    
		public SaveChangesIfUserWantsCommand(ConditionalCommand saveOnTrueRefreshOnFalseCommand)
		{
			super();
			this.saveOnTrueRefreshOnFalseCommand = saveOnTrueRefreshOnFalseCommand;
		}
				
		@Override
		public void prepare()
		{
			if (detailPanel == null) // It's possible to use AbstractCrudPanel
																// without detail panel to limit UI depth
				return;
			if (detailPanel.isDirty())
			{
				DirtyPredicateModel model = (DirtyPredicateModel) detailPanel.getModel();
				int answer = showSaveChangesDialog("crud.saveChangesIn", model);
				switch (answer)
				{
				case JOptionPane.YES_OPTION:

					saveOnTrueRefreshOnFalseCommand.setCondition(ConditionalCommand.Condition.TRUE_COMMAND);
					break;

				case JOptionPane.NO_OPTION:

					saveOnTrueRefreshOnFalseCommand.setCondition(ConditionalCommand.Condition.FALSE_COMMAND);
					break;

				case JOptionPane.CANCEL_OPTION:
				{
					cancel();
					throw new DefaultBackgroundCommand.CancelCommandException("Canceled Save before... question");
				}

				default:
					throw new RuntimeException("Unexcepted answer from JOptionPane: " + answer);
				}
			} else
					saveOnTrueRefreshOnFalseCommand.setCondition(ConditionalCommand.Condition.NO_COMMAND);				
		}
		
		@Override
		/**
		 * The command has no background process but must be a background command to fit into
		 * a command chain. To let command chain handle both foreground and background commands
		 * is not worth the effort for getting rid off this empty method.
		 */
		public Object runInBackground() throws Exception
		{
			return null;
		}
		
		private int showSaveChangesDialog(String messageKey, DirtyPredicateModel model)
		{
			String message = MessageFormat.format(clientBundle.getString(messageKey), new Object[] { getObjectName(model) });
			int answer = JOptionPane.showConfirmDialog(AbstractCrudPanel.this, message, UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_CANCEL_OPTION);
			return answer;
		}

	}
	
	
	private class SaveChangesAction extends DefaultAction
	{
		public SaveChangesAction(String localizedText)
		{
			super(localizedText);
			setSmallIcon(SAVE_ICON);
		}
		
		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			new CommandChain(new SaveChangesCommand(), new RefreshCommand()).run();		
		}			
	}
	
	/**
	 * Abstract superclass the add actions that is handling cases where there are
	 * several options to choose among when instantiating the object to add. In
	 * those cases the user will get a pop up menu when pressing the add button. The
	 * selected option is stored in the add command and passed on to the createNew
	 * method in the AbstractCrudPanel subclass
	 * 
	 */
	private static abstract class AbstractAddAction extends DefaultAction
	{
		private JPopupMenu optionsMenu;
		private Map<StringIcon, Object> options;
		protected Object selectedOption;

		public AbstractAddAction()
		{
			super(clientBundle.getString("crud.add"));
			putValue(Action.SMALL_ICON, UiUtil.getIcon("add.gif", AbstractCrudPanel.class));
		}
			
		public void addOptionsToButton(final JButton button)
		{
			if (options == null)
				return;
			optionsMenu = new JPopupMenu();
			addOptionsToMenu(optionsMenu);
			button.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent evt)
				{
					showOptionMenu(evt);
				}

				private void showOptionMenu(MouseEvent evt)
				{
					Rectangle bounds = button.getBounds();
					optionsMenu.show(evt.getComponent(), 0, (int) bounds.getHeight());
				}
			});
			addPopUpMenuArrow(button);
		}

		/**
		 * Add a small down arrow to the add button label indicating that there is
		 * a popup menu.
		 * 
		 */
		private void addPopUpMenuArrow(JButton button)
		{
			int IMAGE_WIDTH = 15;
			int ASSUMED_GAP = 2;
			Dimension size = button.getPreferredSize();
			URL url = getClass().getResource("popupmenu.gif");
			button.setText("<html><p>" + button.getText() + "<image src='" + url.toString() + "'/></p></html>");
			button.setMaximumSize(new Dimension(size.width + IMAGE_WIDTH + ASSUMED_GAP, size.height));
		}

		protected void addOptionsToMenu(Object menu)
		{
			for (Map.Entry<StringIcon, Object> entry : options.entrySet())
			{
				addOptionMenuItem(menu, entry);
			}
		}

		/**
		 * We have to type type the menu argument to Object since this method is
		 * used to add menu items to both JPopupMenu and JMenu
		 */
		private void addOptionMenuItem(Object menu, final Map.Entry<StringIcon, Object> option)
		{
			AbstractAction optionAction = new AbstractAction(option.getKey().text, option.getKey().icon)
			{

				public void actionPerformed(ActionEvent e)
				{
					selectedOption = option.getValue();
					AbstractAddAction.this.actionPerformed(e);
				}
			};
			if (menu instanceof JPopupMenu)
				((JPopupMenu) menu).add(optionAction);
			else
				((JMenu) menu).add(optionAction);
		}

	}

	/**
	 * Action executed when the user presses the Add button in case non modal add
	 * action is chosen. <br>
	 * Creates a new instance and displays the instance in a modal dialog with a
	 * Save and a Cancel button. Executes a SaveNew action if the user presses
	 * the Save button.
	 */
	private class AddAction extends AbstractAddAction
	{

		public AddAction()
		{
			super();
		}

		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			ConditionalCommand saveOnTrueRefreshOnFalseCommand = new ConditionalCommand(new SaveChangesCommand(), new RefreshCommand());
			new CommandChain(new SaveChangesIfUserWantsCommand(saveOnTrueRefreshOnFalseCommand), saveOnTrueRefreshOnFalseCommand, new AddCommand(selectedOption)).run();			
		}

	}
	
	private class SaveNewCommand extends DefaultBackgroundCommand
	{
		private ObservableIF newObject;
		
		public SaveNewCommand(ObservableIF newObject)
		{
			super();
			this.newObject = newObject;
		}

		@Override
		protected Object runInBackground() throws Exception
		{
			return saveNew(newObject);
		}
		
		@Override
		public void publishResult(Object result) throws Exception
		{
			masterList.add(result);
			selectObjectInTable(result);
		}		
	}
	
	/**
	 * A command that creates and saves an object. The command is run when the
	 * user presses the add button.
	 */
	private class AddCommand extends DefaultBackgroundCommand
	{
		protected Object newObjectOption;
		
		public AddCommand(Object newObjectOption)
		{
			super();
			this.newObjectOption = newObjectOption;
		}

		@Override
		public void prepare()
		{
			if(newObjectOption == null)
				preAdd() ;
			else
				preAdd(newObjectOption);
		}
		
		@Override
		public Object runInBackground() throws Exception
		{
			return saveNew(newObjectOption == null ? createNew() : createNew(newObjectOption));
		}

		@Override
		public void publishResult(Object result) throws Exception
		{
			masterList.add(result);
			selectObjectInTable(result);
		}		
	}
	
	/**
	 * Action executed when the user presses the Add button in case modal add
	 * action in chosen. <br>
	 * Creates a new instance and displays the instance in a modal dialog with a
	 * Save and a Cancel button. Executes a SaveNew action if the user presses
	 * the Save button.
	 */
	private class ModalAddAction extends AbstractAddAction
	{
		private DirtyPredicatePanel modalAddPanel;
		private JDialog modalAddDialog;

		public ModalAddAction()
		{
			super();
		}
		
		protected void cleanup()
		{
			modalAddDialog.setVisible(false);
			modalAddDialog.dispose();
			modalAddPanel = null;
			modalAddDialog = null;
		}

		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			openModalDialog();			
		}

		private void openModalDialog()
		{
			preAdd();
			ObservableIF objectToAdd = selectedOption == null ? createNew() : createNew(selectedOption);
			if (objectToAdd instanceof DirtyPredicateModel)
				((DirtyPredicateModel) objectToAdd).cleanDirty();
			modalAddPanel = createDetailPanelForModalNewAction();
			modalAddPanel.initDirtyPredicatePanel();
			modalAddPanel.setModel(objectToAdd);
			modalAddDialog = new JDialog(JOptionPane.getFrameForComponent(AbstractCrudPanel.this), clientBundle.getString("crud.enterNewData"));
			modalAddDialog.setModal(true);
			JPanel content = new JPanel(new BorderLayout());
			content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JComponent buttonPanel = createButtonPanel(objectToAdd);
			content.add(modalAddPanel, BorderLayout.CENTER);
			content.add(buttonPanel, BorderLayout.SOUTH);
			modalAddDialog.add(content);
			modalAddDialog.pack();
			modalAddDialog.setLocationRelativeTo(null); // Center on screen
			modalAddDialog.setVisible(true);		
		}
		
		private JComponent createButtonPanel(ObservableIF objectToAdd)
		{
			JComponent buttonPanel = createHorizontalButtonPanel();
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			JButton saveButton = new JButton(createSaveAction(objectToAdd));
			JButton cancelButton = new JButton(createCancelAction());
			// The buttons are added in reverse order due to component orientation
			// TODO: The placement of the cancel and save button vary between
			// platforms. This
			// should be handled the same way as JOptionPane does.
			buttonPanel.add(saveButton);
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(cancelButton);
			return buttonPanel;
		}

		private Action createSaveAction(final ObservableIF objectToAdd)
		{
			DefaultAction saveAction =  new DefaultAction(clientBundle.getString("crud.save"))
			{			
				@Override
				protected void execute(ActionEvent evt) throws Exception
				{
					cleanup();
					new CommandChain(new SaveNewCommand(objectToAdd), new RefreshCommand()).run();
				}			
			};
			new EnablePredicateAdapter(null, null, saveAction, null, modalAddPanel.getDirtyPredicate());
			return saveAction;
		}
		
		/**
		 * By removing the panel from the dialog we prevent the panel from
		 * being invisible as the dialog gets invisible.
		 */
		private Action createCancelAction()
		{
			return new DefaultAction(clientBundle.getString("crud.cancel"))
			{			
				@Override
				protected void execute(ActionEvent evt) throws Exception
				{
					cleanup();				
				}			
			};
		}
	}
	
	private class RemoveAction extends DefaultAction
	{
		public RemoveAction()
		{
			super(clientBundle.getString("crud.delete"), UiUtil.getIcon("delete.gif", AbstractCrudPanel.class));
		}
		
		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			new CommandChain(new RemoveCommand(), new RefreshCommand()).run();			
		}		
	}
	
	private class RemoveCommand extends DefaultBackgroundCommand
	{
		private List selectedInTable;
		
		public RemoveCommand()
		{
		}
		
		@Override
		public void prepare()
		{
			selectedInTable = getSelectedInTable();
			if (!isRemovalConfirmedByUser(selectedInTable))
				throw new DefaultBackgroundCommand.CancelCommandException("Removal denied by user");
			preRemove(selectedInTable);
			resetUnsavedChangesFlag();
		}

		@Override
		public Object runInBackground() throws Exception
		{
			for (Object object : selectedInTable)
			{
				remove(object);
			}
			return null;
		}
		
		/**
		 * Make sure no save question will appear for deleted objects by
		 * throwing away all unsaved changes
		 */
		private void resetUnsavedChangesFlag()
		{
			for (Object element : selectedInTable)
			{
				if (element instanceof DirtyPredicateModel)
					((DirtyPredicateModel) element).cleanDirty();
			}
		}		
	}


	/**
	 * If detail panel is null just create a fake saveButtonPredicate.
	 */
	public AbstractCrudPanel(boolean hideButtons, boolean useModalAddAction)
	{
		super(hideButtons);

		// Create actions
		createActions(useModalAddAction);

		// Create enable adapters
		new EnablePredicateAdapter(null, null, removeAction, null, getRemoveActionPredicate());
		PredicateIF saveButtonPredicate;
		if (detailPanel == null)
			saveButtonPredicate = new Predicate(false);
		else
			saveButtonPredicate = detailPanel.getDirtyPredicate();
		new EnablePredicateAdapter(null, null, saveChangesAction, null, saveButtonPredicate);
		new EnablePredicateAdapter(null, null, addAction, null, getAddActionPredicate());
	}

	protected PredicateIF getRemoveActionPredicate()
	{
		return new ListSelectionPredicate(table.getSelectionModel(), ListSelectionPredicate.TEST_SOME);
	}

	private void createActions(boolean useModalAddAction)
	{
		if (useModalAddAction)
			addAction = new ModalAddAction();
		else
			addAction = new AddAction();
		addAction.options = getAddActionOptions();
		saveChangesAction = new SaveChangesAction(clientBundle.getString("crud.save"));
		removeAction = new RemoveAction();
	}

	protected void addMasterButtons(JComponent buttonPanel)
	{
		super.addMasterButtons(buttonPanel);
		// The buttons are added in reverse order due to component orientation
		addDeleteButton(buttonPanel);
		addAddButton(buttonPanel);
	}
	
	protected void addAddButton(JComponent buttonPanel)
	{
		addButton = new JButton(addAction);
		addAction.addOptionsToButton(addButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(addButton);		
	}
	
	protected void addDeleteButton(JComponent buttonPanel)
	{
		deleteButton = new JButton(removeAction);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(deleteButton);		
	}
	

	@Override
	protected void addDetailPanelButtons(DirtyPredicatePanel detailPanel)
	{
		JComponent buttonPanel = createHorizontalButtonPanel();
		buttonPanel.add(new JButton(getSaveChangesAction()));
		detailPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	public void addContextMenuItems(JPopupMenu contextMenu)
	{
		super.addContextMenuItems(contextMenu);
		if (addAction.options != null)
		{
			JMenu subMenu = new JMenu(addAction);
			addAction.addOptionsToMenu(subMenu);
			contextMenu.add(subMenu);
		} else
			contextMenu.add(addAction);
		contextMenu.add(removeAction);
	}

	protected void showErrorDialog(Component parent, String message)
	{
		JOptionPane.showMessageDialog(parent, message, UIManager.getString("OptionPane.messageDialogTitle"), JOptionPane.ERROR_MESSAGE);
	}

	protected void preAdd()
	{

	}
	
	protected void preAdd(Object option)
	{

	}

	protected void preRemove(List objects)
	{

	}

	/**
	 * This method is called when useModalAddAction is chosen in the
	 * constructor. Subclasses should return a new fresh instance every time. Do
	 * not return the single instance that is returned in the getDetailPanel()
	 * method!
	 */
	protected DirtyPredicatePanel createDetailPanelForModalNewAction()
	{
		throw new UnsupportedOperationException("Should be overidden");
	}

	protected ObservableIF createNew()
	{
		throw new UnsupportedOperationException("Should be overriden");
	}

	/**
	 * This method is called when there are several classes to choose from when
	 * adding a new.
	 */
	protected ObservableIF createNew(Object option)
	{
		throw new UnsupportedOperationException("Should be overriden");
	}

	/**
	 * This method is executed outside the AWT thread. Do not access any Swing
	 * components
	 */
	protected ObservableIF saveNew(ObservableIF object) throws RemoteException
	{
		throw new UnsupportedOperationException("Should be overriden");
	}

	public abstract String getObjectName(Object object);

	protected abstract String getConfirmDeleteMessage();

	protected abstract String getConfirmDeleteSeveralMessage();

	@Override
	/**
	 * Return a command chain with two commands where the second command is
	 * a conditional command. Since this command can be used as a predecessor
	 * to a refresh command there must be a way to suppress the refresh that
	 * is performed when the user answer no on the question: Do you what to save
	 * changes?
	 */
	protected ChainableCommand createSaveChangesInDirtyModelCommand(boolean discardChangesByRefresh)
	{
		if (detailPanel == null)
			return super.createSaveChangesInDirtyModelCommand(discardChangesByRefresh);
		else
		{
			ConditionalCommand saveOnTrueRefreshOnFalseCommand = new ConditionalCommand(new SaveChangesCommand(), discardChangesByRefresh ? new RefreshCommand() :null);
			return new CommandChain(new SaveChangesIfUserWantsCommand(saveOnTrueRefreshOnFalseCommand), saveOnTrueRefreshOnFalseCommand);
		}
	}
		
	/**
	 * Override this method to supply options to the add new command. A menu at the add
	 * button will let the user select among the specified options. The selected
	 * option is passed as argument to the createNewMethod. The options are
	 * represented as a map where the key is the name and icon of the option that is
	 * displayed in the option menu and the value is passed as selected option to
	 * the createNew method.
	 */
	public Map<StringIcon, Object>getAddActionOptions()
	{
		return null;
	}

	/**
	 * This method is executed outside the AWT thread. Do not access any Swing
	 * components
	 */
	protected abstract void saveChanges(Object object);

	/**
	 * Choose between two different confirm messages depending one how many
	 * objects there are to be removed
	 */
	private boolean isRemovalConfirmedByUser(List objectsToRemove)
	{
		String message = objectsToRemove.size() > 1 ? MessageFormat.format(getConfirmDeleteSeveralMessage(), new Object[] { objectsToRemove.size() })
				: MessageFormat.format(getConfirmDeleteMessage(), new Object[] { getObjectName(objectsToRemove.get(0)) });
		int userAnswer = JOptionPane.showConfirmDialog(this, message, UIManager.getString("OptionPane.titleText"), JOptionPane.YES_NO_OPTION);
		return userAnswer == JOptionPane.OK_OPTION;
	}

	/**
	 * This method is executed outside the AWT thread. Do not access any Swing
	 * components
	 */
	protected abstract void remove(Object object);

	/**
	 * Create a subclass of this class and add an instance to the table. To
	 * display a popup menu at right click by do
	 * <code>popupMenu.show(table, ev.getX(), ev.getY());</code> in the
	 * handleRightClick method.
	 */
	protected abstract class TableMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent ev)
		{
			if (ev.getButton() == MouseEvent.BUTTON1 && ev.getClickCount() == 2)
				handleDoubleClick(ev);
			else if (ev.getButton() == MouseEvent.BUTTON3 && ev.getClickCount() == 1)
			{
				JTable src = (JTable) ev.getSource();
				int row = src.rowAtPoint(ev.getPoint());
				if (row >= 0)
				{
					ListSelectionModel selectionModel = src.getSelectionModel();
					// A right click outside current selection changes the selection
					// before handling the
					// right click
					if (row < selectionModel.getMinSelectionIndex() || row > selectionModel.getMaxSelectionIndex())
						selectionModel.setSelectionInterval(row, row);
					handleRightClick(ev);
				}
				ev.consume();
			}
			super.mousePressed(ev);
		}

		protected void handleDoubleClick(MouseEvent ev)
		{
		}

		protected void handleRightClick(MouseEvent ev)
		{
		}
	}

	public Action getSaveChangesAction()
	{
		return saveChangesAction;
	}

}
