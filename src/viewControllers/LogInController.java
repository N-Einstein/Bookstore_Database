package viewControllers;

import java.sql.ResultSet;

import dataBaseModel.UserValidator;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import view.HomeView;
import view.LogInView;
import view.RegisterView;
import view.ViewInterface;

public class LogInController implements ControllerInterface {
	private LogInView logInView;
	private UserValidator userValidator = UserValidator.getInstance();

	public LogInController(ViewInterface view) {
		this.logInView = (LogInView) view;
		logInView.setController(this);

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
		if (btn.getId().equals("reg")) {
			RegisterView regView = RegisterView.getInstance();
			RegisterController regController = new RegisterController(regView);
			Stage currentStage = logInView.getPrimaryStage();
			regView.buildView(currentStage);
		} else if (btn.getId().equals("log")) {

			HomeView homView = HomeView.getInstance();
			Stage currentStage = logInView.getPrimaryStage();
			HomeController homController = new HomeController(homView);
			homView.buildView(currentStage);
		}
	}

	public boolean validLogIn(String name, String pass) {
		return userValidator.userLogged(name, pass);
	}

	public String getError() {
		return userValidator.getExceptionError();
	}

}
