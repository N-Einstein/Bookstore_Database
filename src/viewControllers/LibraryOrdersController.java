package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import queryGenerator.UpdateQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.BookDetailsView;
import view.ViewInterface;

public class LibraryOrdersController implements ControllerInterface {

	// views
	private ViewInterface prevView;
	private ViewInterface libraryOrdersView;

	// parser
	private SQLResultParser parser;
	private ResultSet resultSet;

	// database
	DBConnector con;

	// booleans

	public LibraryOrdersController(ViewInterface view, ViewInterface prevView) {
		this.prevView = prevView;
		this.libraryOrdersView = view;
		libraryOrdersView.setController(this);

	}

	public void performQuery() {
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.LIBRARY_ORDERS)));
		sq.addConditions(new ArrayList<>());
		sq.build();
		try {
			con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, sq);
			resultSet = dr.getReult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		parser = new SQLResultParser();
		parser.setupParser(resultSet);

	}

	public ResultSet getNextResult() {

		try {
			if (resultSet.next()) {

				return resultSet;
			} else {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		// TODO Auto-generated method stub

	}

	public void backBtnPressed() {
		prevView.buildView(libraryOrdersView.getPrimaryStage());

	}

	public void confirm(ArrayList<String> arrayList) {
		// remove from orders
		UpdateQuery q = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
		q.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.LIBRARY_ORDERS)));
		q.addConditions(new ArrayList<>(
				Arrays.asList(TablesColsNames.LIBRARY_ORDERS_ORDER_ID + " = '" + arrayList.get(2) + "'")));
		Map<String, String> m = new HashMap<>();
		m.put(TablesColsNames.LIBRARY_ORDERS_VALID, "0");
		q.addUpdates(m);
		q.build();
		try {
			DBConnector con2 = new DBConnector();
			DataEditor de2 = new DataEditor(con2, q);
			de2.updateDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get prev stock
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK)));
		sq.addConditions(
				new ArrayList<String>(Arrays.asList(TablesColsNames.STOCK_ISBN + " = '" + arrayList.get(0) + "'")));
		sq.build();
		int prevStock = 0;
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, sq);
			ResultSet rs = dr.getReult();
			SQLResultParser p = new SQLResultParser();
			p.setupParser(rs);
			if (rs.next()) {
				prevStock = Integer.parseInt(p.getResult(TablesColsNames.STOCK_QUANTITY, rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// update stock

		UpdateQuery q2 = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
		q2.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.STOCK)));
		q2.addConditions(new ArrayList<>(Arrays.asList(TablesColsNames.STOCK_ISBN + " = '" + arrayList.get(0) + "'")));
		Map<String, String> m2 = new HashMap<>();
		int stock = prevStock + Integer.parseInt(arrayList.get(1));
		m2.put(TablesColsNames.STOCK_QUANTITY, Integer.toString(stock));
		q2.addUpdates(m2);
		q2.build();
		try {
			DBConnector con2 = new DBConnector();
			DataEditor de2 = new DataEditor(con2, q2);
			de2.updateDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void detailsPressed(String isbn) {
		ViewInterface detailsView = new BookDetailsView();
		ControllerInterface detailsController = (ControllerInterface) new BookDetailsController(detailsView, isbn,
				libraryOrdersView);
		detailsView.buildView(libraryOrdersView.getPrimaryStage());
	}

}
