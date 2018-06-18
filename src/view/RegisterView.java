package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
//import javafx.scene.control.Alert;
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
import viewControllers.RegisterController;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class RegisterView implements ViewInterface {
	private RegisterController controller;
	private LinkedHashMap<String, Label> registerlabels = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, TextField> registerText = new LinkedHashMap<String, TextField>();
	private LinkedHashMap<String, Button> registerBtn = new LinkedHashMap<String, Button> ();
	private PasswordField passwordField;
	private Stage primaryStage;
	private GridPane gridPane;
	private Scene regScn;

	private static RegisterView instance = new RegisterView();

	private RegisterView() {
	}

	public static RegisterView getInstance() {
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		createLabels();
		createTexts();
		passwordField = new PasswordField();
		passwordField.getStyleClass().add("input");
		createButtons();
		createLogInFormPane();
		addViewsToPane(primaryStage);
		regScn.getStylesheets().add("file:resources/stylesheets/forms.css");
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (RegisterController) controller;

	}
	
	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.regScn;
	}

	public void addViewsToPane(Stage primaryStage) {
		int i = 1;
		for (Entry<String, Label> l : registerlabels.entrySet()) {
			gridPane.add(l.getValue(), 0, i++);
			l.getValue().getStyleClass().add("label");
		}
		i = 1;
		for (Entry<String, TextField> t : registerText.entrySet()) {
			t.getValue().setPrefHeight(40);
			gridPane.add(t.getValue(), 1, i++);
			t.getValue().getStyleClass().add("input");
		}
		passwordField.setPrefHeight(40);
		gridPane.add(passwordField, 1, registerlabels.size());
		i = 0;
		for (Entry<String, Button> b : registerBtn.entrySet()) {
			b.getValue().setPrefHeight(40);
			b.getValue().setDefaultButton(true);
			b.getValue().setPrefWidth(100);
			setButtonAction(b.getValue());
			gridPane.add(b.getValue(), 0, registerlabels.size()+1 + i++, 2, 1);
			GridPane.setHalignment(b.getValue(), HPos.CENTER);
			GridPane.setMargin(b.getValue(), new Insets(20, 0, 20, 0));
			b.getValue().getStyleClass().add("btn");
		}
		
		regScn = new Scene(gridPane, 700, 600);
		primaryStage.setScene(regScn);
		this.primaryStage = primaryStage;
	}

	private void createLabels() {
		registerlabels.put(TablesColsNames.USER_USER_FNAME, new Label("Firset Name : "));
		registerlabels.put(TablesColsNames.USER_USER_LNAME, new Label("Last Name : "));
		registerlabels.put(TablesColsNames.USER_USERNAME, new Label("User Name : "));
		registerlabels.put(TablesColsNames.USER_EMAIL, new Label("Email : "));
		registerlabels.put(TablesColsNames.USER_ADDRESS, new Label("Address : "));
		registerlabels.put(TablesColsNames.USER_PHONE, new Label("Phone : "));
		registerlabels.put(TablesColsNames.USER_PASSWORD, new Label("Password : "));
	}

	private void createTexts() {
		registerText.put(TablesColsNames.USER_USER_FNAME, new TextField());
		registerText.put(TablesColsNames.USER_USER_LNAME, new TextField());
		registerText.put(TablesColsNames.USER_USERNAME, new TextField());
		registerText.put(TablesColsNames.USER_EMAIL, new TextField());
		registerText.put(TablesColsNames.USER_ADDRESS, new TextField());
		registerText.put(TablesColsNames.USER_PHONE, new TextField());
	}

	private void createButtons() {
		registerBtn.put("Submit", new Button("Submit"));
		registerBtn.put("Cancel", new Button("Cancel"));
		registerBtn.get("Submit").setId("sub");
		registerBtn.get("Cancel").setId("canc");
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
			if (btn.getId().equals("sub")&& controller.validRegistrationForm(registerText.get(TablesColsNames.USER_EMAIL).getText())){
				if(controller.insertUser(registerText, passwordField, dataBaseModel.UserRole.USER.ordinal())) {					
				showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Registration Successful!",
					"Welcome ");
				controller.switchView(btn);
				}else{
    	 			showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", controller.getError());
				}
			} else if (btn.getId().equals("canc")) {
				controller.switchView(btn);
			}
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
