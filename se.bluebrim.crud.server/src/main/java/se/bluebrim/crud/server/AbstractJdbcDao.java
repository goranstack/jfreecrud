package se.bluebrim.crud.server;

import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Abstract base class for all DAO (Data Access Objects) using Spring JdbcTemplate <br>
 * The reason for using Spring JDBC layer is to avoid the complexity of a large OMR framework
 * (Hibernate). But since the mapper in Spring only handles read operation there is room for
 * some more declarative approach for the object RDBMS mapping. Have a look at: 
 * http://www.garret.ru/Jora-1.02/ReadMe.html
 * 
 * @author OPalsson
 *
 */
public abstract class AbstractJdbcDao 
{
	/**
	 * The normal jdbcTemplate with limits on the maximum rows
	 */
	protected SimpleJdbcTemplate jdbcTemplate;

	/**
	 * A jdbcTemplate without row limitation. 
	 * This one should be used with care, mainly for server side data manipulation
	 */
	protected SimpleJdbcTemplate jdbcTemplateUnlimitedRows;
	
	protected SimpleDateFormat jdbcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setDataSource(DataSource dataSource) 
	{
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		JdbcTemplate fullJdbcTemplate = (JdbcTemplate) jdbcTemplate.getJdbcOperations();
		fullJdbcTemplate.setMaxRows(getMaxNumberOfRowsFromDb());
		
		jdbcTemplateUnlimitedRows = new SimpleJdbcTemplate(dataSource);
	}

	protected abstract int getMaxNumberOfRowsFromDb();

}
