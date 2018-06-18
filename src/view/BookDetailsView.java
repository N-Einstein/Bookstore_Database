package view;

import java.sql.ResultSet;

import dataBaseModel.User;
import dataBaseModel.UserRole;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.BookDetailsController;
import viewControllers.ControllerInterface;

public class BookDetailsView implements ViewInterface {

	private BookDetailsController controller;
	private SQLResultParser parser;
	private ResultSet resultSet;
	private Stage primaryStage;
	private ScrollPane rootTop;
	private VBox root;
	private Scene mainScene;
	private boolean updated = true;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void buildView(Stage primaryStage) {
		controller.performQuery();
		this.resultSet = controller.getResultSet();
		this.parser = controller.getParser();
		setup(primaryStage);
		buildTitleBar();
		createTitle();
		BuildMainContents();
		//BuildOtherDetails();
		buildEditErrorMsg();
		buildAddToCartButton();
		updated = false;

	}

	private void buildAddToCartButton() {
		String isbn = parser.getResult(TablesColsNames.BOOK_ISBN, resultSet);
		Region gap = new Region();
		Button cart = new Button("Add to Your Cart");
		Label msg = buildSuccessMsg(isbn);
		cart.setId("cart-button");
		HBox h = new HBox();
		HBox h2 = new HBox();
		VBox v = new VBox();
		h2.setAlignment(Pos.CENTER);
		h.getChildren().add(cart);
		h2.getChildren().add(msg);
		h.setAlignment(Pos.CENTER);
		v.getChildren().add(h);
		v.getChildren().add(h2);
		root.getChildren().add(v);
		cart.setOnMousePressed(e -> {
			controller.addToCart(isbn);
		});

	}
	
	private Label buildSuccessMsg(String isbn) {
		Label msg = new Label("Book added to your cart");
		if (!User.cartISBN.containsKey(isbn)) {
			msg.setVisible(false);
		}
		msg.setAlignment(Pos.BASELINE_CENTER);
		msg.setId("msg" + isbn);
		msg.getStyleClass().add("msg");
		return msg;
	}

	private void BuildOtherDetails() {
		VBox otherDetails = buildOtherDetailsBox();
		otherDetails.setId("otherVBox");

		root.getChildren().add(otherDetails);

	}

	private void BuildMainContents() {
		HBox mainPart = new HBox();
		mainPart.setId("main-parts");
		ImageView iv = buildImage();
		HBox im = new HBox();
		im.setId("im");

		VBox BasicDetails = buildBasicDetailsBox();

		
		im.getChildren().add(iv);
		mainPart.getChildren().add(im);
		mainPart.getChildren().add(BasicDetails);

		root.getChildren().add(mainPart);

	}

	private VBox buildOtherDetailsBox() {
		VBox vbox = new VBox();
		vbox.setId("detailsVBox");
		String authors = controller.getAuthorsAsString();
		HBox authorsl = buildDetailsString("Authors : ", "", "");
		HBox authorsv = buildDetailsString("", authors, TablesColsNames.AUTHOR_NAME);
		vbox.getChildren().add(authorsl);
		vbox.getChildren().add(authorsv);
		if (authors.isEmpty()) {
		}
		return vbox;

	}

	private VBox buildBasicDetailsBox() {
		VBox vbox = new VBox();
		vbox.setId("detailsBox");
		HBox publicationYear = buildDetailsString("Publication year: ",
				parser.getResult(TablesColsNames.BOOK_PUBLICATION_YEAR, resultSet),
				TablesColsNames.BOOK_PUBLICATION_YEAR);

		HBox publisher = buildDetailsString("Publisher: ", parser.getResult(TablesColsNames.BOOK_PUBLISHER, resultSet),
				TablesColsNames.BOOK_PUBLISHER);
		HBox category = buildDetailsString("Category: ", parser.getResult(TablesColsNames.BOOK_CATEGORY, resultSet),
				TablesColsNames.BOOK_CATEGORY);
		HBox isbn = buildDetailsString("ISBN: ", parser.getResult(TablesColsNames.BOOK_ISBN, resultSet),
				TablesColsNames.BOOK_ISBN);
		HBox price = buildDetailsString("Price: ", parser.getResult(TablesColsNames.BOOK_PRICE, resultSet) + "$",
				TablesColsNames.BOOK_PRICE);
		HBox stock = buildDetailsString("In Stock: ", controller.getNoInStock(),
				"stock-field");

		vbox.getChildren().add(publisher);
		vbox.getChildren().add(category);
		vbox.getChildren().add(isbn);
		vbox.getChildren().add(price);
		vbox.getChildren().add(stock);

		vbox.getChildren().add(publicationYear);
		vbox.getChildren().add(buildOtherDetailsBox());
		
		return vbox;
	}

	private HBox buildDetailsString(String label, String value, String id) {
		id = id.replaceAll(" ", "");
		HBox box = new HBox();
		box.getStyleClass().add("details-hbox");
		Label mlabel = new Label(label);
		mlabel.getStyleClass().add("label");

		Label mValue = new Label(value);
		mValue.getStyleClass().add("value");
		mValue.setWrapText(true);
		mValue.setId("l" + id);
		mValue.managedProperty().bind(mValue.visibleProperty());

		TextField tvalue = new TextField(value);
		tvalue.getStyleClass().add("value");
		tvalue.setId("f" + id);
		tvalue.setVisible(false);
		tvalue.managedProperty().bind(tvalue.visibleProperty());
		tvalue.setMinWidth(tvalue.getText().length() * 7);
		tvalue.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tvalue.setMinWidth(tvalue.getText().length() * 7);

			}
		});

		box.getChildren().add(mlabel);
		box.getChildren().add(mValue);
		box.getChildren().add(tvalue);
		return box;
	}

	private ImageView buildImage() {
		Image image = new Image("file:" + parser.getResult(TablesColsNames.BOOK_COVER_IMAGE, resultSet));
		ImageView iv = new ImageView();
		iv.setImage(image);
		iv.setFitWidth(100);
		iv.setPreserveRatio(true);
		iv.setSmooth(true);
		iv.setCache(true);
		iv.setId("im");
		return iv;
	}

	private void buildTitleBar() {
		HBox titleBar = new HBox();
		titleBar.setId("titleBar");
		VBox backButtonBox = createBackButton();
		HBox gap = new HBox();
		Button edit = buildEditBox();
		Button order = new Button("order");
		order.setId("order-btn");
		order.setOnMouseClicked(e->{
			controller.order();
		});
		
		
		titleBar.setHgrow(gap, Priority.ALWAYS);
		titleBar.getChildren().add(backButtonBox);
		titleBar.getChildren().add(gap);
		if (User.getInstance().getRole() != UserRole.USER.ordinal()) {
			titleBar.getChildren().add(edit);
			titleBar.getChildren().add(order);
		}

		titleBar.setAlignment(Pos.CENTER_LEFT);
		root.getChildren().add((titleBar));

	}

	private Button buildEditBox() {

		Button edit = new Button("edit");
		edit.setId("edit-btn");
		edit.setOnMouseClicked(e -> {
			if (edit.getText().equals("edit")) {
				edit.setText("save");
				flipView(TablesColsNames.BOOK_CATEGORY);
				flipView(TablesColsNames.BOOK_ISBN);
				flipView(TablesColsNames.BOOK_PRICE);
				flipView(TablesColsNames.BOOK_PUBLISHER);
				flipView(TablesColsNames.BOOK_PUBLICATION_YEAR);
				flipView(TablesColsNames.AUTHOR_NAME);
				flipView(TablesColsNames.BOOK_TITLE);
			} else {
				try {
					controller.updateBookTable();
					edit.setText("edit");
					flipView(TablesColsNames.BOOK_CATEGORY);
					flipView(TablesColsNames.BOOK_ISBN);
					flipView(TablesColsNames.BOOK_PRICE);
					flipView(TablesColsNames.BOOK_PUBLISHER);
					flipView(TablesColsNames.BOOK_PUBLICATION_YEAR);
					flipView(TablesColsNames.AUTHOR_NAME);
					flipView(TablesColsNames.BOOK_TITLE);
					mainScene.lookup("#edit-error-msg").setVisible(false);
				} catch (Exception e1) {
					Label l = (Label) mainScene.lookup("#edit-error-msg");
					l.setText(e1.getMessage());
					mainScene.lookup("#edit-error-msg").setVisible(true);
				}
				
				
			}

		});
		return edit;
	}


	private void flipView(String id) {
		id = id.replaceAll(" ", "");
		Label l = (Label) mainScene.lookup("#l" + id);
		l.setVisible(!l.isVisible());

		TextField t = (TextField) mainScene.lookup("#f" + id);
		t.setVisible(!t.isVisible());

		if (l.isVisible()) {
			l.setText(t.getText());
		} else {
			t.setText(l.getText());
		}

	}

	private void createTitle() {
		HBox titleHBox = new HBox();
		titleHBox.setId("title-hbox");
		String id = TablesColsNames.BOOK_TITLE;
		String text = parser.getResult(TablesColsNames.BOOK_TITLE, resultSet);
		Label mValue = new Label(text);
		mValue.setWrapText(true);
		mValue.setId("l" + id);
		mValue.managedProperty().bind(mValue.visibleProperty());

		TextField tvalue = new TextField(text);
		tvalue.setId("f" + id);
		tvalue.getStyleClass().add("value");
		tvalue.setVisible(false);
		tvalue.managedProperty().bind(tvalue.visibleProperty());
		tvalue.setMinWidth(tvalue.getText().length() * 7);
		tvalue.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tvalue.setMinWidth(tvalue.getText().length() * 7);

			}
		});
		
		mValue.getStyleClass().add("book-title");
		tvalue.getStyleClass().add("book-title");
		titleHBox.getChildren().add(mValue);
		titleHBox.getChildren().add(tvalue);
		titleHBox.setAlignment(Pos.CENTER);
		root.getChildren().add(titleHBox);
	}

	private VBox createBackButton() {
		VBox box = new VBox();
		Button back = new Button();
		back.setId("back-button");
		back.setOnMouseClicked(e -> {
			controller.backPressed();
		});
		Region gap1 = new Region();
		Region gap2 = new Region();
		box.setVgrow(gap1, Priority.ALWAYS);
		box.setVgrow(gap2, Priority.ALWAYS);
		box.getChildren().add(gap1);
		box.getChildren().add(back);
		box.getChildren().add(gap2);
		return box;
	}

	private void buildEditErrorMsg() {
		HBox h = new HBox();
		h.setAlignment(Pos.CENTER);
		Label error = new Label("error");
		error.setId("edit-error-msg");
		error.setVisible(false);
		h.getChildren().add(error);
		root.getChildren().add(h);
		
	}

	private void setup(Stage primaryStage) {
		primaryStage.setTitle("Book details");
		root = new VBox();
		rootTop = new ScrollPane();
		rootTop.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		rootTop.setFitToWidth(true);
		rootTop.setContent(root);
		mainScene = new Scene(rootTop, 700, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/bookDetailsPage.css");
		primaryStage.setScene(mainScene);
		this.primaryStage = primaryStage;
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (BookDetailsController) controller;

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
