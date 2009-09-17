package se.bluebrim.crud.tutorial.remote;

import java.util.List;

import se.bluebrim.crud.tutorial.Artist;

public interface ArtistDao
{
	public List<Artist> getArtists();
	public int addArtist(Artist artist);
	public void changeArtist(Artist artist);
	public void removeArtist(int artistId);
}
