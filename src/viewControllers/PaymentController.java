package viewControllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import dataBaseModel.User;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import queryGenerator.InsertQuery;
import queryGenerator.Query;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import queryGenerator.UpdateQuery;
import sqlResultsParsers.SQLResultParser;
import view.CartView;
import view.HomeView;
import view.PaymentView;
import view.ViewInterface;
import viewControllers.ControllerInterface;

public class PaymentController implements ControllerInterface {

	private PaymentView paymentView;
	private ResultSet resultSet;
	private SQLResultParser parser;

	private String orderId;

	public PaymentController(ViewInterface view) {
		paymentView = (PaymentView) view;
		view.setController(this);
		parser = new SQLResultParser();
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
		switch (btn.getId()) {
		case "cancel_btn":
			CartView cartView = CartView.getInstance();
			cartView.buildView(paymentView.getPrimaryStage());
			break;
		case "pay_btn":
			update();
			HomeView homeView = HomeView.getInstance();
			HomeController homeController = new HomeController(homeView);
			homeView.buildView(paymentView.getPrimaryStage());
			break;
		}
	}

	public synchronized void update() {
		DBConnector con = null;
		try {
			con = new DBConnector();
			con.getConnection().setAutoCommit(false);
			updateUsersOrders(con);
			updateStocks(con);
			updateReciept(con);
			User.cartISBN.clear();
			con.getConnection().commit();
		} catch (Exception e) {
			paymentView.showAlert("synch");
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean updateReciept(DBConnector con) throws ClassNotFoundException, SQLException {
		for (String x : User.cartISBN.keySet()) {
			updateReciept(con, x, String.valueOf(User.cartISBN.get(x)));
		}
		return true;
	}

	private boolean updateUsersOrders(DBConnector con) throws ClassNotFoundException, SQLException {

		Calendar calendar = Calendar.getInstance();
		Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());

		CartView cartView = CartView.getInstance();
		CartViewController cartController = new CartViewController(cartView, paymentView);

		InsertQuery q = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		q.setTables(new ArrayList<String>(Arrays.asList("USERS_ORDERS")));
		q.addRecord(new ArrayList<String>(Arrays.asList("UserId", "Date", "TotalPrice")),
				new ArrayList<String>(Arrays.asList(String.valueOf(User.getInstance().getId()),
						"\'" + currentTimestamp.toString() + "\'",
						"\'" + Double.toString(cartController.getTotalPayment()) + "\'")));
		editDataBase(con, q);

		setOrderId(con);
		return true;
	}

	// -- set orderId field to be used in RECIEPT insertion
	private void setOrderId(DBConnector con) throws ClassNotFoundException, SQLException {
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<>(Arrays.asList("USERS_ORDERS")));
		sq.addConditions(new ArrayList<String>());
		sq.build();

		DataRetriever dr = new DataRetriever(con, sq);
		resultSet = dr.getReult();
		parser.setupParser(resultSet);
		resultSet.last();
		orderId = parser.getResult("OrderId", resultSet);
	}

	private void updateReciept(DBConnector con, String num, String quantity)
			throws ClassNotFoundException, SQLException {
		InsertQuery q = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		q.setTables(new ArrayList<String>(Arrays.asList("RECIEPT")));

		q.addRecord(new ArrayList<String>(Arrays.asList("OrderId", "ISBN", "Quantity")),
				new ArrayList<String>(Arrays.asList(orderId, num, quantity)));
		editDataBase(con, q);
	}

	private boolean updateStocks(DBConnector con) throws ClassNotFoundException, SQLException {
		HashMap<String, String> map;
		for (String x : User.cartISBN.keySet()) {

			UpdateQuery q = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
			q.setTables(new ArrayList<String>(Arrays.asList("STOCK")));
			map = new HashMap<String, String>();
			map.put("Quantity", "Quantity - " + String.valueOf(User.cartISBN.get(x)));
			q.addUpdates(map);
			q.addConditions(new ArrayList<String>(Arrays.asList("ISBN = \'" + x + "\'")));
			editDataBase(con, q);
		}
		return true;
	}

	private void editDataBase(DBConnector con, Query q) throws ClassNotFoundException, SQLException {
		q.build();
		DataEditor edit = new DataEditor(con, q);
		edit.updateDatabase();
	}
}