package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ExceptionHandler.ExceptionHandler;
import dataBaseModel.ISBNValidator;
import dataBaseModel.User;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import queryGenerator.DeleteQuery;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import queryGenerator.UpdateQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.BookDetailsView;
import view.OrderForm;
import view.ViewInterface;

public class BookDetailsController implements ControllerInterface {
	
	private BookDetailsView bookDetailsView;
	private ResultSet resultSet;
	private String ISBN;
	private SQLResultParser parser;
	private boolean updated;
	private ViewInterface prevView;

	public BookDetailsController(ViewInterface view, String ISBN, ViewInterface prevView) {
		bookDetailsView = (BookDetailsView) view;
		this.prevView = prevView;
		view.setController(this);
		this.ISBN = ISBN;	
	}
	
	public void performQuery() {
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.BOOK)));
		sq.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK_ISBN + " = '" + ISBN + "'")));
		sq.build();
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, sq);
			resultSet = dr.getReult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new SQLResultParser();
		parser.setupParser(resultSet);
		try {
			resultSet.next();
		} catch (Exception e){}
		
	}
	public void backPressed() {
		prevView.setUpdated(updated);
		prevView.buildView(bookDetailsView.getPrimaryStage());
		updated = false;
	}
	
	
	public ResultSet getResultSet() {
		return resultSet;
	}
	public SQLResultParser getParser() {
		return parser;
	}
	public String getAuthorsAsString() {
		SelectQuery sqlQuery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sqlQuery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.AUTHOR)));
		sqlQuery.addConditions(new ArrayList<>(Arrays.asList(TablesColsNames.AUTHOR_BOOK_ISBN + "= '" + ISBN + "'")));
		sqlQuery.build();
		SQLResultParser sqlp2 = new SQLResultParser();
		ResultSet authorResult;
		
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, sqlQuery);
			authorResult = dr.getReult();
			String authors = "";
			sqlp2.setupParser(authorResult);
			while(authorResult.next()) {
				authors += sqlp2.getResult(TablesColsNames.AUTHOR_NAME, authorResult);
				if (!authorResult.isLast()) {
					authors += ", ";
				}				
			}
			
			return authors;

		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void addToCart(String isbn) {
		// if stock is empty 
		int currentStock;
		
		TextField stockField = (TextField)  bookDetailsView.getScene().lookup("#fstock-field");
		currentStock = Integer.parseInt(stockField.getText().toString());
		if (currentStock == 0 ) {
			Label msg = (Label) bookDetailsView.getScene().lookup("#msg" + isbn);
			msg.setText("The stock is empty right now!");
			bookDetailsView.getScene().lookup("#msg" + isbn).setVisible(true);
			return;
		}
		if (!User.cartISBN.containsKey(isbn)) {
			User.cartISBN.put(isbn, 1);
			
		}
		bookDetailsView.getScene().lookup("#msg" + isbn).setVisible(true);
		


	}
	@Override
	public void switchView(Button btn) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateBookTable() throws Exception {
		UpdateQuery q = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK)));
		q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK_ISBN + " = " + "'" + ISBN + "'")));
		Map<String, String> m= new HashMap<>();
		String value;
		String id;
		String newIsbn;

		// category
		id = TablesColsNames.BOOK_CATEGORY;
		value = getTextValue(id);
		value = value.replaceAll("'", "\\\\'");
		m.put(id, "'" + value + "'");
		// name
		id = TablesColsNames.BOOK_TITLE;
		value = getTextValue(id);
		value = value.replaceAll("'", "\\\\'");
		m.put(id, "'" + value + "'");
		// isbn
		id = TablesColsNames.BOOK_ISBN;
		value = getTextValue(id);
		value = value.replaceAll("'", "\\\\'");
		if (!ISBNValidator.getInstance().validate(value)) {
			Exception e = new Exception("ISBN is not correct!");
			throw e;
		}
		value = value.replaceAll( "-", "" );
		value = value.replaceAll( " ", "" );
		newIsbn = value;
		m.put(id, "'" + value + "'");
		// price
		id = TablesColsNames.BOOK_PRICE;
		value = getTextValue(id);
		value = value.replace("$", "");
		value = value.replaceAll("'", "\\\\'");
		m.put(id, value);
		// publisher
		id = TablesColsNames.BOOK_PUBLISHER;
		value = getTextValue(id);
		value = value.replaceAll("'", "\\\\'");
		m.put(id, "'" + value + "'");
		// publication year
		id = TablesColsNames.BOOK_PUBLICATION_YEAR;
		value = getTextValue(id);
		value = value.replaceAll("'", "\\\\'");
		m.put(id, value);
		
//		add to query 
		try{
			q.addUpdates(m);
			q.build();
			DBConnector con = new DBConnector();
			con.getConnection().setAutoCommit(false);
			DataEditor de = new DataEditor(con, q);
			de.updateDatabase();
			// authors
			//remove old authors 
			removeOldAuthors(newIsbn, con);
			// add new authors
			id = TablesColsNames.AUTHOR_NAME;
			value = getTextValue(id);
			value = value.replaceAll("'", "\\\\'");
			ArrayList<String> newAuthors = new ArrayList<>(Arrays.asList(value.split("\\s*,\\s*")));
			addNewAuthors(newAuthors, newIsbn, con);
			con.getConnection().commit();
			con.close();
			
		} catch (Exception e) {
			ExceptionHandler.getHandler().handle(e);
		}

		
		if (User.cartISBN.containsKey(ISBN)) {
			int prevQuantity = User.cartISBN.get(ISBN);
			User.cartISBN.remove(ISBN);
			User.cartISBN.put(newIsbn, prevQuantity);
		}
		ISBN = newIsbn;
		bookDetailsView.setUpdated(true);
		
	}
	private void removeOldAuthors(String isbn, DBConnector con2) throws Exception {
		DeleteQuery q = (DeleteQuery) QueryFactory.getInstance().makeQuery(QueryType.DELETE);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.AUTHOR)));
		q.addConditions(new ArrayList<>(Arrays.asList(TablesColsNames.AUTHOR_BOOK_ISBN + " = '" + isbn + "'")));
		q.build();
		DataEditor de = new DataEditor(con2, q);
		de.updateDatabase();
		
	}
	private void addNewAuthors(ArrayList<String> newAuthors, String isbn, DBConnector con2) throws Exception {

		for (int i = 0; i < newAuthors.size(); i++) {
			if (newAuthors.get(i).isEmpty()) {
				continue;
			}
			InsertQuery q = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
			q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.AUTHOR)));
			q.addRecord(new ArrayList<String>(Arrays.asList(TablesColsNames.AUTHOR_BOOK_ISBN, TablesColsNames.AUTHOR_NAME, TablesColsNames.AUTHOR_VALID)), 
					    new ArrayList<String>(Arrays.asList("'" + isbn + "'", "'" + newAuthors.get(i) + "'", "1")));	
			q.build();
			DataEditor de = new DataEditor(con2, q);
			de.updateDatabase();
		}
		

		
		
	}

	
	private String getTextValue(String id) {
		TextField tf = (TextField) bookDetailsView.getScene().lookup("#f" + id);
		return tf.getText();
	}
	public void order() {
		OrderForm orderForm = OrderForm.getInstance();
		OrderFormController ofc = new OrderFormController(ISBN, orderForm, bookDetailsView);
		orderForm.buildView(bookDetailsView.getPrimaryStage());
		
	}
	public String getNoInStock() {
		SelectQuery q = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK)));
		q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK_ISBN + " = '" + ISBN + "'")));
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
}
