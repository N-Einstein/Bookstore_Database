package dataBaseModel;

import java.text.DecimalFormat;
import java.util.HashMap;

import sqlResultsParsers.TablesColsNames;

public class User {
	int userRole;
	int id;
	public static HashMap<String, Integer> cartISBN;
	private HashMap<String, String> userData;
	public static DecimalFormat decimarFormatter = new DecimalFormat("#.00");
	public boolean managerInsertAccess = false;
	private static User user = new User();

	private User() {
	}

	public static User getInstance() {
		return user;
	}

	public int getId() {
		return this.id;
	}

	public int getRole() {
		return this.userRole;
	}

	public void setCurrentUser(HashMap<String, String> data) {
		userData = data;
		id = Integer.parseInt((userData.get(TablesColsNames.USER_ID)));
		userRole = Integer.parseInt(userData.get(TablesColsNames.USER_ROLE));
		cartISBN = new HashMap<String, Integer>();
	}

	public HashMap<String, String> getCurrentUser() {
		return userData;
	}

}
