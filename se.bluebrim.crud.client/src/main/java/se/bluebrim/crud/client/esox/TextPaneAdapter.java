package se.bluebrim.crud.client.esox;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextPane;

import nu.esox.gui.aspect.AbstractAdapter;
import nu.esox.gui.aspect.ModelOwnerIF;

/**
 * 
 * @author GStack
 *
 */
public class TextPaneAdapter extends AbstractAdapter implements FocusListener
{
    private final JTextPane m_textPane;

    
    public TextPaneAdapter( JTextPane textPane, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName )
    {
        this( textPane, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, String.class, null, "", "" );
    }
    
    public TextPaneAdapter( JTextPane textPane, ModelOwnerIF modelOwner, Class modelClass, String getAspectMethodName, String setAspectMethodName, Class aspectClass )
    {
        this( textPane, modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, null, "", "" );
    }
    
    public TextPaneAdapter( JTextPane textPane,
                             ModelOwnerIF modelOwner,
                             Class modelClass,
                             String getAspectMethodName,
                             String setAspectMethodName,
                             Class aspectClass,
                             String aspectName,                            
                             String nullValue,
                             String undefinedValue )
    {
        super( modelOwner, modelClass, getAspectMethodName, setAspectMethodName, aspectClass, aspectName, nullValue, undefinedValue );

        assert nullValue != null;
        assert undefinedValue != null;

        m_textPane = textPane;
        m_textPane.addFocusListener( this );
        m_textPane.setEditable( setAspectMethodName != null );
        update();
    }
               
    protected void update( Object projectedValue )
    {
        m_textPane.setText( projectedValue.toString() );
    }

		public void focusGained(FocusEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		public void focusLost(FocusEvent e)
		{
      setAspectValue( m_textPane.getText() );		
		}
}
