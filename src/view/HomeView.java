package view;

import java.sql.SQLException;

import dataBaseModel.User;
import dataBaseModel.UserRole;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.HomeController;
import utilities.*;

public class HomeView implements ViewInterface {
	private HomeController controller;
	private User currentUser = User.getInstance();
	private Stage primaryStage;
	private Scene homeScn;
	private VBox headerBox, root;
	private HBox menuBar, searchBar;
	private MenuBar menu;
	private MenuItem profile, logOut;
	private Button cart, searchBtn;
	private MenuItem libraryOrders, publishers, users, addBooks;
	private Menu user, manager, report;
	private TextField search;
	private MenuItem topTenSales, topCustomers, totalSales;
	private ComboBox<String> comboFilter;
	private String searchItem;

	private static HomeView instance = new HomeView();

	private HomeView() {
	}

	public static HomeView getInstance() {
		return instance;
	}

	@Override
	public void buildView(Stage primaryStage) {
		createViews(primaryStage);
		setActions();
		homeScn = new Scene(root, 700, 900);
		homeScn.getStylesheets().add("file:resources/stylesheets/toolbar.css");

		this.primaryStage = primaryStage;

		// fill random book
		controller.addRandomBooks();
	}

	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (HomeController) controller;
	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.homeScn;
	}

	private void createViews(Stage primaryStage) {
		root = new VBox();
		headerBox = new VBox();
		headerBox.setId("header-box");
		user = new Menu("User");
		user.getStyleClass().add("text-btn");
		profile = new MenuItem("Profile");
		logOut = new MenuItem("Log Out");
		user.getItems().addAll(profile, logOut);
		cart = new Button();
		cart.setId("cart-btn");
		libraryOrders = new MenuItem("Library Orders");
		publishers = new MenuItem("Publishers");
		users = new MenuItem("Users");
		addBooks = new MenuItem("Add Books");
		manager = new Menu("Manager");
		manager.getItems().addAll(libraryOrders, publishers, users, addBooks);
		report = new Menu("Report");
		topTenSales = new MenuItem("Top Ten Sales");
		topCustomers = new MenuItem("Top Five Customer");
		totalSales = new MenuItem("Total Sales");
		menu = new MenuBar();
		report.getItems().addAll(topTenSales, topCustomers, totalSales);
		menu.getMenus().addAll(user, manager, report);
		menuBar = new HBox(menu);
		menuBar.getChildren().addAll(cart);
		searchBar = new HBox();
		search = new TextField();
		search.setPromptText("search");
		searchBtn = new Button();
		searchBtn.setId("search-btn");
		comboFilter = new ComboBox<>();
		comboFilter.getItems().addAll(TablesColsNames.BOOK_TITLE, TablesColsNames.BOOK_PUBLISHER,
				TablesColsNames.BOOK_AUTHOR, TablesColsNames.BOOK_CATEGORY, TablesColsNames.BOOK_PUBLICATION_YEAR,
				TablesColsNames.BOOK_PRICE, TablesColsNames.BOOK_ISBN);
		searchBar.getChildren().addAll(search, searchBtn, comboFilter);
		HBox h = new HBox();
		Region g = new Region();
		h.setHgrow(g, Priority.ALWAYS);
		h.getChildren().addAll(menuBar, g, searchBar);
		headerBox.getChildren().addAll(h);
		root.getChildren().add(headerBox);
		if (currentUser.getRole() == UserRole.USER.ordinal()) {
			manager.setVisible(false);
			report.setVisible(false);
		}

	}

	private void setActions() {
		comboFilter.setOnAction(e -> searchItem = comboFilter.getValue());
		searchBtn.setOnAction(e -> controller.selectBooks(searchItem, search.getText()));
		profile.setOnAction(e -> controller.switchView(profile.getText()));
		logOut.setOnAction(e -> controller.switchView(logOut.getText()));
		cart.setOnMouseClicked(e -> {
			controller.switchView(cart.getId());
		});
		libraryOrders.setOnAction(e -> {
			controller.switchView(libraryOrders.getText());
		});
		publishers.setOnAction(e -> {
			controller.switchView(publishers.getText());
		});
		users.setOnAction(e -> {
			controller.switchView(users.getText());
		});
		addBooks.setOnAction(e -> {
			controller.switchView(addBooks.getText());
		});
		topTenSales.setOnAction(e -> {
			try {
				new JavaCallJasperReport().showReport("repo3");
			} catch (ClassNotFoundException | JRException | SQLException e1) {
				e1.printStackTrace();
			}
		});
		topCustomers.setOnAction(e -> {
			try {
				new JavaCallJasperReport().showReport("repo2");
			} catch (ClassNotFoundException | JRException | SQLException e1) {
				e1.printStackTrace();
			}
		});
		totalSales.setOnAction(e -> {
			try {
				new JavaCallJasperReport().showReport("repo1");
			} catch (ClassNotFoundException | JRException | SQLException e1) {
				e1.printStackTrace();
			}
		});

	}

	@Override
	public void setUpdated(boolean updated) {

	}

	public VBox getHeaderBox() {
		return headerBox;
	}

	public HBox getMenuBar() {
		return menuBar;
	}
}
