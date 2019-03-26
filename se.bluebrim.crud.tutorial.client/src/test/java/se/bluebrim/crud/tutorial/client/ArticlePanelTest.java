package se.bluebrim.crud.tutorial.client;

import java.util.List;

import org.junit.Test;

import se.bluebrim.crud.client.AbstractPanel;
import se.bluebrim.crud.client.AbstractPanelTest;
import se.bluebrim.crud.tutorial.Artist;

public class ArticlePanelTest extends AbstractPanelTest
{
	public ArticlePanelTest()
	{
	}

	public static void main(String[] args)
	{
		new ArticlePanelTest().testCreateGui();
	}
	
	@Test	
	public void testCreateGui()
	{
		List<AbstractPanel> panels = openTestWindow(ArtistPanel.class);
		Artist artist = createArtist();
		((ArtistPanel)panels.get(0)).setModel(artist);
		((ArtistPanel)panels.get(1)).setModel(artist);		
	}


	private Artist createArtist()
	{
		Artist artist = new Artist(-1, "Miles Davis", "Famous jazz musician");
		return artist;
	}

	
}
