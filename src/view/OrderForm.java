package view;

import java.sql.ResultSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import viewControllers.ControllerInterface;
import viewControllers.OrderFormController;

public class OrderForm implements ViewInterface {

	private static OrderForm instance = new OrderForm();

	private OrderForm() {
	}

	public static OrderForm getInstance() {
		return instance;
	}

	private OrderFormController controller;
	private Stage primaryStage;
	private ScrollPane rootTop;
	private VBox root;
	private Scene mainScene;
	private boolean updated = true;
	public final String ISBN_ID = "isbnTF";
	public final String QUANTITY_ID = "quantityTF";

	@Override
	public void buildView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		if (!updated) {
			primaryStage.setScene(mainScene);
			return;
		}
		setup();
		navigationBar();
		Form();
		Confirm();
		fillData();
		updated = false;

	}

	private void navigationBar() {
		HBox navBar = new HBox();
		Button back = new Button();
		back.setId("back-button");
		back.setOnMouseClicked(e -> {
			controller.backPressed();
		});
		navBar.getChildren().add(back);
		navBar.setId("titleBar");
		root.getChildren().add(navBar);

	}

	private void Form() {
		Label();
		Quantity();

	}

	private void Label() {
		HBox hbox = new HBox();
		hbox.getStyleClass().add("hbox");
		Label isbnL = new Label("ISBN:      ");
		TextField isbnTF = new TextField("");
		isbnTF.setId(ISBN_ID);
		isbnTF = adjustTextFieldWidth(isbnTF);

		hbox.getChildren().add(isbnL);
		hbox.getChildren().add(isbnTF);

		root.getChildren().add(hbox);
	}

	private void Quantity() {
		HBox hbox = new HBox();
		hbox.getStyleClass().add("hbox");
		Label quantityL = new Label("Quantity:");
		TextField quantityTF = new TextField("");
		quantityTF.setId(QUANTITY_ID);
		quantityTF = adjustTextFieldWidth(quantityTF);

		hbox.getChildren().add(quantityL);
		hbox.getChildren().add(quantityTF);
		root.getChildren().add(hbox);

	}

	private void Confirm() {
		HBox hb = new HBox();
		Button confirm = new Button("confirm");
		confirm.setOnMouseClicked(e -> {
			controller.confirmOrder();
		});

		Region g1 = new Region();
		Region g2 = new Region();
		hb.setHgrow(g1, Priority.ALWAYS);
		hb.setHgrow(g2, Priority.ALWAYS);

		hb.getChildren().add(g1);
		hb.getChildren().add(confirm);
		hb.getChildren().add(g2);
		root.getChildren().add(hb);
	}

	private void fillData() {
		// In case the Controller knows the ISBN
		if (controller.known) {
			((TextField) mainScene.lookup("#" + ISBN_ID)).setText(controller.ISBN);
		}
	}

	private TextField adjustTextFieldWidth(TextField tf) {
		tf.managedProperty().bind(tf.visibleProperty());
		tf.setMinWidth(tf.getText().length() * 7);
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tf.setMinWidth(tf.getText().length() * 7);

			}
		});
		return tf;

	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (OrderFormController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void setUpdated(boolean updated) {
		// TODO Auto-generated method stub

	}

	@Override
	public Scene getScene() {
		return mainScene;
	}

	private void setup() {
		primaryStage.setTitle("Order");
		root = new VBox();
		rootTop = new ScrollPane();
		rootTop.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		rootTop.setFitToWidth(true);
		rootTop.setContent(root);
		mainScene = new Scene(rootTop, 900, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/orderForm.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/forms.css");
		primaryStage.setScene(mainScene);

	}

}
