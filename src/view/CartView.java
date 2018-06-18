package view;

import java.sql.ResultSet;

import dataBaseModel.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.CartViewController;
import viewControllers.ControllerInterface;
import viewControllers.SearchResultViewController;

public class CartView implements ViewInterface {
	
	private static CartView instance = new CartView();
	private CartView(){}
	public static CartView getInstance() {
		return instance;
	}
	
	
	public static final String TOTAL_PRICE_ID = "totalPrice";
	
	private Scene mainScene;
	private VBox root;
	private ScrollPane rootTop;
	private SQLResultParser parser;
	private Stage primaryStage;
	private CartViewController controller;
	private boolean updated = true;

	@Override
	public void buildView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		setupView();
		setBackButton();
		setPricePanel();
		updateTextField(TOTAL_PRICE_ID, User.decimarFormatter.format(controller.getTotalPayment()) + "$");
		controller.setup();
		displayRows();
		setPayButton();

	}


	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (CartViewController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public Scene getScene() {
		return mainScene;
	}

	private void setupView() {
		primaryStage.setTitle("Shopping Cart");
		root = new VBox();
		rootTop = new ScrollPane();
		rootTop.setFitToWidth(true);
		rootTop.setContent(root);
		mainScene = new Scene(rootTop, 700, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/shoppingCartPage.css");
		mainScene.getStylesheets().add("file:resources/stylesheets/resultPage.css");
		primaryStage.setScene(mainScene);
		parser =controller.getParser();
		
	}
	
	private void setPricePanel() {
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		
		Label label = new Label("Total = ");
		label.getStyleClass().add("price-panel");
		Label price = new Label();
		price.setId(TOTAL_PRICE_ID);
		price.getStyleClass().add("price-panel");
		
		hbox.getChildren().add(label);
		hbox.getChildren().add(price);
		root.getChildren().add(hbox);
	}
	
	private void setBackButton() {
		HBox h = new HBox();
		h.setId("titleBar");
		Button back = new Button();
		back.setId("back-button");
		h.getChildren().add(back);
		root.getChildren().add(h);
		back.setOnMouseClicked(e-> {
			controller.backButtonPressed();
		});
		
	}
	private void setPayButton() {
		HBox h = new HBox();
		h.setAlignment(Pos.CENTER);
		Button back = new Button("pay");
		back.getStyleClass().add("pay-btn");
		h.getChildren().add(back);
		root.getChildren().add(h);
		back.setOnMouseClicked(e-> {
			controller.payButtonPressed();
		});
		
	}
	private void displayRows() {
		SearchResultViewController searchResultViewController = new SearchResultViewController(SearchResultView.getInstance(), null);
		BookSearchResultView bookSearchResultView = new BookSearchResultView(searchResultViewController, this);
		CartQuantityBox cartQuantityBox = new CartQuantityBox(controller);
		ResultSet row = controller.getNextRowResultSet();
		String isbn;
		while(row != null) {
			isbn = parser.getResult(TablesColsNames.BOOK_ISBN, row);
			HBox hbox = new HBox();
			hbox.getStyleClass().add("row");
			hbox.setAlignment(Pos.CENTER);
			Region gap = new Region();
			hbox.setHgrow(gap, Priority.ALWAYS);
			hbox.getChildren().add(addDeleteButton(isbn));
			hbox.getChildren().add(bookSearchResultView.buildBookDetails(row, parser));
			hbox.getChildren().add(gap);
			hbox.getChildren().add(cartQuantityBox.getControlBox(row));
			root.getChildren().add(hbox);
			row = controller.getNextRowResultSet();
		}
		
	}
	private Button addDeleteButton(String isbn) {
		Button d = new Button();
		d.getStyleClass().add("delete-btn");
		d.setOnMousePressed(e-> {
			controller.updateQuantity(0, isbn);
			User.getInstance().cartISBN.remove(isbn);
			//ListView<HBox> lv = (ListView<HBox>) mainScene.lookup("#br");
			HBox parent = (HBox) d.getParent();
			root.getChildren().remove(parent);
			
		
		});
		return d;
	}
	public void updateTextField(String id, String newText) {
		Label label = (Label) mainScene.lookup("#" + id);
		label.setText(newText);
	}
	public String getLabelText(String id) {
		Label label = (Label) mainScene.lookup("#" + id);
		if (label != null) {
			return label.getText();
		}
		return null;
	}
	@Override
	public void setUpdated(boolean updated) {
		this.updated = updated;
		controller.updated = updated;

	}

}
