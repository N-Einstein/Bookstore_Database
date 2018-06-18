package view;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.LogInController;

public class LogInView implements ViewInterface {
	private LogInController controller;
	private LinkedHashMap<String, Label> logInlabels = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, TextField> logInText = new LinkedHashMap<String, TextField>();
	private LinkedHashMap<String, Button> logInBtn = new LinkedHashMap<String, Button> ();
    private PasswordField passwordField;
	private Stage primaryStage;
	private GridPane gridPane;
	private Scene logInScn;
	private static LogInView instance = new LogInView();

	private LogInView() {
	}

	public static LogInView getInstance() {
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		createLabels();
		createTexts();
		createButtons();
		passwordField = new PasswordField();
		passwordField.getStyleClass().add("input");
		createLogInFormPane();
		addViewsToPane(primaryStage);
		logInScn.getStylesheets().add("file:resources/stylesheets/forms.css");
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (LogInController) controller;
	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.logInScn;
	}

	public void addViewsToPane(Stage primaryStage) {
		int j = 1;
		for (Entry<String, Label> l : logInlabels.entrySet()) {
			gridPane.add(l.getValue(), 0, j++);
			l.getValue().getStyleClass().add("label");
		}
		j = 1;
		for (Entry<String, TextField> t : logInText.entrySet()) {
			t.getValue().setPrefHeight(40);
			gridPane.add(t.getValue(), 1, j++);
			t.getValue().getStyleClass().add("input");
		}
		j = 0;
		for (Entry<String, Button> b : logInBtn.entrySet()) {
			b.getValue().setPrefHeight(40);
			b.getValue().setDefaultButton(true);
			b.getValue().setPrefWidth(100);
			setButtonAction(b.getValue());
			gridPane.add(b.getValue(), 0, logInlabels.size()+1 + j++, 2, 1);
			GridPane.setHalignment(b.getValue(), HPos.CENTER);
			GridPane.setMargin(b.getValue(), new Insets(20, 0, 20, 0));
			b.getValue().getStyleClass().add("btn");
		}
		
		passwordField.setPrefHeight(40);
		gridPane.add(passwordField, 1, logInlabels.size());
		logInScn = new Scene(gridPane, 500, 500);
		primaryStage.setScene(logInScn);
		this.primaryStage = primaryStage;

	}

	private void createLabels() {
		logInlabels.put(TablesColsNames.USER_USERNAME, new Label("User Name : "));
		logInlabels.put(TablesColsNames.USER_PASSWORD, new Label("Password : "));
	}

	private void createTexts() {
		logInText.put(TablesColsNames.USER_USERNAME, new TextField());
	}

	private void createButtons() {
		logInBtn.put("LogIn", new Button("Log In"));
		logInBtn.put("Register", new Button("Register"));
		logInBtn.get("LogIn").setId("log");
		logInBtn.get("Register").setId("reg");
	}

	private void createLogInFormPane() {
		gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setPadding(new Insets(40, 40, 40, 40));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
		columnOneConstraints.setHalignment(HPos.RIGHT);
		ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
		columnTwoConstrains.setHgrow(Priority.ALWAYS);
		gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
	}

	private void setButtonAction(Button btn) {
		btn.setOnMouseClicked(e -> {
			if (btn.getId().equals("log"))
				if(controller.validLogIn(logInText.get(TablesColsNames.USER_USERNAME).getText(), passwordField.getText())){
				controller.switchView(btn);
				}else{
    	 			showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", controller.getError());
			} else if (btn.getId().equals("reg"))
				controller.switchView(btn);
		});
	}

	@Override
	public void setUpdated(boolean updated) {
		
	}

	
	public void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(owner);
		alert.show();
	}

}
