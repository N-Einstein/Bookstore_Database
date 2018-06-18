package viewControllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ExceptionHandler.ExceptionHandler;
import dataBaseModel.ISBNValidator;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.AddBooksView;
import view.HomeView;
import view.ViewInterface;

public class AddBookController implements ControllerInterface {
	private AddBooksView bookView;
	private ExceptionHandler excepHandler = ExceptionHandler.getHandler();
	private String exceptionError = "";

	public AddBookController(ViewInterface view) {
		this.bookView = (AddBooksView) view;
		bookView.setController(this);
	}

	@Override
	public SQLResultParser getParser() {
		return null;
	}

	@Override
	public ResultSet getResultSet() {
		return null;
	}

	@Override
	public void switchView(Button btn) {
		HomeView homView = HomeView.getInstance();
		Stage currentStage = bookView.getPrimaryStage();
		homView.buildView(currentStage);
	}

	public boolean addBook(LinkedHashMap<String, TextField> registerText) {

		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		TextField isbn = registerText.get(TablesColsNames.BOOK_ISBN);
		if (!ISBNValidator.getInstance().validate(isbn.getText())) {
			exceptionError = "Not valid ISBN !";
			return false;
		} else {
			isbn.setText(isbn.getText().replaceAll(" ", ""));
			isbn.setText(isbn.getText().replaceAll("-", ""));
			registerText.put(TablesColsNames.BOOK_ISBN, isbn);
		}
		ArrayList<String> stkKeys = new ArrayList<String>();
		ArrayList<String> stkValues = new ArrayList<String>();

		ArrayList<String> authKeys = new ArrayList<String>();
		ArrayList<String> authValues = new ArrayList<String>();

		for (Entry<String, TextField> rt : registerText.entrySet()) {
			if (!rt.getValue().getText().equals("")) {
				if (rt.getKey().equals(TablesColsNames.BOOK_ISBN)) {
					stkKeys.add(rt.getKey());
					stkValues.add("'" + rt.getValue().getText() + "'");
					keys.add(rt.getKey());
					values.add("'" + rt.getValue().getText() + "'");
				} else if (rt.getKey().equals(TablesColsNames.STOCK_QUANTITY)) {
					stkKeys.add(rt.getKey());
					stkValues.add("'" + rt.getValue().getText() + "'");
				} else if (rt.getKey().equals(TablesColsNames.AUTHOR_NAME)) {
					authKeys.add(rt.getKey());
					authValues = new ArrayList<String>(Arrays.asList(rt.getValue().getText().split(",")));
				} else if (rt.getKey().equals(TablesColsNames.STOCK_THRESHOLD)) {
					stkKeys.add(rt.getKey());
					stkValues.add("'" + rt.getValue().getText() + "'");
				} else {
					keys.add(rt.getKey());
					values.add("'" + rt.getValue().getText() + "'");
				}

			}
		}
		// catch exception here
		boolean valid = false;
		if (authValues.size() > 0) {
			DBConnector con = null;
			try {
				con = new DBConnector();
				con.getConnection().setAutoCommit(false);
				insertBook(keys, values, con);
				addToStock(stkKeys, stkValues, con);
				addAuthors(authValues, values.get(keys.indexOf(TablesColsNames.BOOK_ISBN)), con);
				con.getConnection().commit();
				valid = true;
			} catch (Exception e) {
				try {
					excepHandler.handle(e);
				} catch (Exception e2) {
					exceptionError = e2.toString();
				}
			} finally {
				try {
					con.close();
				} catch (SQLException e) {

				}
			}
		} else {
			exceptionError = "ExceptionHandler.NullException:'Author Name doesn't have a default value";
		}

		return valid;

	}

	private void insertBook(ArrayList<String> keys, ArrayList<String> values, DBConnector con) throws SQLException {
		InsertQuery insertQuery = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		insertQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK)));
		insertQuery.addRecord(keys, values);
		insertQuery.build();

		DataEditor dr = new DataEditor(con, insertQuery);
		dr.updateDatabase();

	}

	private void addAuthors(ArrayList<String> authrs, String ISBN, DBConnector con) throws SQLException {

		for (String auth : authrs) {
			InsertQuery insertQuery = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
			insertQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.AUTHOR)));
			insertQuery.addRecord(
					new ArrayList<String>(Arrays.asList(TablesColsNames.AUTHOR_BOOK_ISBN, TablesColsNames.AUTHOR_NAME)),
					new ArrayList<String>(Arrays.asList(ISBN, "'" + auth.replaceAll("'", "\\\\'") + "'")));
			insertQuery.build();
			DataEditor dr = new DataEditor(con, insertQuery);
			dr.updateDatabase();

		}

	}

	private void addToStock(ArrayList<String> keys, ArrayList<String> values, DBConnector con) throws SQLException {
		InsertQuery insertQuery = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		insertQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK)));
		insertQuery.addRecord(keys, values);
		insertQuery.build();
		DataEditor dr = new DataEditor(con, insertQuery);
		dr.updateDatabase();

	}

	private boolean fullStockData(ArrayList<String> keys) {
		return keys.size() == 3;
	}

	public String getError() {
		return exceptionError;
	}

}