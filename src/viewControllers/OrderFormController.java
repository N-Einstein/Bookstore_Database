package viewControllers;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ExceptionHandler.ExceptionHandler;
import dataBaseModel.User;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.OrderForm;
import view.ViewInterface;

public class OrderFormController implements ControllerInterface{
	private OrderForm orderForm;
	private ResultSet resultSet;
	public String ISBN;
	private SQLResultParser parser;
	private boolean updated;
	private ViewInterface prevView;
	public boolean known = false;
	private final String ALERT_ISBN_EMPTY = "The ISBN is empty"; 
	private final String ALERT_ISBN_NOT_CORRECT = "The ISBN is not correct"; 
	private final String ALERT_QUANTITY = "The quantity is not a correct number!"; 
	private final String ALERT_SUCCESS = "Order placed Successfully!";
	private enum alertType{error, success};
	
	public OrderFormController(String ISBN,ViewInterface view, ViewInterface prevView) { // order specific book
		this.ISBN = ISBN;
		this.prevView = prevView;
		this.orderForm = (OrderForm) view;
		known = true;
		setup();
	}
	
	public OrderFormController(ViewInterface view, ViewInterface prevView) { // order form in general
		this.prevView = prevView;
		this.orderForm = (OrderForm) view;
		known = false;
		setup();
	}
	
	private void setup() {
		// link view
		orderForm.setController(this);
		// build view
		
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

	public void backPressed() {
		prevView.buildView(orderForm.getPrimaryStage());
		
	}

	public void confirmOrder() {
		String isbn;
		int quantity;
		try { 
			isbn =((TextField) orderForm.getScene().lookup("#" + orderForm.ISBN_ID)).getText();
			if (isbn.isEmpty()) {
				alert(ALERT_ISBN_EMPTY, alertType.error);
				return;
			}
		} catch (Exception e2) {
			alert(ALERT_ISBN_NOT_CORRECT, alertType.error);
			return;
		}
		
		try {
			quantity =Integer.parseInt(((TextField) orderForm.getScene().lookup("#" + orderForm.QUANTITY_ID)).getText());
		} catch (Exception e3) {
			alert(ALERT_QUANTITY, alertType.error);
			return;
		}
		
		if (quantity <= 0) {
			alert(ALERT_QUANTITY, alertType.error);
			return;
		}
		
		// query to insert
		String userId = Integer.toString(User.getInstance().getId());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		
		InsertQuery q = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.LIBRARY_ORDERS)));
		ArrayList<String> cols = new ArrayList<>(Arrays.asList(
				TablesColsNames.LIBRARY_ORDERS_USER_ID, TablesColsNames.LIBRARY_ORDERS_ISBN, 
				TablesColsNames.LIBRARY_ORDERS_QUANTITY, TablesColsNames.LIBRARY_ORDERS_DATE));
		ArrayList<String> values = new ArrayList<>(Arrays.asList(
				"'" + userId + "'", "'" + isbn + "'", Integer.toString(quantity), "'" + date + "'"));
		q.addRecord(cols, values);
		q.build();
		DBConnector con;
		try {
			con = new DBConnector();
			DataEditor de = new DataEditor(con, q);
			de.updateDatabase();
			con.close();
		} catch (Exception e) {
			try {
				ExceptionHandler.getHandler().handle(e);
			} catch (Exception e2) {
				alert(e2.getMessage(), alertType.error);
				return;
			}
		} 
		
		alert(ALERT_SUCCESS, alertType.success);
		
		
	}
	
	private void alert(String msg, alertType type) {
		Alert a = null;
		if (type.equals(alertType.error)) {
			a = new Alert(AlertType.ERROR);
		} else if (type.equals(alertType.success)) {
			a = new Alert(AlertType.INFORMATION);;
		}
		a.setContentText(msg);
		a.setTitle("");
		a.setHeaderText("");
		a.showAndWait();
		
	}

}
