package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.TablesColsNames;
import viewControllers.AddPublisherController;
import viewControllers.ControllerInterface;

public class AddPublisherView implements ViewInterface {

	private static final String SUCCESS_MSG = "Publisher added successfully!";
	private static AddPublisherView instance = new AddPublisherView();

	private AddPublisherView() {
	}

	public static AddPublisherView getInstance() {
		return instance;
	}

	// view
	VBox root;
	Stage primaryStage;
	Scene mainScene;
	// controller
	AddPublisherController controller;

	@Override
	public void buildView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		setupView();
		buildNavigationBar();
		vGap(root);
		addFields();
		buildAddNewPublisherButton();
		buildMsg();
		vGap(root);
		vGap(root);
		vGap(root);

	}

	private void buildNavigationBar() {
		HBox nav = new HBox();
		BuildBackButton(nav);
		nav.getChildren().add(HomeView.getInstance().getMenuBar());
		root.getChildren().add(nav);

	}

	private void BuildBackButton(HBox nav) {
		Button back = new Button("back");
		nav.getChildren().add(back);
		back.setOnMouseClicked(e -> {
			controller.switchView(back);
		});

	}

	private void buildAddNewPublisherButton() {
		HBox h = new HBox();
		Button add = new Button("Add");
		add.setOnMouseClicked(e -> {
			try {
				Label msg = (Label) mainScene.lookup("#msg");
				TextField name = (TextField) mainScene.lookup("#f" + TablesColsNames.PUBLISHER_NAME);
				TextField address = (TextField) mainScene.lookup("#f" + TablesColsNames.PUBLISHER_ADDRESS);
				TextField tele = (TextField) mainScene.lookup("#f" + TablesColsNames.PUBLISHER_TELEPHONE);
				controller.addPublisher(name.getText(), address.getText(), tele.getText());
				msg.setText(SUCCESS_MSG);
				msg.getStyleClass().clear();
				msg.getStyleClass().add("msg-confirm");
				msg.setVisible(true);
			} catch (Exception e2) {
				Label msg = (Label) mainScene.lookup("#msg");
				msg.setText(e2.getMessage());
				msg.getStyleClass().clear();
				msg.getStyleClass().add("msg-error");
				msg.setVisible(true);

			}

		});

		h.getChildren().add(add);
		h.setAlignment(Pos.CENTER);
		root.getChildren().add(h);

	}

	private void buildMsg() {
		HBox h = new HBox();
		Label msg = new Label();
		msg.setId("msg");
		msg.setVisible(false);

		h.getChildren().add(msg);
		h.setAlignment(Pos.CENTER);
		root.getChildren().add(h);
	}

	private void addFields() {
		HBox container = new HBox();
		container.getStyleClass().add("container");
		container.setAlignment(Pos.CENTER);
		VBox v1 = new VBox();
		VBox v2 = new VBox();
		v1.getStyleClass().add("v1");
		v2.getStyleClass().add("v2");
		makeRow(TablesColsNames.PUBLISHER_NAME, v1, v2);
		makeRow(TablesColsNames.PUBLISHER_ADDRESS, v1, v2);
		makeRow(TablesColsNames.PUBLISHER_TELEPHONE, v1, v2);

		container.getChildren().add(v1);
		container.getChildren().add(v2);
		root.getChildren().add(container);

	}

	private void makeRow(String name, VBox v1, VBox v2) {
		Label l = new Label(name + ": ");
		l.setId("l" + name);
		v1.getChildren().add(l);

		TextField tf = new TextField();
		tf.setId("f" + name);
		v2.getChildren().add(tf);
	}

	private void hGap(HBox parent) {
		Region gap = new Region();
		parent.getChildren().add(gap);
		parent.setHgrow(gap, Priority.ALWAYS);

	}

	private void vGap(VBox parent) {
		Region gap = new Region();
		parent.getChildren().add(gap);
		parent.setVgrow(gap, Priority.ALWAYS);

	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (AddPublisherController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.getScene();
	}

	@Override
	public void setUpdated(boolean updated) {

	}

	private void setupView() {
		primaryStage.setTitle("Add Publishers");
		root = new VBox();
		root.setSpacing(5);
		mainScene = new Scene(root, 500, 500);
		mainScene.getStylesheets().add("file:resources/stylesheets/AddPublishers.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/forms.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/toolBar.css");
		primaryStage.setScene(mainScene);

	}

}
