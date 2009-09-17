package se.bluebrim.crud.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import nu.esox.gui.aspect.DefaultModelOwner;
import nu.esox.gui.aspect.ModelOwnerIF;
import nu.esox.gui.model.ObservableListTableModel;
import nu.esox.util.ObservableIF;
import nu.esox.util.ObservableList;
import nu.esox.util.PredicateIF;
import se.bluebrim.crud.ServerList;
import se.bluebrim.crud.ValidationResult;
import se.bluebrim.crud.client.command.BackgroundCommand;
import se.bluebrim.crud.client.command.ChainableCommand;
import se.bluebrim.crud.client.command.CommandChain;
import se.bluebrim.crud.client.command.DefaultAction;
import se.bluebrim.crud.client.command.DefaultBackgroundCommand;
import se.bluebrim.crud.client.command.NoOperationBackgroundCommand;

/**
 * Abstract super class to panels that displays a list of objects
 * in a table and a detail panel displaying the object of the selected row
 * in the table.<br>
 * See <a href="http://en.wikipedia.org/wiki/Master-detail">Master detail</a><br>
 * TODO: We should split this class into two separate classes since there are cases
 * where we only want the master part or the detail part. For example sometimes
 * we want to reduce the depth of user interface by only showing a table of objects
 * and no details for the selected row in the table. Now that is solved by subclasses
 * that returns null as detail panel which gets special treatment in this class. See
 * the anonymous subclass of TerminalCrudPanel in IMasterCrudPanel that returns null
 * as detail panel.<br>
 * An example of separate use of the detail part is when the user double clicks on a
 * node in the tree overview. Then the detail part of master detail user interface for
 * actual node type is displayed in a separate tab. Now this is solved by using the whole
 * master detail user interface and select the table row that corresponds to the double
 * clicked node in the tree. The the table is hidden and the split pane is replaced by the 
 * detail part. An ugly operation but the only way to reuse the detail part for tree overview. 
 * <br> 
 * We could name the new classes:
 * <ul> 
 * <li>TableUI - the master part of the MasterDetailPanel
 * <li>ReadOnlyDetailUI - the detail part of the MasterDetailPanel
 * <li>DetailUI - the detail part of the AbstractCrudPanel inheriting from ReadOnlyDetailUI
 * </ul>
 * @author GStack
 *
 */
public abstract class AbstractMasterDetailPanel extends AbstractPanel implements VetoableWindowClosingListener
{
	
	public interface DoubleClickHandler
	{
		public void handleDoubleClick(Object objectUnderMouse);
	}
 
  protected JTable table;
	private LoadingInProgressOverlay northLayeredPane;
	protected DirtyPredicatePanel detailPanel;
	private BlockingPanel southBlockingPanel;
	private JLayeredPane southLayeredPane;
  protected ModelOwnerIF detailModelOwner;	// Used as an alternative model owner when there is no detail panel
  private JComponent masterButtonPanel;
  protected ObservableListTableModel tableModel;
  protected ServerList masterList;
  private Component filterPanel;
	private DoubleClickHandler doubleClickHandler;
	private JPopupMenu contextMenu;
	private MouseAdapter tableMouseListener;
	private JSplitPane splitPane;
	protected DefaultAction refreshAction;
	private JComponent footerPanel;
	private JScrollPane scrollPane;
 
	/**
	 * In the most common case both top (table) and bottom (detail panel)
	 * component is present but there are subclasses where there is no detail panel
	 * or other subclasses where there is no table at the top. The reason for not
	 * having a detail panel can be to limit the depth of the user interface.<br>
	 * AbstractMasterDetailPanel without a table at the top is used by the
	 * TreeOverviewPanel to display the model of the selected element in tree
	 * in a full blown user interface.
	 *  
	 */
	public AbstractMasterDetailPanel(boolean hideButtons) 
	{
		super();
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		createActions();

		if (!hideButtons)
			masterButtonPanel = createMasterButtonPanel();

		table = new MasterTable(tableModel = createTableModel(null));		

		detailPanel = createDetailPanel();
		Component topComponent = createNorthComponent();
		if (detailPanel != null)
		{
			detailPanel.initDirtyPredicatePanel();
			detailPanel.addListener(new DetailPanelBlocker());
			createSouthComponent();
			if (topComponent == null)
			{
				// No table only a detail panel
				add(southLayeredPane, BorderLayout.CENTER);
			} else
			{
				// Both table and detail panel
				new InjectModelInToDetailPanelListener();
				splitPane = new JSplitPane(getSplitOrientation());
				add(splitPane, BorderLayout.CENTER);
				splitPane.setTopComponent(topComponent);
				splitPane.setBottomComponent(southLayeredPane);
				splitPane.setResizeWeight(getResizeWeight());
			}
		} else
		{
			// No detail panel only a table
			detailModelOwner = new DefaultModelOwner();
			new InjectModelInToDetailModelOwnerListener();
			add(topComponent, BorderLayout.CENTER);	
		}
		installTableCellRenderers();		
	}
	
	/**
	 * JLayeredPane won't work if any layout is set according to:
	 * http://forum.java.sun.com/thread.jspa?forumID=57&threadID=770737 <br>
	 * Therefore we have to do all layouting by our self.
	 */
	private void createSouthComponent()
	{
		southLayeredPane = new JLayeredPane()
		{
			@Override
			public Dimension getPreferredSize()
			{
				return detailPanel.getPreferredSize();
			}
						
			@Override
			public void doLayout()
			{
				super.doLayout();
				detailPanel.setSize(getSize());
				southBlockingPanel.setSize(getSize());

			}
		};
		southBlockingPanel = new BlockingPanel();
		southLayeredPane.add(detailPanel, JLayeredPane.DEFAULT_LAYER);
		southLayeredPane.add(southBlockingPanel, JLayeredPane.DRAG_LAYER);
	}
	
	protected void createActions()
	{
		refreshAction = new RefreshAction();
	}
	
	/**
	 * TODO: Temporary fix until we have divided this class into two separate classes.
	 * The present design do not permit reuse of the detail part in an other context but
	 * this transformation will produce the same result. <br>
	 * See class comment.
	 */
	public void transformToSingleInstanceUI(ObservableIF detailModel)
	{
		removeFilterPanel();
		remove(splitPane);
		add(splitPane.getRightComponent());
		splitPane = null;
		getDetailModelOwner().setModel(detailModel);
		modelInjectedInToDetailPanel(detailModel);	
	}

	protected void removeFilterPanel()
	{
		remove(filterPanel);
	}
	
	public void initialize()
	{
		createContextMenu();
		if (masterButtonPanel != null)
			addMasterButtons(masterButtonPanel);
		addDetailPanelButtons(detailPanel);
		addFilterObserver();
	}
	
	/**
	 * Override this to for example update Action with the current selected model
	 */
	protected void modelInjectedInToDetailPanel(ObservableIF newModel)
	{
		
	}

	public Action getRefreshAction()
	{
		return refreshAction;
	}
	
	
	/**
	 * Used when there is no table at the top and the selection of model to
	 * be injected in to the detail panel is handled outside the master detail panel. 
	 */
	public void injectModelInToDetailPanel(ObservableIF newModel)
	{
		new InjectModelInToDetailPanelCommand(newModel).run();			
	}	

	/**
	 * Override this method to install cell renderers on the table
	 *
	 */
	protected void installTableCellRenderers()
	{
	}

	
	protected PredicateIF getAddActionPredicate()
	{
		return getHasModel();
	}

	/**
	 * Overridden by subclasses that wants a horizontal split pane
	 */
	protected int getSplitOrientation()
	{
		return JSplitPane.VERTICAL_SPLIT;
	}
	
	protected float getResizeWeight()
	{
		return 0.5f;
	}
	
	protected final void toggleDetailPanelVisibility()
	{
		setDetailPanelVisable(!southLayeredPane.isVisible());
	}
	
	protected final void setDetailPanelVisable(boolean visible)
	{
		southLayeredPane.setVisible(visible);
		splitPane.resetToPreferredSizes();		
	}
	
	protected final boolean isDetailPanelVisable()
	{
		return southLayeredPane.isVisible();
	}
		
	protected void setSortOrder(int columnIndex, SortOrder sortOrder)
	{
		table.getRowSorter().setSortKeys(Arrays.asList(new SortKey(columnIndex, sortOrder)));	
	}
	
	private JComponent createMasterButtonPanel()
	{
		JComponent buttonPanel = createHorizontalButtonPanel();
		return buttonPanel;
	}
	
	protected void addMasterButtons(JComponent buttonPanel)
	{
		JButton refreshButton = new JButton(refreshAction);		
		refreshButton.setBorder(null);
		refreshButton.setContentAreaFilled(false);
		refreshButton.setRolloverIcon(UiUtil.getIcon("refresh_rollover.png", AbstractMasterDetailPanel.class));
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(refreshButton);		
	}
	
	
	protected void addDetailPanelButtons(DirtyPredicatePanel detailPanel)
	{	
	}
	
	/**
	 * Do the same thing as pressing the refresh button
	 *
	 */
	public void refresh()
	{
		new RefreshCommand().run();
	}
	
	@Override
	/**
	 * The clearSelection() call will no trigger the JTable.changeSelection method
	 * that normally handle unsaved work. Therefore we have to explicitly run the
	 * SaveChangesInDirtyModelCommand before we call clear selection which is needed
	 * by subclasses that publish messages to the EventBus about selected model
	 * in the crud GUI. When a window is closed no model is selected any more which
	 * can be important knowledge to subscribers of that message. <br>
	 */
	public void internalFrameClosing(final VetoableWindowClosingListener.DoneListener doneListener)
	{
		detailPanel.updateModelWithOnGoingTextEditing();
		final ChainableCommand command = createSaveChangesInDirtyModelCommand(true);
		command.run(new BackgroundCommand.DoneListener(){

			@Override
			public void done()
			{
				if (command.isCancelled())
					doneListener.done(false);
				else
					if (command.hadException())
						doneListener.done(true);
				else
				{
					table.getSelectionModel().clearSelection();
					doneListener.done(true);
				}			
			}});
	}
		
	/**
	 * Overridden by subclasses that needs to do more than reloading the list
	 * 
	 */
	protected void postRefresh() throws RemoteException
	{
		
	}

	/**
	 * AbstractMasterDetails can be used without master buttons controlled by a boolean
	 * in the constructor. <br>
	 * The north component is added as on of the layers of a layered pane.
	 * The other layer is a blocking panel that give user feedback that the table
	 * is waiting for new data from the server. <br>
	 * <br>
	 * JLayeredPane won't work if any layout is set according to:
	 * http://forum.java.sun.com/thread.jspa?forumID=57&threadID=770737 <br>
	 * Therefore we have to do all layouting by our self.
	 */
	private Component createNorthComponent()
	{
		final Component northComponent;
		Component masterComponent = createMasterComponent();
		if (masterButtonPanel == null || masterComponent == null)
			northComponent = masterComponent;
		else
		{
			JPanel panelWithButtons = new JPanel(new BorderLayout());
			panelWithButtons.add(masterComponent, BorderLayout.CENTER);
			panelWithButtons.add(masterButtonPanel, BorderLayout.SOUTH);
			northComponent = panelWithButtons;
		}
		
		northLayeredPane = new LoadingInProgressOverlay(northComponent);
		return northLayeredPane;
	}
	
	/**
	 * The table is wrapped in a scroll pane and combined with a footer panel
	 * if that exists.
	 */
	private Component createMasterComponent()
	{
		scrollPane = new JScrollPane(table);
		footerPanel = createFooterPanel();
		if (footerPanel != null)
			return addFooter(scrollPane);
		else
			return scrollPane;				
	}
	
	/**
	 * Return a dummy command since a AbstractMasterDetailPanel do not
	 * allow changes so there can't be any dirty model to save.
	 * This method is overridden by the AbstractCrudPanel who do allow changes.
	 * 
	 */
	protected ChainableCommand createSaveChangesInDirtyModelCommand(boolean dischardChangesByRefresh)
	{
		return new NoOperationBackgroundCommand();
	}
	
	private Component addFooter(JScrollPane masterComponent)
	{
		JPanel tableWithFooter = new JPanel(new BorderLayout());
		tableWithFooter.add(masterComponent, BorderLayout.CENTER);
		tableWithFooter.add(footerPanel, BorderLayout.SOUTH);
		synchronizeFooterModel();	
		synchronizeFooterWidth(masterComponent);
		return tableWithFooter;		
	}
	
	/**
	 * Overridden by subclasses that adds a footer panel just below the 
	 * scrollable table.
	 */
	protected JComponent createFooterPanel()
	{
		return null;
	}
	
	/**
	 * The footer model contains sums of the selected rows and gets
	 * updated when the selection changes.
	 */
	private void synchronizeFooterModel()
	{
		tableModel.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e)
			{
				updateFooterModel(footerPanel);
				
			}});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateFooterModel(footerPanel);
			}
		});
	}
	
	/**
	 * Adjust a right side insets depending on the presence of the vertical scroll bar.
	 * This gives the footer a similar behavior as the header.
	 */
	private void synchronizeFooterWidth(final JScrollPane masterComponent)
	{
		masterComponent.getViewport().addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				if (masterComponent.getVerticalScrollBar().isVisible())
					footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, masterComponent.getVerticalScrollBar().getWidth() + 2));
				else
					footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));								
			}});
	}

	
	protected void updateFooterModel(JComponent footerPanel)
	{
		
	}
	
	/**
	 * Is only called once to avoid more than one detail panel instance
	 * which would jeopardize the disabling mechanism handled by the
	 * EnablePredicateAdapter.
	 */
	protected abstract DirtyPredicatePanel createDetailPanel();

	/**
	 * This method is called when useModalAddAction is chosen
	 * in the constructor. Subclasses should return a new fresh
	 * instance every time. Do not return the single instance that is returned in
	 * the getDetailPanel() method!
	 */
	protected DirtyPredicatePanel createDetailPanelForModalNewAction()
	{
		throw new UnsupportedOperationException("Should be overidden");
	}

	protected abstract ObservableListTableModel createTableModel(ObservableList list);
	
	/**
	 * This method is executed outside the AWT thread. Do not access any Swing components
	 */	
	protected ObservableIF saveNew(ObservableIF object) throws RemoteException
	{
		throw new UnsupportedOperationException("Should be overriden");
	}

	protected final List getListFromServer() throws RemoteException
	{
		return null;
	}

	public void setMasterList(ServerList masterList)
	{
		setModel(masterList);
		this.masterList = masterList;
		tableModel.setData(masterList);
	}
	
	protected void setFilterPanel(Component filterPanel)
	{
		this.filterPanel = filterPanel;
		add(filterPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Override this to register a listener to the filter that reloads the data
	 * by calling the refresh method. <br>
	 * This must be called after the default values has been injected in the filter
	 * to avoid unnecessary load of data.
	 */
	protected void addFilterObserver()
	{
	}


	/**
	 * Called in the beginning of a SaveAction. Subclasses returns a list
	 * of errors that is displayed as a list in a message dialog. The SaveAction action
	 * is terminated if the returned list contains any errors. 
	 */
	protected ValidationResult validate(Object object)
	{
		return new ValidationResult(null);
	}
	
	/**
	 * Called in the beginning of a SaveNewAction. Subclasses returns a list
	 * of errors that is displayed as a list in a message dialog. The SaveNewAction action
	 * is terminated if the returned list contains any errors. <br>
	 * Subclasses should override this if useModalAddAction is true and
	 * new objects has additional validation for example unique key constraints.
	 * You can put it the other way around and say if you have unique key constraints
	 * you must set useModalAddAction to true to get the possibility to check
	 * that the key is unique in this method. <br>
	 */
	protected ValidationResult validateNew(Object object)
	{
		return new ValidationResult(null);
	}

	/**
	 * Since there could be column sorting installed the row index are converted.
	 * @return a List with Object corresponding to the highlighted rows in the table.
	 */
	protected List<Object> getSelectedInTable()
	{
		List<Object> result = new ArrayList<Object>();
		int[] selection = table.getSelectedRows();
		if (masterList != null)
		{
			for (int i = 0; i < selection.length; i++)
			{
				try
				{
					int selectedRowIndex = table.convertRowIndexToModel(selection[i]);
					result.add(masterList.get(selectedRowIndex));
					// In case we end up here when table and observableList are not synchronized
				} catch (IndexOutOfBoundsException e)
				{
					return new ArrayList<Object>();
				}
			}
		}
		return result;	
	}

	protected void selectObjectsInTable(List<Object> previousSelectedObjects)
	{
		ListSelectionModel selectionModel = table.getSelectionModel();
		if (previousSelectedObjects.size() > 700)
		{
			selectionModel.clearSelection();
			return;
		}
		selectionModel.setValueIsAdjusting(true);
		selectionModel.clearSelection();
		if (previousSelectedObjects == null || masterList == null)
			return;
		
		int i = -1;
		for (Object previousSelectedObject : previousSelectedObjects)
		{
			i = masterList.indexOf(previousSelectedObject);
			if(i >= 0)
			{
				i = table.convertRowIndexToView(i);
				selectionModel.addSelectionInterval(i, i);
			}	
		}
		selectionModel.setValueIsAdjusting(false);

		if(i >= 0)
		{
			Rectangle rect = table.getCellRect(i, 0, true);
			table.scrollRectToVisible(rect);
		}
	}

	
	protected void selectObjectInTable(Object objectToSelect)
	{
		if (objectToSelect != null)
		{
			List<Object> objectsToSelect = new ArrayList<Object>();
			objectsToSelect.add(objectToSelect);
			selectObjectsInTable(objectsToSelect);
		}
	}
	

	/**
	 * Called by subclasses to install a popup menu that is displayed when the user right
	 * clicks in the table.
	 */
	protected void setContextMenu(JPopupMenu contextMenu)
	{
		installMouseListener();
		this.contextMenu = contextMenu;
	}
	
	private void createContextMenu()
	{
		contextMenu = new JPopupMenu();
		addContextMenuItems(contextMenu);
		if (contextMenu.getSubElements().length == 0)
			contextMenu = null;
		else
			installMouseListener();
	}

	/**
	 * Override this to add context menu items
	 */
	public void addContextMenuItems(JPopupMenu contextMenu)
	{
	}
		
	/**
	 * Called by subclasses to install a double click handler that is called when the user double
	 * clicks in the table. The table element under the mouse is supplied as argument in the
	 * double click handler call.
	 */
	protected void setDoubleClickHandler(DoubleClickHandler doubleClickHandler)
	{
		installMouseListener();
		this.doubleClickHandler = doubleClickHandler;
	}

	
	private void installMouseListener()
	{
		if (tableMouseListener == null)
		{
			tableMouseListener = new MouseAdapter()
							{
								public void mouseReleased(MouseEvent e)
								{
									if(e.getClickCount() == 2 && doubleClickHandler != null)
									{
										Object tableElementAtPoint = getTableElementAtPoint(e.getPoint());
										if (tableElementAtPoint != null)
											doubleClickHandler.handleDoubleClick(tableElementAtPoint);
										return;
									}
									if(e.isShiftDown() || e.isControlDown() || e.isAltDown())
										return;
									// Show popup menu
									if(contextMenu != null && e.getButton() == MouseEvent.BUTTON3)
										contextMenu.show(e.getComponent(), e.getX(), e.getY());
								}
							};
			// Add double-click and context-menu triggering listener.
	    table.addMouseListener(tableMouseListener);
	    scrollPane.addMouseListener(tableMouseListener);
		}

	}
	
	/**
	 * Called by subclasses to set the width (in percent of the table width) of a table column 
	 */
	protected void setRelativeTableColumnWidth(int columnIndex, int widthPercent)
	{
		this.addComponentListener(new ColumnResizerComponentListener(columnIndex, widthPercent));
	}
	
	/**
	 * ComponentListener that changes the width of a table column after resize
	 */
	private class ColumnResizerComponentListener extends ComponentAdapter 
	{
		private int columnIndex;
		private int widthPercent;
		boolean firstResize = true;
		
		public ColumnResizerComponentListener(int columnIndex, int width)
		{
			this.columnIndex = columnIndex;
			this.widthPercent = width;
		}
		
		@Override
		public void componentResized(ComponentEvent e)
		{
			if (firstResize) 
			{
				int panelWidth = AbstractMasterDetailPanel.this.getWidth();
				String name = table.getColumnName(columnIndex);
				TableColumn col = table.getColumn(name);
				col.setPreferredWidth((int)(panelWidth * ((float)widthPercent / 100f)));
				firstResize = false;
			}
		}
	}
	
	/**
	 * Return the list that is displayed in the table of the master detail user interface.
	 * The returned list is a ServerList a special list class that has subclasses that implements
	 * the specific database calls needed for retrieving the elements of the table.
	 */
	public ServerList getMasterList()
	{
		return masterList;
	};

	/**
	 * Return a model owner that is assigned with the model of the selected row in the table.
	 * Normally the detailPanel can be used for this matter but sometimes there is no detail panel.
	 * In that case we create an alternative model owner that can be returned.
	 */
	protected ModelOwnerIF getDetailModelOwner()
	{
		return detailPanel == null ? detailModelOwner : detailPanel;
	}


	private Object getTableElementAtPoint(Point point)
	{
		int rowAtPoint = table.rowAtPoint(point);
		if (rowAtPoint == -1)
			return null;
		else
			return masterList.get(table.convertRowIndexToModel(rowAtPoint));
	}
		
	protected final List<String> getLocalizedErrorMessages(ResourceBundle resourceBundle, List<String> errorKeys)
	{
		List<String> errors = new ArrayList<String>();
		for (String errorKey : errorKeys)
		{
			errors.add(resourceBundle.getString(errorKey));
		}
		return errors;
	}

	/**
	 * Abstract super class to factories that creates a MasterDetailPanel. The
	 * factory loads data from the database in a background thread by calling
	 * the refresh method
	 * 
	 */
	public abstract static class MasterDetailPanelFactory extends PanelFactory
	{
		public MasterDetailPanelFactory()
		{
			super();
		}
						
		@Override
		public final Component createPanel()
		{
			AbstractMasterDetailPanel masterDetailsPanel = createMasterDetailPanel();
			masterDetailsPanel.initialize();
			masterDetailsPanel.refresh();
			return masterDetailsPanel;
		}

		protected abstract AbstractMasterDetailPanel createMasterDetailPanel();
			

	}
		
	/**
	 * The injection of a model to the detail panel is not triggered by a menu item or a button 
	 * Instead the triggering is handled by an AssignModelToDetailPanelListener and the use of action in 
	 * this case is a little bit awkward. But we still want the fault barrier
	 * built in to the DefaultCommandAction so we have to live with until we separate Actions from
	 * the tasks that are performed by the actions.
	 */
	private class InjectModelInToDetailPanelCommand extends DefaultBackgroundCommand
	{		
		private ObservableIF model;
		
		public InjectModelInToDetailPanelCommand(ObservableIF model)
		{
			super();
			this.model = model;
		}
		
		@Override
		protected Object runInBackground() throws Exception
		{
			return null;
		}
		
		@Override
		protected void publishResult(Object result) throws Exception
		{
			getDetailModelOwner().setModel(model);
			modelInjectedInToDetailPanel(model);
		}
		
	}

	protected class RefreshAction extends DefaultAction
	{
		/**
		 * Refresh action has no text.
		 */
		public RefreshAction() 
		{
			super(null);
			setSmallIcon(UiUtil.getIcon("refresh.png", AbstractMasterDetailPanel.class));
		}
				
	
		@Override
		protected void execute(ActionEvent evt) throws Exception
		{
			new CommandChain(createSaveChangesInDirtyModelCommand(false), new RefreshCommand()).run();		
		}
  }
	
	/**
	 * Reloads all data from the server
	 *
	 */
	protected class RefreshCommand extends DefaultBackgroundCommand
	{
		private List<Object> selectedInTable;

		@Override
		public void prepare()
		{
			super.prepare();
			selectedInTable = getSelectedInTable();
			northLayeredPane.startLoadingInProgressFeedback();
		}

		
		@Override
		public Object runInBackground() throws Exception
		{
			if (masterList != null)
				masterList.readFromServer();
			return null;	
		}
		
		@Override
		protected void handleException(Exception e)
		{
			northLayeredPane.stopLoadingInProgressFeedback();			
			super.handleException(e);
		}
		
		@Override
		public void publishResult(Object result) throws Exception
		{
			northLayeredPane.stopLoadingInProgressFeedback();
			if (masterList != null)
				masterList.update();
			selectObjectsInTable(selectedInTable);
			postRefresh();
		}
		
	}

	/**
	 * Detects selection in the master table and injects the selected model in a
	 * model owner. Subclasses handle the cases where we have a detail panel or not.
	 */
	private abstract class MasterListSelectionListener implements ListSelectionListener
	{
		public MasterListSelectionListener()
		{
			super();
			table.getSelectionModel().addListSelectionListener(this);
		}
		
		protected ObservableIF getItemOfRow(int row)
		{
			return (ObservableIF) masterList.get(row);
		}
		
		@Override
		public void valueChanged(ListSelectionEvent ev)
		{
			if (ev.getValueIsAdjusting())
				return;

			ObservableIF newModel;
			if (table.getSelectedRowCount() == 1)
				newModel = getItemOfRow(table.convertRowIndexToModel(table.getSelectedRow()));
			else
				newModel = null;
			injectModel(newModel);
		}
		
		protected abstract void injectModel(ObservableIF newModel);		
	}

	/**
	 * The AssignModelToDetailPanelListener injects the selected model of the table
	 * into the detail panel.
	 */
	private class InjectModelInToDetailPanelListener extends MasterListSelectionListener
	{
		public InjectModelInToDetailPanelListener()
		{
			super();
		}
		
		@Override
		protected void injectModel(final ObservableIF newModel)
		{
			injectModelInToDetailPanel(newModel);		
		}	
	}
	
	/**
	 * The AssignModelToDetailPanelListener injects the selected model of the table
	 * into the detail model owner used when no detail panel is present.
	 */
	private class InjectModelInToDetailModelOwnerListener extends MasterListSelectionListener
	{
		public InjectModelInToDetailModelOwnerListener()
		{
			super();
		}
		
		@Override
		protected void injectModel(ObservableIF newModel)
		{
			detailModelOwner.setModel(newModel);			
			modelInjectedInToDetailPanel(newModel);
		}	
	}


	/**
	 * A detail panel with a null model is blocked from user interaction
	 * and dimmed by adding an extra layer in the layered pane. This class
	 * is only used when there is a detail panel.
	 */		
	private class DetailPanelBlocker implements ModelOwnerIF.Listener
	{
		@Override
		public void modelAssigned(ObservableIF oldModel, ObservableIF newModel)
		{
			if (newModel != null)
			{
				southLayeredPane.remove(southBlockingPanel);
				southLayeredPane.repaint();
			}
			else
			{
				southLayeredPane.add(southBlockingPanel, JLayeredPane.DRAG_LAYER);
				southLayeredPane.repaint();
			}					
		}
	}

	/**
	 * A MasterTable is used as the table in the upper part of the split pane in
	 * the master detail UI.
	 */
	private class MasterTable extends JTable
	{
		public MasterTable(TableModel tableModel)
		{
			super(tableModel);
			setRowSorter(new TableRowSorter<TableModel>(tableModel));
			getRowSorter().setSortKeys(Arrays.asList(new SortKey(0, SortOrder.ASCENDING)));		// Sort the first column as default		
			setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		}
		
		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}
		
		@Override
		/**
		 * Since we like to prevent selection change in some cases we can't use a ListSelectionListener
		 * or a TableSelectionAdaptor. If the user cancel the dialog that pops up from the SaveChangesIfUserWantsCommand
		 * no change of selection will take place. The selection just remains where it is. Same thing will happen
		 * if there is validation errors in the model that is about to be save.
		 */
		public void changeSelection(final int rowIndex, final int columnIndex, final boolean toggle, final boolean extend)
		{
			createSaveChangesInDirtyModelCommand(true).run(new BackgroundCommand.DoneListener(){

				@Override
				public void done()
				{
					changeSelectionContinue(rowIndex, columnIndex, toggle, extend);
					
				}});
		}
		
		/**
		 * To call super of an other method feel strange but I see no other way
		 */
		private void changeSelectionContinue(int rowIndex, int columnIndex, boolean toggle, boolean extend)
		{
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
		}

	}


	
}
