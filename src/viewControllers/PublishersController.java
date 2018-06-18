package viewControllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dataTransfer.DBConnector;
import dataTransfer.DataRetriever;
import javafx.scene.control.Button;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.AddPublisherView;
import view.ViewInterface;
import view.publishersView;

public class PublishersController implements ControllerInterface{
	
	
	// view
	publishersView publishersView;
	ViewInterface prevView;
	
	// controller
	ResultSet resultSet;
	SQLResultParser parser;
	
	// database
	SelectQuery selquery;
	int offset;
	private static final int LIMIT = 10;
	DBConnector con;
	
	public PublishersController(ViewInterface publishersView, ViewInterface prevView) {
		this.publishersView = (view.publishersView) publishersView;
		this.prevView = prevView;
		publishersView.setController(this);
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
		switch (btn.getText()) {
		case "back":
			prevView.buildView(publishersView.getPrimaryStage());
			break;
		case "":
			AddPublisherView apv = AddPublisherView.getInstance();
			AddPublisherController apc = new AddPublisherController(apv, publishersView);
			apv.buildView(publishersView.getPrimaryStage());
			break;
		default:
			break;
				
		
		}
		
	}

	public void selectPublishers(String searchItem, String text) {
		performQuery(searchItem, text);
	}

	public void setupController() {
		parser = new SQLResultParser();
		performQuery(null, null);
	}
	
	public void performQuery(String name, String value) {
		offset = 0;
		selquery = (SelectQuery) QueryFactory.getInstance().makeQuery(QueryType.SELECT);
		selquery.setTables(new ArrayList<>(Arrays.asList(TablesColsNames.PUBLISHER)));
		if (value == null || name == null || value.isEmpty()) { // all
			selquery.addConditions(new ArrayList<String>());	
		}else {
			selquery.addConditions(new ArrayList<String>(Arrays.asList(name + " like " + "'%" + value.replaceAll("'", "\\\\'")+"%'")));
		}
		SelectQuery copyQuery = copyQuery(selquery);
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
		SelectQuery copyQuery = copyQuery(selquery);
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
			if (resultSet.next()) {
				return resultSet;
			} else {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
