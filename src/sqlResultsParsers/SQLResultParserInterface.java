package sqlResultsParsers;

import java.sql.ResultSet;

public interface SQLResultParserInterface {
	
	/*
	 * called once on new result set (After new queries)
	 */
	public void setupParser(ResultSet resultSet);
	
	/*
	 * result set is one row that you want to find the value
	 * corresponding to the column name in it.
	 */
	public String getResult(String colName, ResultSet resultSet);

}
