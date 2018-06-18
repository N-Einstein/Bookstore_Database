package view;

import java.sql.ResultSet;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.PublishersController;

public class publishersView implements ViewInterface {

	// constructor
	private publishersView() {
	}

	private static publishersView instance = new publishersView();

	public static publishersView getInstance() {
		return instance;
	}

	// view
	VBox root;
	Stage primaryStage;
	Scene mainScene;
	PublisherSearchItem publisherSearchItem;

	// controller
	PublishersController controller;
	SQLResultParser parser;

	@Override
	public void buildView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		publisherSearchItem = new PublisherSearchItem();
		setupView();
		controller.setupController();
		buildNavigationBar();
		buildScrollPane();

	}

	private void buildNavigationBar() {
		HBox nav1 = new HBox();
		HBox nav2 = new HBox();
		BuildBackButton(nav1);
		buildSearchArea(nav2);
		buildAddNewPublisherButton(nav1);

		root.getChildren().addAll(nav1, nav2);
	}

	private void BuildBackButton(HBox nav) {
		Button back = new Button("back");
		nav.getChildren().add(back);
		back.setOnMouseClicked(e -> {
			controller.switchView(back);
		});
		nav.getChildren().add(HomeView.getInstance().getMenuBar());
	}

	private void buildSearchArea(HBox nav) {
		HBox searchBar = new HBox();
		TextField search = new TextField();
		search.setPromptText("search");
		Button searchBtn = new Button("search");
		ComboBox comboFilter = new ComboBox<>();
		comboFilter.getItems().addAll(TablesColsNames.PUBLISHER_NAME, TablesColsNames.PUBLISHER_ADDRESS,
				TablesColsNames.PUBLISHER_TELEPHONE);
		searchBar.getChildren().addAll(search, searchBtn, comboFilter);
		nav.getChildren().addAll(searchBar);
		searchBtn.setOnAction(e -> {
			if (comboFilter.getValue() == null) {
				controller.selectPublishers(null, search.getText());
			} else {
				controller.selectPublishers(comboFilter.getValue().toString(), search.getText());
			}
			root.getChildren().remove(root.lookup("#scrollpane"));
			buildScrollPane();
		});

	}

	private void buildAddNewPublisherButton(HBox nav) {
		Region gap = new Region();
		nav.setHgrow(gap, Priority.ALWAYS);

		Button add = new Button();
		add.getStyleClass().add("cart-button");
		add.setOnMouseClicked(e -> {
			controller.switchView(add);
		});

		nav.getChildren().add(gap);
		nav.getChildren().add(add);

	}

	private void buildScrollPane() {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		VBox contents = new VBox();
		scrollPane.setId("scrollpane");
		addRows(contents);
		scrollPane.setOnScroll(e -> {
			if (scrollPane.getVvalue() == 1.0) {// 100%
				// load next 10 results
				addMoreRows(contents);
			}
		});
		scrollPane.setContent(contents);
		root.getChildren().add(scrollPane);
	}

	private void addMoreRows(VBox contents) {
		controller.loadNextPage();
		addRows(contents);

	}

	private void addRows(VBox contents) {
		ResultSet rs = controller.getNextRow();
		while (rs != null) {
			contents.getChildren().add(publisherSearchItem.getPubSearchBox(rs, controller.getParser(), this));
			rs = controller.getNextRow();
			if (rs != null) { // not last one
				// horizontal line
				Separator sp = new Separator();
				sp.setOrientation(Orientation.HORIZONTAL);
				contents.getChildren().add(sp);
			}
		}

	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (PublishersController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public Scene getScene() {
		return mainScene;
	}

	@Override
	public void setUpdated(boolean updated) {
		// TODO Auto-generated method stub

	}

	private void setupView() {
		primaryStage.setTitle("Publishers");
		root = new VBox();
		root.setSpacing(5);
		mainScene = new Scene(root, 700, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/Publishers.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/toolbar.css");
		primaryStage.setScene(mainScene);

	}

}
