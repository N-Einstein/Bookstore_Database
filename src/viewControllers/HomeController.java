package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.AddBooksView;
import view.CartView;
import view.HomeView;
import view.LibraryOrders;
import view.LogInView;
import view.ProfileView;
import view.SearchResultView;
import view.UserListView;
import view.ViewInterface;
import view.publishersView;

public class HomeController implements ControllerInterface {
	private HomeView homeView;
	private SQLResultParser parser;
	private ResultSet resultSet;

	public HomeController(ViewInterface view) {
		this.homeView = (HomeView) view;
		homeView.setController(this);
		parser = new SQLResultParser();
	}

	@Override
	public SQLResultParser getParser() {
		return this.parser;
	}

	@Override
	public ResultSet getResultSet() {
		return this.resultSet;
	}

	@Override
	public void switchView(Button btn) {
  
	}

	public void selectBooks(String field, String attribute) {
		if(field != null){
		SelectQuery selquery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		if(field.equals(TablesColsNames.BOOK_AUTHOR)){
			selectWithJoin(selquery, attribute);
		}else{
			selectWithNoJoin(selquery, field, attribute);
			
		}
		selquery.build();
		ViewInterface searchView = (SearchResultView) SearchResultView.getInstance();
		ControllerInterface searchController = new SearchResultViewController(searchView, selquery);
		searchView.buildView(homeView.getPrimaryStage());
		}

	}
	private void selectWithNoJoin(SelectQuery selquery,String field, String attribute){
		selquery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.BOOK)));
		selquery.addConditions(new ArrayList<String>(Arrays.asList(field + " like " + "'"+attribute.replaceAll("'", "\\\\'")+"'")));
		
	}
	private void selectWithJoin(SelectQuery selquery, String attribute){
		selquery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.BOOK, TablesColsNames.AUTHOR)));
		ArrayList<String> condition = new ArrayList<String>();
		condition.add("BookISBN = ISBN");
		condition.add("AuthorName like " + "'"+attribute.replaceAll("'", "\\\\'")+"'");
		selquery.addConditions(condition);
	}

	public void switchView(String switchType) {
		if (switchType.equals("Log Out")) {
			LogInView logView = LogInView.getInstance();
			Stage currentStage = homeView.getPrimaryStage();
			Scene otherScene = logView.getScene();
			currentStage.setScene(otherScene);
		}else if(switchType.equals("Profile")){
			ProfileView profView = ProfileView.getInstance();
			Stage currentStage = homeView.getPrimaryStage();
			ProfileController profController = new ProfileController(profView);	
			profView.buildView(currentStage);
		} else if(switchType.equals("cart-btn")){
			CartView cartView = CartView.getInstance();
			CartViewController cartViewController = new CartViewController(cartView, homeView);
			cartView.buildView(homeView.getPrimaryStage());
		} else if(switchType.equals("Library Orders")){
			LibraryOrders libraryOrdersView = LibraryOrders.getInstance();
			LibraryOrdersController libraryOrdersController = new LibraryOrdersController(libraryOrdersView, homeView);
			libraryOrdersView.buildView(homeView.getPrimaryStage());
		} else if(switchType.equals("Publishers")){
			publishersView pv = publishersView.getInstance();
			PublishersController pc = new PublishersController(pv, homeView);
			pv.buildView(homeView.getPrimaryStage());
		}else if(switchType.equals("Users")){
			UserListView userView = UserListView.getInstance();
			UserListController usercontroller = new UserListController(userView);
			Stage current = homeView.getPrimaryStage();
			userView.buildView(current);
		}else if(switchType.equals("Add Books")){
			AddBooksView bookView = AddBooksView.getInstance();
			AddBookController bookcontroller = new AddBookController(bookView);
			bookView.buildView(homeView.getPrimaryStage());
		}
		
		
	}

	public void addRandomBooks() {
		// generate random books
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.BOOK)));
		sq.addConditions(new ArrayList<String>());
		ViewInterface searchView = (SearchResultView) SearchResultView.getInstance();
		ControllerInterface searchController = new SearchResultViewController(searchView, sq);
		searchView.buildView(homeView.getPrimaryStage());
		
	}

}
