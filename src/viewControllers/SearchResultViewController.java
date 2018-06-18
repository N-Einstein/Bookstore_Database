package viewControllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dataBaseModel.User;
import dataTransfer.DBConnector;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.BookDetailsView;
import view.SearchResultView;
import view.ViewInterface;

public class SearchResultViewController implements ControllerInterface{
	private ResultSet resultSet;
	private SearchResultView searchResultView;
	private SQLResultParser parser;
	private DBConnector con;
	private SelectQuery sq;
	SelectQuery sqq;
	private int offset;
	private final int LIMIT = 10;
	
	public SearchResultViewController(ViewInterface view, SelectQuery sq) {
		this.searchResultView = (SearchResultView) view;
        System.out.println(sq);
		searchResultView.setController(this);
		this.sq = sq;
		
	}
	public void setupController() {
		// temp
				sqq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
				sqq.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.BOOK)));
				sqq.addConditions(new ArrayList<String>());
		
		offset = 0;
		parser = new SQLResultParser();
		performQuery();

	}
	
	
	
	private void performQuery() {
		// query must load 10 by 10 
		SelectQuery copyQuery = copyQuery(sq);
		copyQuery.setLimit(Integer.toString(LIMIT));
		copyQuery.build();
		try {
			con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, copyQuery);
			resultSet = dr.getReult();
			parser.setupParser(resultSet);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadNextPage() {
		// increment offset
		SelectQuery copyQuery = copyQuery(sq);
		offset += LIMIT;
		copyQuery.setLimit(Integer.toString(LIMIT));
		copyQuery.setOffset(Integer.toString(offset));
		copyQuery.build();
		
		try {
			con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, copyQuery);
			resultSet = dr.getReult();
			parser.setupParser(resultSet);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public ResultSet getNextRow() {
		
		try {
			if(resultSet.next()) {
				return resultSet;
			}  else {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void addToCart(String isbn) {
		// get stock 
		int inStock = Integer.parseInt(getNoInStock(isbn));
		if (inStock <= 0) {
			Label msg = (Label) searchResultView.getScene().lookup("#msg2"+isbn);
			msg.setVisible(true);
			return;
		}
		if (!User.cartISBN.containsKey(isbn)) {
			User.cartISBN.put(isbn, 1);
			searchResultView.getScene().lookup("#msg"+isbn).setVisible(true);
		}
		
		
	}

	public String getNoInStock(String isbn) {
		SelectQuery q = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK)));
		q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK_ISBN + " = '" + isbn + "'")));
		q.build();
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, q);
			ResultSet rs = dr.getReult();
			parser.setupParser(rs);
			if(rs.next()) {
				return parser.getResult(TablesColsNames.STOCK_QUANTITY, rs);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return "";
	}

	public void getdetails(String ISBN, ViewInterface prevView) {
		BookDetailsView bookDetailsView = new BookDetailsView();
		BookDetailsController bookDetailsController = new BookDetailsController(bookDetailsView, ISBN, prevView);
		bookDetailsView.buildView(searchResultView.getPrimaryStage());
	}
	@Override
	public SQLResultParser getParser() {
		return parser;
	}
	@Override
	public ResultSet getResultSet() {
		return resultSet;
	}
	@Override
	public void switchView(Button btn) {		
	}
	

	private SelectQuery copyQuery(SelectQuery src) {
		SelectQuery copyQuery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		copyQuery.setTables(src.getTables());
		copyQuery.addConditions(src.getConditions());
		copyQuery.getConditions().remove(copyQuery.getConditions().size()-1);
		copyQuery.setOrder(src.getOrder());
		return copyQuery;
	}


}
