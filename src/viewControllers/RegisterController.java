package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import dataBaseModel.PasswordHandler;
import dataBaseModel.UserValidator;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import view.HomeView;
import view.LogInView;
import view.RegisterView;
import view.ViewInterface;

public class RegisterController implements ControllerInterface {
	private RegisterView regView;
	private UserValidator userValidator = UserValidator.getInstance();
	private PasswordHandler passwordHandler = PasswordHandler.getInstance();
	boolean password = false;

	public RegisterController(ViewInterface view) {
		this.regView = (RegisterView) view;
		regView.setController(this);
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
		if (btn.getId().equals("canc")) {
			LogInView logView = LogInView.getInstance();
			Stage currentStage = regView.getPrimaryStage();
			Scene otherScene = logView.getScene();
			currentStage.setScene(otherScene);
		} else if (btn.getId().equals("sub")) {
			HomeView homView = HomeView.getInstance();
			Stage currentStage = regView.getPrimaryStage();
			HomeController homController = new HomeController(homView);
			homView.buildView(currentStage);
		}
	}

	public boolean validRegistrationForm(String em) {
		boolean valid = true;
		if (!userValidator.validUserEmail(em)) {
			regView.showAlert(Alert.AlertType.ERROR, regView.getScene().getWindow(), "Form Error!", "Invalid email");
			valid = false;
		}
		return valid;
	}

	public boolean insertUser(LinkedHashMap<String, TextField> registerText, PasswordField passwordField,
			int userType) {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		for(Entry<String, TextField> rt : registerText.entrySet()){
			if(!rt.getValue().getText().equals("")){
				keys.add(rt.getKey());
				values.add(rt.getValue().getText());
			}
		}
		keys.add("Password");
		keys.add("Role");
		if(passwordField.getText().equals("")){
			password = false;
			return false;
		} else {
		values.add(passwordHandler.encrypt(passwordField.getText()));
		values.add(Integer.toString(userType));
		return userValidator.insertUser(keys, values);
		}
	}

	public String getError() {
		if (password) {
			return userValidator.getExceptionError();
		} else {
			password = true;
			return "ExceptionHandler.NullException:'Password doesn't have a default value";
		}
	}

}