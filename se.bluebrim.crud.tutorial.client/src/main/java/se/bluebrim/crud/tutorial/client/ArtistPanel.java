package se.bluebrim.crud.tutorial.client;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import nu.esox.gui.TextFieldFocusHandler;
import nu.esox.gui.aspect.TextFieldAdapter;
import se.bluebrim.crud.client.DirtyPredicatePanel;
import se.bluebrim.crud.client.FormPanel;
import se.bluebrim.crud.client.esox.TextPaneAdapter;
import se.bluebrim.crud.tutorial.Artist;

@SuppressWarnings("serial")
public class ArtistPanel extends DirtyPredicatePanel
{
	private final static ResourceBundle bundle = ResourceBundle.getBundle("se.bluebrim.crud.tutorial.client.Client");
	
	private JTextField name;
	private JTextPane biography;
	
	public ArtistPanel()
	{
		initComponents();
		arrangeLayout();
		bindComponents();
		setRenderers();		
	}

	private void initComponents()
	{
		name= createInputLimitedTextField(50);
		biography = createInputLimitedTextPane(1000);	
	}

	private void arrangeLayout()
	{
		setLayout(new BorderLayout());
		FormPanel form = new FormPanel();
		form.addFormRow(new JLabel(bundle.getString("artist.name")), name);
		form.addFormRow(new JLabel(bundle.getString("artist.biography")), biography);
		form.adjustLabelWidthsToLargest();
		add(form, BorderLayout.CENTER);
	}

	private void bindComponents()
	{
		new TextFieldAdapter(name, this, Artist.class, "getName", "setName", String.class, null);
		TextFieldFocusHandler.add( name );
		new TextPaneAdapter(biography, this, Artist.class, "getBiography", "setBiography", String.class);	
	}

	private void setRenderers()
	{
		// TODO Auto-generated method stub		
	}

}
