package sqlResultsParsers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLResultParser implements SQLResultParserInterface {

	private ResultSet resultSet;
	private HashMap<String, Integer> cols;

	public void setupParser(ResultSet resultSet) {
		this.resultSet = resultSet;
		cols = new HashMap<String, Integer>();
		mapColsNames();

	}

	private void mapColsNames() {
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			int noOfCols = metaData.getColumnCount();
			for (int i = 1; i <= noOfCols; i++) {
				cols.put(metaData.getColumnName(i), i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getResult(String colName, ResultSet resultSet) {
		try {
			int colNo = cols.get(colName);
			return resultSet.getString(colNo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
