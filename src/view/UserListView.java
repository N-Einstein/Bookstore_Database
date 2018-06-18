package view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import sqlResultsParsers.TablesColsNames;
import viewControllers.ControllerInterface;
import viewControllers.UserListController;
import viewControllers.UserListController.User;

public class UserListView implements ViewInterface{

	private TableView<User> table;
	private TableColumn actionCol;
	private final ObservableList<User> data = FXCollections.observableArrayList();
	private HomeView homeView = HomeView.getInstance();
	private UserListController controller;
	private Stage primaryStage;
	private Scene tableScn;
	private HBox searchBar;
	private HBox header;
	private TextField search;
	private Button searchBtn;
	private Button home;
	private ComboBox<String> comboFilter;
	private String searchItem = "", fieldItem = "";
	private VBox root;	
	private int offset = 0, limit = 10, initialSize = 50, lastUpdate = 0;
	private boolean selectAll = false;
    private static UserListView inst = new UserListView();
    private UserListView(){
    	
    }
    public static UserListView getInstance(){
    	return inst;
    }
    private void refresh(){
		data.remove(0, data.size());
        offset = 0;
        lastUpdate = 0;
		selectAll = false;

	}
	private void searchUser(String field) {
		refresh();
		fieldItem = field;
		if(searchItem.equals("") || searchItem.equals("ALL")){
			selectAll = true;
		}
		ArrayList<User> initialList = controller.getLazyLoadList(searchItem, fieldItem,initialSize, offset, selectAll);
		for (User user : initialList) {
			data.add(user);
		}
		offset += initialList.size();

	}


	private ScrollBar getTableViewScrollBar(TableView<?> tab) {
		ScrollBar scrollbar = null;
		for (Node node : tab.lookupAll(".scroll-bar")) {
			if (node instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) node;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					scrollbar = bar;
				}
			}
		}
		return scrollbar;
	}

	private void buildTable() {
		TableColumn<User, String> c0 = new TableColumn<User, String>("ID");
		TableColumn<User, String> c1 = new TableColumn<User, String>("First Name");
		TableColumn<User, String> c2 = new TableColumn<User, String>("Last Name");
		TableColumn<User, String> c3 = new TableColumn<User, String>("Email");
		TableColumn<User, String> c4 = new TableColumn<User, String>("User Name");
		TableColumn<User, String> c5 = new TableColumn<User, String>("Phone");
		TableColumn<User, String> c6 = new TableColumn<User, String>("Address");
		TableColumn<User, String> c7 = new TableColumn<User, String>("Role");
		
		c0.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
		c1.setCellValueFactory(new PropertyValueFactory<User, String>("fname"));
		c2.setCellValueFactory(new PropertyValueFactory<User, String>("lname"));
		c3.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
		c4.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		c5.setCellValueFactory(new PropertyValueFactory<User, String>("phone"));
		c6.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
		c7.setCellValueFactory(new PropertyValueFactory<User, String>("role"));

		actionCol = new TableColumn("PreamtUser");
		table.setPrefSize(1000, 900);
		table.setItems(data);
		table.getColumns().addAll(c0,c1, c2, c3, c4, c5, c6, c7, actionCol);

	}

	private void addScrollObserver() {

		ScrollBar tableViewScrollBar = getTableViewScrollBar(table);
		tableViewScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
			double position = newValue.doubleValue();
			ScrollBar scrollBar = getTableViewScrollBar(table);

			if (position == scrollBar.getMax()) {
				ArrayList<User> lazyLoadList = controller.getLazyLoadList(searchItem, fieldItem,limit, offset, selectAll);
				if (!lazyLoadList.isEmpty()) {
					int listSize = lazyLoadList.size();
					for (int i = 0; i < listSize; i++) {
						data.add(lazyLoadList.get(i));
					}
					offset += listSize;
					for (int i = 0; i < listSize; i++)
						data.remove(0);
					lastUpdate = listSize;
					scrollBar.decrement();
				}
			} else if (position == scrollBar.getMin()) {
				if (offset > initialSize) {
					offset -= lastUpdate;
					ArrayList<User> lazyLoadList = controller.getLazyLoadList(searchItem, fieldItem,lastUpdate, offset - initialSize, selectAll);
					int listSize = lazyLoadList.size();
					
					for (int i = 0; i < listSize; i++) {
						data.add(i, lazyLoadList.get(i));
					}
					for (int i = 0; i < listSize; i++)
						data.remove(data.size() - 1);
					
					lastUpdate = Math.max(listSize,limit);
					scrollBar.increment();
				}

			}
		});
	}

	private void addButtonAction() {
		Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory = new Callback<TableColumn<User, String>, TableCell<User, String>>() {

			@Override
			public TableCell call(final TableColumn<User, String> param) {
				final TableCell<User, String> cell = new TableCell<User, String>() {

					final Button btn = new Button("Preamt");

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							btn.setOnAction(event -> {
								User user = getTableView().getItems().get(getIndex());
								controller.preamtUser(user);
							});
							setGraphic(btn);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		actionCol.setCellFactory(cellFactory);
	}

	@Override
	public void buildView(Stage primaryStage) {
		refresh();
		root = new VBox();
		home = new Button("Home");
		header = new HBox();
		table = new TableView<User>();
		createSerchBar();
		buildTable();
		header.getChildren().addAll(homeView.getMenuBar(), home);
		root.getChildren().addAll(header,searchBar, table);
		tableScn = new Scene(root, 1000, 900);
		primaryStage.setScene(tableScn);
		primaryStage.show();
		addButtonAction();
		addScrollObserver();
		setActions();
		this.primaryStage = primaryStage;
		tableScn.getStylesheets().add("file:resources/stylesheets/toolBar.css");

	}
    private void createSerchBar(){
    	searchBar = new HBox();
		search = new TextField();
		search.setPromptText("search");
		searchBtn = new Button("search");
		comboFilter = new ComboBox<>();
		comboFilter.getItems().addAll("ALL",TablesColsNames.USER_EMAIL,TablesColsNames.USER_ID,TablesColsNames.USER_USER_FNAME, TablesColsNames.USER_USER_LNAME,TablesColsNames.USER_USERNAME, TablesColsNames.USER_ROLE);
		searchBar.getChildren().addAll(search,searchBtn,comboFilter);
    }
	@Override
	public void setController(ControllerInterface controller) {
            this.controller = (UserListController) controller;		
	}

	@Override
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	@Override
	public Scene getScene() {
		return this.tableScn;
	}

	@Override
	public void setUpdated(boolean updated) {
		
	}
	private void setActions(){
		comboFilter.setOnAction( e -> searchItem = comboFilter.getValue());
		searchBtn.setOnAction(e -> {
			searchUser(search.getText());
		});
        home.setOnMouseClicked( e -> {
        	controller.goHome();
        });

	}

}