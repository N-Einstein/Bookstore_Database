package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dataBaseModel.UserRole;
import dataBaseModel.UserValidator;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.HomeView;
import view.UserListView;
import view.ViewInterface;

public class UserListController implements ControllerInterface  {
	private UserValidator userValidator = UserValidator.getInstance();
    private UserListView userListView = UserListView.getInstance();
    
	public UserListController(ViewInterface view) {
		this.userListView = (UserListView) view;
		userListView.setController(this);		
	}

	@Override
	public SQLResultParser getParser() {
		return null;
	}

	@Override
	public ResultSet getResultSet() {
		return null;
	}

	@Override
	public void switchView(Button btn) {
		
	}
	public ArrayList<User> getLazyLoadList(String attribute, String field, int limit, int offset, boolean selectAll){
		ArrayList<User> userList = new ArrayList<User>();
		ArrayList<HashMap<String, String>> mapList;
		if(selectAll){
		 mapList = userValidator.selectAllUsers(limit, offset);
		}else{
			mapList = userValidator.selectUser(field, attribute, limit, offset);
		}
		for(int i = 0; i < mapList.size() ;i++){
			User user = creatUser(mapList.get(i));
			userList.add(user);
		}
		return userList;
	}

	public User creatUser(HashMap<String, String> map){
		String s0 = map.get(TablesColsNames.USER_ID);
		String s1 = map.get(TablesColsNames.USER_USERNAME);
		String s2 = map.get(TablesColsNames.USER_USER_FNAME);
		String s3 = map.get(TablesColsNames.USER_USER_LNAME);
		String s4 = map.get(TablesColsNames.USER_EMAIL);
		String s5 = map.get(TablesColsNames.USER_ADDRESS);
		String s6 = map.get(TablesColsNames.USER_PHONE);
		String s7 = map.get(TablesColsNames.USER_ROLE);
		return new User(s0,s1,s2,s3,s4,s5,s6,s7);
		
	}
	public void preamtUser(User user){
		String userName = user.getUsername();
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(TablesColsNames.USER_ROLE));
		ArrayList<String> values = new ArrayList<String>(Arrays.asList(Integer.toString(UserRole.PROMOTED.ordinal()))); 
		userValidator.updateUser(keys, values, TablesColsNames.USER_USERNAME, userName, false);
	}
	public void goHome(){
		HomeView homeView = HomeView.getInstance();
		Stage currentStage = userListView.getPrimaryStage();
		homeView.buildView(currentStage);
	}
	public static class User {
        private final SimpleStringProperty id;
        private final SimpleStringProperty fname;
        private final SimpleStringProperty lname;
        private final SimpleStringProperty email;
        private final SimpleStringProperty username;
        private final SimpleStringProperty address;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty role;

 
        public User(String id, String username, String fname, String lname, String email,String address, String phone, String role) {
            this.id = new SimpleStringProperty(id);
        	this.fname = new SimpleStringProperty(fname);
            this.lname = new SimpleStringProperty(lname);
            this.email = new SimpleStringProperty(email);
            this.address = new SimpleStringProperty(address);
            this.phone  = new SimpleStringProperty(phone);
            this.role  = new SimpleStringProperty(role);
            this.username  = new SimpleStringProperty(username);

        }
 
        public String getFname() {
            return fname.get();
        }
        public String getId() {
            return id.get();
        }
 
        public String getLname() {
            return lname.get();
        }
 
        public String getEmail() {
            return email.get();
        }
        public String getAddress() {
            return address.get();
        }
 
        public String getPhone() {
            return phone.get();
        }
 
        public String getRole() {
            return role.get();
        }
        public String getUsername() {
            return username.get();
        }
 
 
    }

}
