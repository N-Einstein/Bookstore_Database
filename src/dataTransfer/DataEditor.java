package dataTransfer;

import java.sql.SQLException;
import java.sql.Statement;

import dataTransfer.DBConnector;
import queryGenerator.Query;

public class DataEditor {

	private DBConnector con;
	private Query q;

	public DataEditor(DBConnector con, Query q) {
		this.con = con;
		this.q = q;
	}

	public void updateDatabase() throws SQLException {
		Statement stmt = con.getConnection().createStatement();
		System.out.println(q.toString());
		stmt.executeUpdate(q.toString(), Statement.RETURN_GENERATED_KEYS);
	}
}
