package viewControllers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

import ExceptionHandler.ExceptionHandler;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import javafx.scene.control.Button;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;
import view.AddPublisherView;
import view.ViewInterface;

public class AddPublisherController implements ControllerInterface {

	// view
	AddPublisherView addPublishersView;
	ViewInterface prevView;

	// controller
	ResultSet resultSet;
	SQLResultParser parser;

	public AddPublisherController(ViewInterface addPublishersView, ViewInterface prevView) {
		this.addPublishersView = (AddPublisherView) addPublishersView;
		this.prevView = prevView;
		parser = new SQLResultParser();
		addPublishersView.setController(this);
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
		switch (btn.getText()) {
		case "back":
			prevView.buildView(addPublishersView.getPrimaryStage());
			break;
		default:
			break;
		}
	}

	public void addPublisher(String name, String address, String tele) throws Exception {
		name = "'" + name + "'";
		address = "'" + address + "'";
		tele = "'" + tele + "'";
		InsertQuery q = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.PUBLISHER)));
		q.addRecord(new ArrayList<>(Arrays.asList(name, address, tele, "1")));
		q.build();
		try {
			DBConnector db = new DBConnector();
			DataEditor de = new DataEditor(db, q);
			de.updateDatabase();

		} catch (Exception e) {
			ExceptionHandler.getHandler().handle(e);
		}

	}

}
