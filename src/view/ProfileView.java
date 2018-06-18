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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.ProfileController;

public class ProfileView implements ViewInterface {

	private ProfileController controller;
	private HomeView homeView = HomeView.getInstance();
	private LinkedHashMap<String, Label> keysLabel = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, Label> valuesLabel = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, TextField> valuesText = new LinkedHashMap<String, TextField>();
	private LinkedHashMap<String, Button> profileBtn = new LinkedHashMap<String, Button>();
	private VBox root;
	private HBox header;
	private Button home;
	private PasswordField passwordField = new PasswordField();
	private Stage primaryStage;
	private GridPane gridPane;
	private Scene profScn;

	private static ProfileView instance = new ProfileView();

	private ProfileView() {

	}

	public static ProfileView getInstance() {
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		initialLabels();
		createText();
		createButtons();
		createProfilePane();
		addViewsToPane();
		root = new VBox();
		header = new HBox();
		home = new Button("Home");
		header.getChildren().addAll(homeView.getMenuBar(), home);
		root.getChildren().addAll(header, gridPane);
		profScn = new Scene(root, 700, 700);
		primaryStage.setScene(profScn);
		home.setId("home");
		setButtonAction(home);
		this.primaryStage = primaryStage;
		profScn.getStylesheets().add("file:resources/stylesheets/toolBar.css");
		profScn.getStylesheets().add("file:resources/stylesheets/forms.css");
	}

	void initialLabels() {
		keysLabel.put(TablesColsNames.USER_USER_FNAME, new Label("Firset Name : "));
		keysLabel.put(TablesColsNames.USER_USER_LNAME, new Label("Last Name : "));
		keysLabel.put(TablesColsNames.USER_USERNAME, new Label("User Name : "));
		keysLabel.put(TablesColsNames.USER_EMAIL, new Label("Email : "));
		keysLabel.put(TablesColsNames.USER_ADDRESS, new Label("Address : "));
		keysLabel.put(TablesColsNames.USER_PHONE, new Label("Phone : "));
		keysLabel.put(TablesColsNames.USER_PASSWORD, new Label("Password : "));
		valuesLabel.put(TablesColsNames.USER_USER_FNAME,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USER_FNAME)));
		valuesLabel.put(TablesColsNames.USER_USER_LNAME,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USER_LNAME)));
		valuesLabel.put(TablesColsNames.USER_USERNAME,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USERNAME)));
		valuesLabel.put(TablesColsNames.USER_EMAIL,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_EMAIL)));
		valuesLabel.put(TablesColsNames.USER_ADDRESS,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_ADDRESS)));
		valuesLabel.put(TablesColsNames.USER_PHONE,
				new Label(controller.getUser().getCurrentUser().get(TablesColsNames.USER_PHONE)));
	}

	void createText() {
		valuesText.put(TablesColsNames.USER_USER_FNAME, new TextField());
		valuesText.put(TablesColsNames.USER_USER_LNAME, new TextField());
		valuesText.put(TablesColsNames.USER_USERNAME, new TextField());
		valuesText.put(TablesColsNames.USER_EMAIL, new TextField());
		valuesText.put(TablesColsNames.USER_ADDRESS, new TextField());
		valuesText.put(TablesColsNames.USER_PHONE, new TextField());
	}

	void updateLabels() {
		valuesLabel.get(TablesColsNames.USER_USER_FNAME)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USER_FNAME));
		valuesLabel.get(TablesColsNames.USER_USER_LNAME)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USER_LNAME));
		valuesLabel.get(TablesColsNames.USER_USERNAME)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_USERNAME));
		valuesLabel.get(TablesColsNames.USER_EMAIL)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_EMAIL));
		valuesLabel.get(TablesColsNames.USER_ADDRESS)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_ADDRESS));
		valuesLabel.get(TablesColsNames.USER_PHONE)
				.setText(controller.getUser().getCurrentUser().get(TablesColsNames.USER_PHONE));
	}

	private void createButtons() {
		profileBtn.put("Edit", new Button("Edit"));
		profileBtn.put("Save", new Button("Save"));
		profileBtn.put("Cancel", new Button("Cancel"));
		profileBtn.get("Edit").setId("edit");
		profileBtn.get("Save").setId("save");
		profileBtn.get("Cancel").setId("canc");
	}

	private void createProfilePane() {
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

	public void addViewsToPane() {
		int i = 1;
		for (Entry<String, Label> l : keysLabel.entrySet()) {
			gridPane.add(l.getValue(), 0, i++);
			if (l.getKey().equals(TablesColsNames.USER_PASSWORD))
				l.getValue().setVisible(false);
			l.getValue().getStyleClass().add("label");

		}
		i = 1;
		for (Entry<String, TextField> t : valuesText.entrySet()) {
			t.getValue().setPrefHeight(40);
			gridPane.add(t.getValue(), 1, i++);
			t.getValue().setVisible(false);
			t.getValue().getStyleClass().add("input");
		}
		i = 1;
		for (Entry<String, Label> l : valuesLabel.entrySet()) {
			l.getValue().setPrefHeight(40);
			gridPane.add(l.getValue(), 1, i++);
			l.getValue().getStyleClass().add("label");
		}
		passwordField.setPrefHeight(40);
		passwordField.getStyleClass().add("input");
		gridPane.add(passwordField, 1, keysLabel.size());
		passwordField.setVisible(false);
		i = 0;
		for (Entry<String, Button> b : profileBtn.entrySet()) {
			b.getValue().setPrefHeight(40);
			b.getValue().setDefaultButton(true);
			b.getValue().setPrefWidth(100);
			setButtonAction(b.getValue());
			gridPane.add(b.getValue(), 0, keysLabel.size() + 1 + i++, 2, 1);
			GridPane.setHalignment(b.getValue(), HPos.CENTER);
			GridPane.setMargin(b.getValue(), new Insets(20, 0, 20, 0));
			if (b.getKey().equals("Save") || b.getKey().equals("Cancel"))
				b.getValue().setVisible(false);
			b.getValue().getStyleClass().add("btn");
		}

	}

	private void editMode(boolean mode) {
		keysLabel.get(TablesColsNames.USER_PASSWORD).setVisible(mode);
		for (Entry<String, TextField> t : valuesText.entrySet()) {
			t.getValue().setVisible(mode);
		}
		for (Entry<String, Label> l : valuesLabel.entrySet()) {
			l.getValue().setVisible(!mode);
		}
		passwordField.setVisible(mode);
		profileBtn.get("Edit").setVisible(!mode);
		profileBtn.get("Save").setVisible(mode);
		profileBtn.get("Cancel").setVisible(mode);
	}

	private void setButtonAction(Button btn) {
		btn.setOnMouseClicked(e -> {
			if (btn.getId().equals("edit")) {
				editMode(true);
			} else if (btn.getId().equals("save")
					&& controller.validUpdateForm(valuesText.get(TablesColsNames.USER_EMAIL).getText())) {
				if (controller.updateUser(valuesText, passwordField)) {
					updateLabels();
					editMode(false);
				} else {
					showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!",
							controller.getError());
				}
			} else if (btn.getId().equals("canc")) {
				editMode(false);
			} else {
				controller.switchView(btn);
			}
		});
	}

	public void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(owner);
		alert.show();
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (ProfileController) controller;
	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.profScn;
	}

	@Override
	public void setUpdated(boolean updated) {
	}

}
