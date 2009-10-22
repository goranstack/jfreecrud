package se.bluebrim.crud.client;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import nu.esox.gui.aspect.ModelPredicateProxy;
import nu.esox.util.OrPredicate;
import nu.esox.util.Predicate;
import nu.esox.util.PredicateIF;
import se.bluebrim.crud.esox.DirtyPredicateModel;

/**
 * Abstract super class to panels that has a Save action that indicates unsaved
 * changes. This class contains a mechanism that makes it possible to enable the
 * Save action at the first key stroke in a text component. The reason for that is the fact
 * that although the model isn't affected of the initial keystrokes the entered text
 * will get saved if the user trigger the save action directly after entered some
 * text in a text field. Therefore its not sufficient for the Save action to only
 * listen at the model. It also has to listen to a predicate that in this class
 * that gets true as soon as the user enters some characters in one of the text components. <br>
 * Non text components for example a combobox updates the model immediately so they do not need
 * any special arrangement.
 * 
 * @author GStack
 *
 */
public abstract class DirtyPredicatePanel extends AbstractPanel implements DirtyPredicateProvider
{
	private Predicate panelDirtyPredicate;
	private PredicateIF modelDirtyPredicate;
	private JTextComponent focusOwner;

	public DirtyPredicatePanel()
	{
		modelDirtyPredicate = new ModelPredicateProxy(this, DirtyPredicateModel.class, "getDirtyPredicate");
		panelDirtyPredicate = new Predicate(false);
	}

	/* (non-Javadoc)
	 * @see se.bluebrim.crud.client.DirtyPredicateProvider#initDirtyPredicatePanel()
	 */
	public void initDirtyPredicatePanel()
	{
		registerKeyListenerOnEachTextComponent();
	}
	
	/**
	 * Register a KeyListener and a FocusListener at each text component. The KeyListener
	 * sets the panelDirtyPredicate to true when text is entered in the text component.
	 * and the FocusListener sets the panelDirtyPredicate to false when the focus is lost because
	 * then the model gets dirty and that will enable the Save button. We have to reset the 
	 * panelDirtyPredicate sooner or later anyhow. Lets do it as soon as possible.<br>
	 * We must clean the dirty flag in an invoke later call otherwise the save button will receive
	 * a disable call before handling the save action. 
	 * 
	 * TODO: Handle JEditorPane and JTextArea as well
	 */
	private void registerKeyListenerOnEachTextComponent()
	{
		eachComponent(new ComponentVisitor(){

			public void visit(final Component component)
			{
				if (component instanceof JTextComponent)
				{
					component.addKeyListener(new KeyAdapter()
					{
						public void keyTyped(KeyEvent e)
						{
							DirtyPredicatePanel dirtyPredicatePanel = getDirtyPredicatePanel(component);
							if (dirtyPredicatePanel != null)
								dirtyPredicatePanel.setDirty();
						}
					});
					component.addFocusListener(new FocusAdapter(){
						
						@Override
						public void focusGained(FocusEvent e)
						{
							focusOwner = (JTextComponent)component;
						}
						
						@Override
						/**
						 * The behavior must match <code>TextFieldFocusHandler</code>. For example when
						 * editing is on going in a text field and the user selects a menu this method is
						 * called but the FocusEvent is temporary. 
						 */
						public void focusLost(FocusEvent e)
						{
							if (e.isTemporary())
								return;
							final DirtyPredicatePanel dirtyPredicatePanel = getDirtyPredicatePanel(component);
							if (dirtyPredicatePanel != null)
							{
								SwingUtilities.invokeLater(new Runnable(){

									@Override
									public void run()
									{
										dirtyPredicatePanel.cleanDirty();										
									}});
							}
								
						}
					});
				}
			}
			
			/**
			 * Find the DirtyPredicatePanel in the
			 * parent chain of the specified component.
			 */
			private DirtyPredicatePanel getDirtyPredicatePanel(Component component)
			{
				if (component instanceof DirtyPredicatePanel)
				{
					DirtyPredicatePanel dirtyPredicatePanel = (DirtyPredicatePanel) component;
					if (dirtyPredicatePanel.panelDirtyPredicate != null)
						return dirtyPredicatePanel;
				}
				if (component.getParent() != null)
					return getDirtyPredicatePanel(component.getParent());
				else
					return null;
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see se.bluebrim.crud.client.DirtyPredicateProvider#getDirtyPredicate()
	 */
	public Predicate getDirtyPredicate()
	{
		return new OrPredicate(panelDirtyPredicate, modelDirtyPredicate);
	}

	private void setDirty()
	{
		panelDirtyPredicate.set(true);
	}
	
	/* (non-Javadoc)
	 * @see se.bluebrim.crud.client.DirtyPredicateProvider#cleanDirty()
	 */
	public void cleanDirty()
	{
		panelDirtyPredicate.set(false);		
	}
	
	/* (non-Javadoc)
	 * @see se.bluebrim.crud.client.DirtyPredicateProvider#isDirty()
	 */
	public boolean isDirty()
	{
		return panelDirtyPredicate.isTrue() || modelDirtyPredicate.isTrue();
	}
	
	
	/* (non-Javadoc)
	 * @see se.bluebrim.crud.client.DirtyPredicateProvider#updateModelWithOnGoingTextEditing()
	 */
	public void updateModelWithOnGoingTextEditing()
	{
		if (focusOwner != null && focusOwner.isFocusOwner())
			focusOwner.transferFocus();
	}
	
}
