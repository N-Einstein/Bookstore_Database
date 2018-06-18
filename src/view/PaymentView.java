package view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import viewControllers.ControllerInterface;
import viewControllers.PaymentController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PaymentView implements ViewInterface {
	private PaymentController controller;
	private LinkedHashMap<String, Label> labels = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, TextField> txt = new LinkedHashMap<String, TextField>();

	private PasswordField password;
	private Stage primaryStage;
	private Button Btn[] = new Button[2];
	private DatePicker expiryDatePicker;
	private GridPane gridPane;
	private Scene scene;

	private static PaymentView instance;

	private PaymentView() {
	}

	public static PaymentView getInstance() {
		if (instance == null)
			instance = new PaymentView();
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		createLabels();
		createTexts();
		password = new PasswordField();
		createButtons();
		createLogInFormPane();
		addViewsToPane(primaryStage);
		scene.getStylesheets().add("file:resources/stylesheets/forms.css");
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (PaymentController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public Scene getScene() {
		return scene;
	}

	public void addViewsToPane(Stage primaryStage) {
		int i = 1;
		for (Entry<String, Label> l : labels.entrySet()) {
			gridPane.add(l.getValue(), 0, i++);
			l.getValue().getStyleClass().add("label");
		}

		i = 1;
		for (Entry<String, TextField> t : txt.entrySet()) {
			t.getValue().setPrefHeight(40);
			gridPane.add(t.getValue(), 1, i++);
			t.getValue().getStyleClass().add("input");
		}

		password.setPrefHeight(40);
		password.getStyleClass().add("iput");
		gridPane.add(password, 1, i);

		expiryDatePicker = new DatePicker();
		expiryDatePicker.setPrefHeight(40);
		gridPane.add(expiryDatePicker, 1, labels.size());

		for (int j = 0; j < Btn.length; j++) {
			Btn[j].setPrefHeight(40);
			Btn[j].setDefaultButton(true);
			Btn[j].setPrefWidth(100);
			setButtonAction(j);
			gridPane.add(Btn[j], 0, labels.size() + 1 + j, 2, 1);
			GridPane.setHalignment(Btn[j], HPos.CENTER);
			GridPane.setMargin(Btn[j], new Insets(20, 0, 20, 0));
			Btn[j].getStyleClass().add("btn");
		}
		scene = new Scene(gridPane, 700, 900);
		primaryStage.setScene(scene);
		this.primaryStage = primaryStage;
	}

	private void createLabels() {
		labels.put("credit", new Label("Creditcard number : "));
		labels.put("address", new Label("Shipping Address : "));
		labels.put("password", new Label("Password : "));
		labels.put("date", new Label("Expiry Date : "));
	}

	private void createTexts() {
		txt.put("credit", new TextField());
		txt.put("address", new TextField());
	}

	private void createButtons() {
		Btn[0] = new Button("Pay");
		Btn[1] = new Button("Cancel");
		Btn[0].setId("pay_btn");
		Btn[1].setId("cancel_btn");
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

	private void setButtonAction(int j) {
		Button btn = Btn[j];
		if (j == 0) {
			btn.setOnMouseClicked(e -> {
				if (emptyField()) {
					showAlert("error");
				} else if (isExpiredCredit()) {
					showAlert("expired");
				} else if (!isValidCreditNumber(txt.get("credit").getText().trim())) {
					showAlert("invalid");
				} else {
					controller.switchView(btn);
				}
			});
		}else if(j == 1) {
			btn.setOnMouseClicked(e -> {
				controller.switchView(btn);
			});
		}
	}

	// -- using Luhn algorithm
	private boolean isValidCreditNumber(String cardNo) {
		int nDigits = cardNo.length();
		int nSum = 0;
		boolean isSecond = false;
		for (int i = nDigits - 1; i >= 0; i--) {
			int d = cardNo.charAt(i) - '0';

			if (isSecond == true)
				d *= 2;

			nSum += d / 10;
			nSum += d % 10;

			isSecond = !isSecond;
		}
		return (nSum % 10 == 0);
	}

	private boolean emptyField() {
		for (Entry<String, TextField> t : txt.entrySet()) {
			if (t.getValue().getText().trim().length() == 0) {
				return true;
			}
		}
		if (password.getText().length() == 0)
			return true;
		return false;
	}

	private boolean isExpiredCredit() {
		LocalDate date = expiryDatePicker.getValue();
		return !date.isAfter(LocalDate.now());
	}

	public void showAlert(String type) {
		Alert alert = null;
		switch (type) {
		case "error":
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("Please, provide all the required information");
			break;
		case "expired":
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("your credit card is expired");
			break;
		case "invalid":
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("double check your credit number");
			break;
		case "synch":
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setContentText("ERROR: cannot perform transaction!");
			break;
		}
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	public String getText(String key) {
		return txt.get(key).getText();
	}

	@Override
	public void setUpdated(boolean updated) {
		// TODO Auto-generated method stub
	}
}