package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dataBaseModel.User;
import dataTransfer.DBConnector;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.CartView;
import view.PaymentView;
import view.ViewInterface;

public class CartViewController implements ControllerInterface {

	private SQLResultParser parser;
	private ResultSet ResultSet;
	private CartView cartView;
	private ViewInterface prevView;
	private Iterator iterator;
	public boolean updated;

	public CartViewController(CartView cartView, ViewInterface prevView) {
		this.cartView = cartView;
		this.prevView = prevView;
		cartView.setController(this);
		parser = new SQLResultParser();

	}

	@Override
	public SQLResultParser getParser() {
		return parser;
	}

	@Override
	public ResultSet getResultSet() {
		return ResultSet;
	}

	public void backButtonPressed() {
		prevView.setUpdated(updated);
		prevView.buildView(cartView.getPrimaryStage());
		updated = false;
	}

	public ResultSet getNextRowResultSet() {
		ResultSet rs;
		Map.Entry<String, Integer> en;
		if (iterator.hasNext()) {
			en = (Entry<String, Integer>) iterator.next();
		} else {
			iterator = User.cartISBN.entrySet().iterator();
			return null;
		}
		try {
			SelectQuery q = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
			q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK)));
			q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK_ISBN + " = '" + en.getKey() + "'")));
			q.build();
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, q);
			rs = dr.getReult();
			parser.setupParser(rs);
			rs.next();
			return rs;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private double  getPrice(String ISBN) {
		SelectQuery q = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK)));
		q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.BOOK_ISBN + " = '" + ISBN + "'")));
		q.build();
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, q);
			ResultSet resultSet = dr.getReult();
			parser.setupParser(resultSet);
			resultSet.next();
			double price = Double.parseDouble(parser.getResult(TablesColsNames.BOOK_PRICE, resultSet));

			return price;
		} catch (Exception e) {
			e.printStackTrace();
			return Double.MIN_VALUE;
		}
	}

	public void updateQuantity(int i, String isbn) {
		int currentInStock  = getStock(isbn);
		String totalPriceLabel = cartView.getLabelText(cartView.TOTAL_PRICE_ID);
		totalPriceLabel = totalPriceLabel.substring(0, totalPriceLabel.length() - 1);
		double totalPayments = Double.parseDouble(totalPriceLabel);
		double price = getPrice(isbn);
		price = Double.parseDouble(User.decimarFormatter.format(price));
		double newTotalPrice;
		int oldQuantity = User.cartISBN.get(isbn);
		int newQuantity = oldQuantity + i;

		if (i == 0) { // deleted
			newTotalPrice = totalPayments - price * oldQuantity;
		} else if (newQuantity <= 0 || newQuantity > currentInStock) {
			return;
		} else {
			User.cartISBN.replace(isbn, newQuantity);
			newTotalPrice = totalPayments + i * price;
			cartView.updateTextField("q" + isbn, Integer.toString(newQuantity)); // book quantity
			cartView.updateTextField("p" + isbn, User.decimarFormatter.format(price * newQuantity) + "$"); // total price of this
																								// book
		}
		cartView.updateTextField(cartView.TOTAL_PRICE_ID, User.decimarFormatter.format(newTotalPrice) + "$");
	}

	private int getStock(String isbn) {
		SelectQuery sq = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		sq.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.STOCK)));
		sq.addConditions(new ArrayList<>(Arrays.asList(TablesColsNames.STOCK_ISBN + "= '" + isbn + "'")));
		sq.build();
		try {
			DBConnector con = new DBConnector();
			DataRetriever dr = new DataRetriever(con, sq);
			ResultSet r = dr.getReult();
			SQLResultParser p = new SQLResultParser();
			p.setupParser(r);
			if (r.next()) {
				return Integer.parseInt(p.getResult(TablesColsNames.STOCK_QUANTITY, r));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	public double getTotalPayment() {
		double total = 0;
		Iterator it = User.cartISBN.entrySet().iterator();
		Map.Entry<String, Integer> en;
		while (it.hasNext()) {
			en = (Entry<String, Integer>) it.next();
			total += getPrice(en.getKey()) * en.getValue();
		}
		return total;
	}

	@Override
	public void switchView(Button btn) {
		// TODO Auto-generated method stub
	}

	public void payButtonPressed() {
		PaymentView pv =  PaymentView.getInstance();
		PaymentController pc = new PaymentController(pv);
		pv.buildView(cartView.getPrimaryStage());
		
	}

	public void setup() {
		iterator = User.cartISBN.entrySet().iterator();
		
	}
}
