package dataTransfer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import queryGenerator.Query;

public class DataRetriever {

	private DBConnector con;
	private Query q;

	public DataRetriever(DBConnector con, Query q) {
		this.con = con;
		this.q = q;
	}

	public ResultSet getReult() throws SQLException {
		Statement stmt = con.getConnection().createStatement();
		ResultSet rs;
		System.out.println(q.toString());

		rs = stmt.executeQuery(q.toString());
		return rs;
	}
}
