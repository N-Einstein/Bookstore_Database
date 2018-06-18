package view;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.GapContent;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.LibraryOrdersController;

public class LibraryOrders implements ViewInterface {
	private static LibraryOrders instance = new LibraryOrders();
	private LibraryOrders() {}
	public static LibraryOrders getInstance() {
		return instance;
	}
	
	// booleans
	private boolean updated = true;
	
	// controller
	LibraryOrdersController controller;
	
	// database
	
	// view
	private Stage primaryStage;
	private Scene mainScene;
	private VBox root;

	@Override
	public void buildView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		setup();
		controller.performQuery();
		buildNavigationBar();
		buildScrollArea();

		updated = false;
	}

	private void buildNavigationBar() {
		HBox navBar = new HBox();
		navBar.setId("nav-bar");
		
		Button backBtn = buildBackButton();
		backBtn.setId("back-button");
		navBar.setId("titleBar");
		
		
		navBar.getChildren().add(backBtn);
		root.getChildren().add(navBar);
		
	}
	private void buildScrollArea() {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setId("scroll-area");
		VBox contents = new VBox();	
		contents.setId("contents");
		contents.setFillWidth(true);
		// add rows
		addRow(controller.getNextResult(), contents);
		
		
		
		scrollPane.setContent(contents);
		root.getChildren().add(scrollPane);
		
	}
	
	
	
	private void addRow(ResultSet nextResult, VBox container) {
		if (nextResult == null) {
			return;
		}
		HBox row = new HBox();
		row.getStyleClass().add("row");
		row.setSpacing(30);
		row.setMinWidth(container.getWidth());
		SQLResultParser parser = controller.getParser();
		String isbnString = parser.getResult(TablesColsNames.LIBRARY_ORDERS_ISBN, nextResult);
		HBox isbn = formLabel("ISBN: ", isbnString);
		HBox managerId = formLabel("Manager ID: ", parser.getResult(TablesColsNames.LIBRARY_ORDERS_USER_ID, nextResult));
		String quantityString = parser.getResult(TablesColsNames.LIBRARY_ORDERS_QUANTITY, nextResult);
		HBox quantity = formLabel("Quantity: ", quantityString);
		String orderIdString = parser.getResult(TablesColsNames.LIBRARY_ORDERS_ORDER_ID, nextResult);
		HBox orderId = formLabel("Order ID: ", orderIdString);
		HBox date = formLabel("Date: ", parser.getResult(TablesColsNames.LIBRARY_ORDERS_DATE, nextResult));
		
		VBox col1 = dataCol(new ArrayList<HBox>(Arrays.asList(orderId, managerId, isbn)));
		VBox col2 = dataCol(new ArrayList<HBox>(Arrays.asList(quantity, date)));
		Region gap = new Region();
		row.setHgrow(gap, Priority.ALWAYS);
		
		Separator sp = new Separator();
		sp.setOrientation(Orientation.HORIZONTAL);

        
        Button confirm = buildConfirmBox();
        confirm.setUserData(new ArrayList<String>(Arrays.asList(isbnString, quantityString, orderIdString)));
        
		row.getChildren().add(col1);
		row.getChildren().add(col2);
		row.getChildren().add(gap);
		row.getChildren().add(confirm);

		
		
		container.getChildren().add(row);
		container.getChildren().add(sp);
		container.setSpacing(5);
		addRow(controller.getNextResult(), container);
		
	}
	private HBox formLabel(String name, String value) {
		HBox labelBox = new HBox();
		Label title = new Label(name);
		Label data = new Label(value);
		
		labelBox.setSpacing(3);
		labelBox.getChildren().add(title);
		labelBox.getChildren().add(data);
		
		if (name.equals("ISBN: ")) {
			data.getStyleClass().add("isbn");
			data.setOnMouseClicked(e-> {
				controller.detailsPressed(data.getText());
			});
		}
		
		return labelBox;
	}
	private VBox dataCol(ArrayList<HBox> arrayList) {
		VBox col = new VBox();
		for (int i = 0; i < arrayList.size(); i++) {
			col.getChildren().add(arrayList.get(i));
		}
		col.setSpacing(3);
		return col;
	}
	private Button buildBackButton() {
		Button btn = new Button();
		btn.setOnMouseClicked(e->{
			controller.backBtnPressed();
		});
		return btn;
	}
	
	
	private Button buildConfirmBox() {
		Button btn = new Button("Confirm Order");
		btn.setOnMouseClicked(e-> {
			controller.confirm((ArrayList<String>) btn.getUserData());
			updated = true;
			this.buildView(primaryStage);
		});
		
		return btn;
	}
	@Override
	public void setController(ControllerInterface controller) {
		this.controller = (LibraryOrdersController) controller;

	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.mainScene;
	}

	@Override
	public void setUpdated(boolean updated) {
		this.updated = updated;

	}
	private void setup() {
		primaryStage.setTitle("Library Orders");
		root = new VBox();
		root.setSpacing(5);
		mainScene = new Scene(root, 900, 600);
		mainScene.getStylesheets().add("file:resources/stylesheets/LibraryOrders.css");
		primaryStage.setScene(mainScene);
		
	}

}
