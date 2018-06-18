package dataTransfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

	private Connection connection;
	private static final String USER = "root", PASSWORD = "12345678",
			DATABASE = "jdbc:mysql://localhost:3306/bookstore_database", DRIVER = "com.mysql.jdbc.Driver";

	public DBConnector() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		connection = DriverManager.getConnection(DATABASE, USER, PASSWORD);
	}

	public Connection getConnection() {
		return connection;
	}

	public void close() throws SQLException {
		connection.close();
	}
}
