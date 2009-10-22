package se.bluebrim.crud.tutorial.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import se.bluebrim.crud.server.AbstractJdbcDao;
import se.bluebrim.crud.tutorial.Artist;
import se.bluebrim.crud.tutorial.remote.ArtistDao;

public class ArtistDaoImpl extends AbstractJdbcDao implements ArtistDao
{
	
	private ParameterizedRowMapper<Artist> artistMapper = new ParameterizedRowMapper<Artist>() 
	{
		@Override
		@Transactional(rollbackFor=Exception.class)
		public Artist mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			Artist artist = new Artist(
				      rs.getInt("Id"),
				      rs.getString("Name"),
				      rs.getString("Biography")				      			      
				      );
			return artist;
		}
	};

	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<Artist> getArtists()
	{
		String sql = "SELECT * FROM Artist";		
		return jdbcTemplate.query(sql, artistMapper);		
	}
		
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int addArtist(Artist artist)
	{
		int artistId = 0;
		String sql = "INSERT INTO Artist" + 
		" (Name, Biography)" + 
		"  VALUES(?, ?) ";		
		jdbcTemplate.update(sql, artist.getName(),
										artist.getBiography()
										);
		//Now get the maximum value of the Id
		sql = "SELECT MAX(Id) FROM Artist";
		artistId =  jdbcTemplate.queryForInt(sql);
		return artistId;
	}
		
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void changeArtist(Artist artist)
	{
		String sql = "UPDATE Artist " +
		             " SET Name = ?, Biography = ?" +
		             " WHERE  Id = ?  ";	
		jdbcTemplate.update(sql, artist.getName(),
				artist.getBiography()
				);		
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void removeArtist(int artistId)
	{
		String sql = "DELETE FROM Artist WHERE  Id = ?";
		jdbcTemplate.update(sql, artistId);
	}

	@Override
	protected int getMaxNumberOfRowsFromDb()
	{
		return 20000;
	}
}
