package dataBaseModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ExceptionHandler.ExceptionHandler;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import dataTransfer.DataRetriever;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import queryGenerator.UpdateQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;

public class UserValidator {
	private static UserValidator userValidator = new UserValidator();
	private User user = User.getInstance();
	private SQLResultParser parser = new SQLResultParser();
	private ExceptionHandler excepHandler = ExceptionHandler.getHandler();
	private PasswordHandler passwordHandler = PasswordHandler.getInstance();
	private String exceptionError = "";

	private UserValidator() {

	}

	public static UserValidator getInstance() {
		return userValidator;
	}

	public boolean validUserEmail(String email) {
		final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		return matcher.find();
	}

	public ArrayList<HashMap<String, String>> selectUser(String attribute, String Field, int limit, int offset) {
		ArrayList<HashMap<String, String>> userData = null;
		SelectQuery selquery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		selquery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.USER)));
		selquery.addConditions(new ArrayList<String>(Arrays.asList(Field + " = " + "'" + attribute + "'")));
		selquery.setLimit(Integer.toString(limit));
		selquery.setOffset(Integer.toString(offset));
		selquery.build();
		ResultSet resultSet = null;
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, selquery);
			resultSet = dr.getReult();
			userData = parseResultSet(resultSet);
			con.close();
		} catch (Exception e) {
		}
		return userData;
	}

	public ArrayList<HashMap<String, String>> selectAllUsers(int limit, int offset) {
		ArrayList<HashMap<String, String>> userData = null;
		SelectQuery selquery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		selquery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.USER)));
		selquery.addConditions(new ArrayList<String>());
		selquery.setLimit(Integer.toString(limit));
		selquery.setOffset(Integer.toString(offset));
		selquery.build();
		ResultSet resultSet = null;
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, selquery);
			resultSet = dr.getReult();
			userData = parseResultSet(resultSet);
			con.close();
		} catch (Exception e) {
		}
		return userData;
	}

	public boolean userLogged(String name, String pass) {
		boolean valid = false;
		ArrayList<HashMap<String, String>> userData = selectUser(name, TablesColsNames.USER_USERNAME, 1, 0);
		if (!userData.isEmpty()) {
			String encpasssword = userData.get(0).get(TablesColsNames.USER_PASSWORD);

			if (passwordHandler.compare(pass, encpasssword)) {
				cacheUserData(userData);
				valid = true;
			} else {
				exceptionError = "Not Valid User !";
			}
		} else {
			exceptionError = "Not Valid User !";
		}

		return valid;
	}

	public boolean insertUser(ArrayList<String> keys, ArrayList<String> values) {
		boolean valid = false;
		InsertQuery insertQuery = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		insertQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.USER)));
		ArrayList<String> vals = updateValuesFormat(values);
		insertQuery.addRecord(keys, vals);
		insertQuery.build();
		DBConnector con = null;
		try {
			con = new DBConnector();
			DataEditor dr = new DataEditor(con, insertQuery);
			dr.updateDatabase();
			valid = true;
		} catch (Exception e) {
			try {
				excepHandler.handle(e);
			} catch (Exception e2) {
				exceptionError = e2.toString();
			}
		} finally {
			try {
				con.close();
			} catch (SQLException e) {

			}

		}
		if (valid) {
			ArrayList<HashMap<String, String>> userData = selectUser(
					values.get(keys.indexOf(TablesColsNames.USER_USERNAME)), TablesColsNames.USER_USERNAME, 1, 0);
			cacheUserData(userData);
		}
		return valid;
	}

	public boolean updateUser(ArrayList<String> keys, ArrayList<String> values, String field, String attribute,
			boolean profileMode) {
		boolean valid = false;
		Map<String, String> m = new HashMap<>();
		ArrayList<String> vals = updateValuesFormat(values);
		for (int i = 0; i < keys.size(); i++)
			m.put(keys.get(i), vals.get(i));

		UpdateQuery updateQuery = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
		updateQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.USER)));
		updateQuery.addConditions(new ArrayList<String>(Arrays.asList(field + " = " + "'" + attribute + "'")));
		updateQuery.addUpdates(m);
		updateQuery.build();
		DBConnector con = null;
		try {
			con = new DBConnector();
			DataEditor dr = new DataEditor(con, updateQuery);
			dr.updateDatabase();
			valid = true;
		} catch (Exception e) {
			try {
				excepHandler.handle(e);
			} catch (Exception e2) {
				exceptionError = e2.toString();
			}
		} finally {
			try {
				con.close();
			} catch (SQLException e) {

			}

		}
		if (valid && profileMode) {
			updateUserData(keys, values);
		}
		return valid;
	}

	private ArrayList<String> updateValuesFormat(ArrayList<String> values) {
		ArrayList<String> v = new ArrayList<String>();
		for (String vv : values) {
			StringBuilder s = new StringBuilder();
			s.append("'").append(vv).append("'");
			v.add(s.toString());
		}
		return v;
	}

	private void cacheUserData(ArrayList<HashMap<String, String>> userData) {
		user.setCurrentUser(userData.get(0));
	}

	private void updateUserData(ArrayList<String> keys, ArrayList<String> values) {
		HashMap<String, String> userData = user.getCurrentUser();
		for (int i = 0; i < keys.size(); i++) {
			userData.put(keys.get(i), values.get(i));
		}
	}

	private ArrayList<HashMap<String, String>> parseResultSet(ResultSet rs) {
		ArrayList<HashMap<String, String>> userDataList = new ArrayList<HashMap<String, String>>();
		parser.setupParser(rs);
		try {
			while (rs.next()) {
				HashMap<String, String> userData = new HashMap<String, String>();
				userData.put(TablesColsNames.USER_PASSWORD, parser.getResult(TablesColsNames.USER_PASSWORD, rs));
				userData.put(TablesColsNames.USER_EMAIL, parser.getResult(TablesColsNames.USER_EMAIL, rs));
				userData.put(TablesColsNames.USER_ADDRESS, parser.getResult(TablesColsNames.USER_ADDRESS, rs));
				userData.put(TablesColsNames.USER_ID, parser.getResult(TablesColsNames.USER_ID, rs));
				userData.put(TablesColsNames.USER_PHONE, parser.getResult(TablesColsNames.USER_PHONE, rs));
				userData.put(TablesColsNames.USER_ROLE, parser.getResult(TablesColsNames.USER_ROLE, rs));
				userData.put(TablesColsNames.USER_USER_FNAME, parser.getResult(TablesColsNames.USER_USER_FNAME, rs));
				userData.put(TablesColsNames.USER_USER_LNAME, parser.getResult(TablesColsNames.USER_USER_LNAME, rs));
				userData.put(TablesColsNames.USER_USERNAME, parser.getResult(TablesColsNames.USER_USERNAME, rs));
				userData.put(TablesColsNames.USER_PASSWORD, parser.getResult(TablesColsNames.USER_PASSWORD, rs));
				userDataList.add(userData);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userDataList;
	}

	public String getExceptionError() {
		return exceptionError;
	}
}