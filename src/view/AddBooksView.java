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
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import sqlResultsParsers.TablesColsNames;
import viewControllers.AddBookController;
import viewControllers.ControllerInterface;

public class AddBooksView implements ViewInterface {
	private AddBookController controller;
	private HomeView homeView = HomeView.getInstance();
	private LinkedHashMap<String, Label> booklabels = new LinkedHashMap<String, Label>();
	private LinkedHashMap<String, TextField> bookText = new LinkedHashMap<String, TextField>();
	private LinkedHashMap<String, Button> bookBtn = new LinkedHashMap<String, Button>();
	private Stage primaryStage;
	private GridPane gridPane;
	private Scene bookScn;
	private Button home;
	private VBox root;
	private HBox header;
	private static AddBooksView instance = new AddBooksView();

	private AddBooksView() {
	}

	public static AddBooksView getInstance() {
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		createLabels();
		createTexts();
		createButtons();
		createBookFormPane();
		addViewsToPane(primaryStage);
		root = new VBox();
		home = new Button("Home");
		header = new HBox();
		header.getChildren().addAll(homeView.getMenuBar(), home);
		root.getChildren().addAll(header, gridPane);
		bookScn = new Scene(root, 700, 700);
		primaryStage.setScene(bookScn);
		home.setId("home");
		setButtonAction(home);
		this.primaryStage = primaryStage;
		bookScn.getStylesheets().add("file:resources/stylesheets/toolBar.css");
		bookScn.getStylesheets().add("file:resources/stylesheets/forms.css");
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (AddBookController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.bookScn;
	}

	public void addViewsToPane(Stage primaryStage) {
		int i = 1;
		for (Entry<String, Label> l : booklabels.entrySet()) {
			gridPane.add(l.getValue(), 0, i++);
			l.getValue().getStyleClass().add("label");
		}
		i = 1;
		for (Entry<String, TextField> t : bookText.entrySet()) {
			t.getValue().setPrefHeight(40);
			gridPane.add(t.getValue(), 1, i++);
			t.getValue().getStyleClass().add("input");
		}
		i = 0;
		for (Entry<String, Button> b : bookBtn.entrySet()) {
			b.getValue().setPrefHeight(40);
			b.getValue().setDefaultButton(true);
			b.getValue().setPrefWidth(100);
			setButtonAction(b.getValue());
			gridPane.add(b.getValue(), 0, booklabels.size() + 1 + i++, 2, 1);
			GridPane.setHalignment(b.getValue(), HPos.CENTER);
			GridPane.setMargin(b.getValue(), new Insets(20, 0, 20, 0));
			b.getValue().getStyleClass().add("btn");
		}

	}

	private void createLabels() {
		booklabels.put(TablesColsNames.BOOK_ISBN, new Label("ISBN : "));
		booklabels.put(TablesColsNames.BOOK_TITLE, new Label("Title : "));
		booklabels.put(TablesColsNames.BOOK_PUBLISHER, new Label("Publisher : "));
		booklabels.put(TablesColsNames.BOOK_CATEGORY, new Label("Category : "));
		booklabels.put(TablesColsNames.BOOK_PRICE, new Label("Price : "));
		booklabels.put(TablesColsNames.STOCK_QUANTITY, new Label("Quantity : "));
		booklabels.put(TablesColsNames.STOCK_THRESHOLD, new Label("Threshold : "));
		booklabels.put(TablesColsNames.AUTHOR_NAME, new Label("Author Name : "));
		booklabels.put(TablesColsNames.BOOK_PUBLICATION_YEAR, new Label("Publication Year : "));
		booklabels.put(TablesColsNames.BOOK_COVER_IMAGE, new Label("Cover Image : "));
	}

	private void createTexts() {
		bookText.put(TablesColsNames.BOOK_ISBN, new TextField());
		bookText.put(TablesColsNames.BOOK_TITLE, new TextField());
		bookText.put(TablesColsNames.BOOK_PUBLISHER, new TextField());
		bookText.put(TablesColsNames.BOOK_CATEGORY, new TextField());
		bookText.put(TablesColsNames.BOOK_PRICE, new TextField());
		bookText.put(TablesColsNames.STOCK_QUANTITY, new TextField());
		bookText.put(TablesColsNames.STOCK_THRESHOLD, new TextField());
		bookText.put(TablesColsNames.AUTHOR_NAME, new TextField());
		bookText.put(TablesColsNames.BOOK_PUBLICATION_YEAR, new TextField());
		bookText.put(TablesColsNames.BOOK_COVER_IMAGE, new TextField());

	}

	private void createButtons() {
		bookBtn.put("ADD", new Button("ADD"));
		bookBtn.get("ADD").setId("add");
	}

	private void createBookFormPane() {
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
			if (btn.getId().equals("add")) {
				if (controller.addBook(bookText)) {
					showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "new Book", "Success ! ");
					controller.switchView(btn);
				} else {
					showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "new Book",
							controller.getError());
				}
			} else {
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