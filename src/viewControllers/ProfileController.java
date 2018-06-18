package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import dataBaseModel.PasswordHandler;
import dataBaseModel.User;
import dataBaseModel.UserValidator;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.HomeView;
import view.ProfileView;
import view.ViewInterface;

public class ProfileController implements ControllerInterface {

	private ProfileView profView;
	private User user = User.getInstance();
	private UserValidator userValidator = UserValidator.getInstance();
    private PasswordHandler passwordHandler = PasswordHandler.getInstance();
	public ProfileController(ViewInterface view) {
		this.profView = (ProfileView) view;
		profView.setController(this);
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
		HomeView homView = HomeView.getInstance();
		Stage currentStage = profView.getPrimaryStage();
		homView.buildView(currentStage);
	}
	public User getUser() {
		return this.user;
	}
	public boolean updateUser(LinkedHashMap<String, TextField> valuesText, PasswordField passwordField) {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		for(Entry<String, TextField> vt : valuesText.entrySet()){
			if(!vt.getValue().getText().equals("")){
				keys.add(vt.getKey());
				values.add(vt.getValue().getText());
			}
		}
		if(!passwordField.getText().equals("")){
		    keys.add("Password");
		    values.add(passwordHandler.encrypt(passwordField.getText()));
		}
		if (keys.size() != 0) {
			return userValidator.updateUser(keys, values, TablesColsNames.USER_USERNAME, user.getCurrentUser().get(TablesColsNames.USER_USERNAME), true);
		}
		return true;
	}

	public boolean validUpdateForm(String em) {
		boolean valid = true;
		if (!userValidator.validUserEmail(em) && !em.isEmpty()) {
			 profView.showAlert(Alert.AlertType.ERROR,
			 profView.getScene().getWindow(), "Form Error!", "Invalid email");
			valid = false;
		}
		return valid;
	}
	public String getError(){
		return userValidator.getExceptionError();
				
	}

}
