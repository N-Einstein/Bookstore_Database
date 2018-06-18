package view;

import java.sql.ResultSet;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import viewControllers.CartViewController;
import viewControllers.ControllerInterface;
import viewControllers.OrderFormController;
import viewControllers.SearchResultViewController;

public class SearchResultView implements ViewInterface {

	private static SearchResultView instance = new SearchResultView();

	private SearchResultView() {
	}

	public static SearchResultView getInstance() {
		return instance;
	}

	private BookSearchResultView bookSearchResultView;
	private SearchResultViewController controller;
	private VBox root;
	private VBox root2;
	private ScrollPane rootTop;
	private Stage primaryStage;
	private SQLResultParser parser;
	private Scene mainScene;
	private boolean updated = true;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void buildView(Stage primaryStage) {
		setup(primaryStage);
		displayNavigationArea();
		controller.setupController();
		this.parser = controller.getParser();
		displayRows();
		root2.getChildren().add(rootTop);
	}

	private void displayNavigationArea() {
		root2.getChildren().add(HomeView.getInstance().getHeaderBox());
		Button cart = new Button("Cart");
		cart.setOnMouseClicked(e -> {
			CartView cartView = CartView.getInstance();
			CartViewController cartViewController = new CartViewController(cartView, this);
			cartView.buildView(primaryStage);
		});

		Button order = new Button("order");
		order.setOnMouseClicked(e -> {
			OrderForm orderForm = OrderForm.getInstance();
			OrderFormController ofc = new OrderFormController(orderForm, this);
			orderForm.buildView(this.getPrimaryStage());
		});

	}

	public void setController(ControllerInterface controller) {
		this.controller = (SearchResultViewController) controller;
	}

	private void displayRows() {
		ResultSet row = controller.getNextRow();
		bookSearchResultView = new BookSearchResultView(controller, this);
		while (row != null) {
			root.getChildren().add(bookSearchResultView.buildBookHBox(row, parser));
			row = controller.getNextRow();
		}
	}

	private void addMoreRows() {
		controller.loadNextPage();
		displayRows();

	}

	private void setup(Stage primaryStage) {
		bookSearchResultView = new BookSearchResultView(controller, this);
		primaryStage.setTitle("Search Results");
		root = new VBox();
		root2 = new VBox();
		rootTop = new ScrollPane();
		rootTop.setFitToWidth(true);
		rootTop.setContent(root);
		rootTop.setOnScroll(e -> {
			if (rootTop.getVvalue() == 1.0) {// 100%
				// load next 10 results
				addMoreRows();
			}
		});
		mainScene = new Scene(root2, 700, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/resultPage.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/toolbar.css");
		primaryStage.setScene(mainScene);
		this.primaryStage = primaryStage;
	}

	@Override
	public Scene getScene() {
		return mainScene;
	}

	@Override
	public void setUpdated(boolean updated) {
		this.updated = updated;

	}

}
