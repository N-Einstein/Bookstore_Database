package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

import ExceptionHandler.ExceptionHandler;
import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import dataTransfer.DataRetriever;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import queryGenerator.SelectQuery;
import queryGenerator.UpdateQuery;
import sqlResultsParsers.SQLResultParser;
import sqlResultsParsers.TablesColsNames;

public class PublisherSearchItem {

	SQLResultParser parser;
	ResultSet resultSet;
	String name;
	ViewInterface mainView;

	public PublisherSearchItem() {
	}

	public HBox getPubSearchBox(ResultSet resultSet, SQLResultParser parser, ViewInterface mainView) {
		HBox hbox = new HBox();
		hbox.getStyleClass().add("row");
		this.resultSet = resultSet;
		this.parser = parser;
		this.mainView = mainView;

		buildDetails(hbox);
		addEditButton(hbox);

		return hbox;
	}

	private void buildDetails(HBox hbox) {
		VBox vbox = new VBox();
		name = parser.getResult(TablesColsNames.PUBLISHER_NAME, resultSet);
		vbox.getChildren()
				.add(buildDetailsString(TablesColsNames.PUBLISHER_NAME, name, TablesColsNames.PUBLISHER_NAME + name));
		vbox.getChildren()
				.add(buildDetailsString(TablesColsNames.PUBLISHER_ADDRESS,
						parser.getResult(TablesColsNames.PUBLISHER_ADDRESS, resultSet),
						TablesColsNames.PUBLISHER_ADDRESS + name));
		vbox.getChildren()
				.add(buildDetailsString(TablesColsNames.PUBLISHER_TELEPHONE,
						parser.getResult(TablesColsNames.PUBLISHER_TELEPHONE, resultSet),
						TablesColsNames.PUBLISHER_TELEPHONE + name));

		hbox.getChildren().add(vbox);

	}

	private void addEditButton(HBox hbox) {
		Button edit = new Button("edit");
		String id = name;
		while (id.contains(".")) {
			id = id.replace('.', ' ');
		}
		id.replaceAll(" ", "");
		edit.setId(id);
		edit.setUserData(name);
		edit.setOnMouseClicked(e -> {
			if (edit.getText().equals("edit")) {
				edit.setText("save");
				flipView(TablesColsNames.PUBLISHER_NAME + edit.getId());
				flipView(TablesColsNames.PUBLISHER_ADDRESS + edit.getId());
				flipView(TablesColsNames.PUBLISHER_TELEPHONE + edit.getId());

			} else {
				try {
					updateValues(edit.getUserData().toString());
					edit.setText("edit");
					flipView(TablesColsNames.PUBLISHER_NAME + edit.getId());
					flipView(TablesColsNames.PUBLISHER_ADDRESS + edit.getId());
					flipView(TablesColsNames.PUBLISHER_TELEPHONE + edit.getId());
				} catch (Exception e1) {
					Alert a = new Alert(AlertType.ERROR);
					;
					a.setContentText(e1.getMessage());
					a.setTitle("");
					a.setHeaderText("");
					a.showAndWait();
				}

			}

		});

		Region gap = new Region();
		hbox.setHgrow(gap, Priority.ALWAYS);
		hbox.getChildren().add(gap);
		hbox.getChildren().add(edit);
	}

	private void flipView(String id) {
		while (id.contains(".")) {
			id = id.replace('.', ' ');
		}
		id = id.replaceAll(" ", "");
		Label l = (Label) mainView.getScene().lookup("#l" + id);
		l.setVisible(!l.isVisible());

		TextField t = (TextField) mainView.getScene().lookup("#f" + id);
		t.setVisible(!t.isVisible());

		if (l.isVisible()) {
			l.setText(t.getText());
		} else {
			t.setText(l.getText());
		}

	}

	private HBox buildDetailsString(String label, String value, String id) {
		HBox box = new HBox();
		while (id.contains(".")) {
			id = id.replace('.', ' ');
		}
		id = id.replaceAll(" ", "");
		box.getStyleClass().add("details-hbox");
		Label mlabel = new Label(label + ": ");
		mlabel.getStyleClass().add("label1");

		Label mValue = new Label(value);
		mValue.getStyleClass().add("value");
		mValue.setWrapText(true);
		mValue.setId("l" + id);
		mValue.managedProperty().bind(mValue.visibleProperty());

		TextField tvalue = new TextField(value);
		tvalue.getStyleClass().add("value");
		tvalue.setId("f" + id);
		tvalue.setVisible(false);
		tvalue.managedProperty().bind(tvalue.visibleProperty());
		tvalue.setMinWidth(tvalue.getText().length() * 7);
		tvalue.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tvalue.setMinWidth(tvalue.getText().length() * 7);

			}
		});

		box.getChildren().add(mlabel);
		box.getChildren().add(mValue);
		box.getChildren().add(tvalue);
		return box;
	}

	private void updateValues(String id) throws Exception {
		UpdateQuery q = (UpdateQuery) QueryFactory.getInstance().makeQuery(QueryType.UPDATE);
		q.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.PUBLISHER)));
		q.addConditions(new ArrayList<String>(Arrays.asList(TablesColsNames.PUBLISHER_NAME + " = " + "'" + id + "'")));
		Map<String, String> m = new HashMap<>();
		String value;
		// name
		value = getTextValue(TablesColsNames.PUBLISHER_NAME + id);
		value = value.replaceAll("'", "\\\\'");
		m.put(TablesColsNames.PUBLISHER_NAME, "'" + value + "'");
		// address
		value = getTextValue(TablesColsNames.PUBLISHER_ADDRESS + id);
		value = value.replaceAll("'", "\\\\'");
		m.put(TablesColsNames.PUBLISHER_ADDRESS, "'" + value + "'");
		// name
		value = getTextValue(TablesColsNames.PUBLISHER_TELEPHONE + id);
		value = value.replaceAll("'", "\\\\'");
		m.put(TablesColsNames.PUBLISHER_TELEPHONE, "'" + value + "'");

		// add to query
		try {
			q.addUpdates(m);
			q.build();
			DBConnector con = new DBConnector();
			DataEditor de = new DataEditor(con, q);
			de.updateDatabase();
		} catch (Exception e) {
			ExceptionHandler.getHandler().handle(e);
		}

	}

	private String getTextValue(String id) {
		while (id.contains(".")) {
			id = id.replace('.', ' ');
		}
		id = id.replaceAll(" ", "");
		TextField tf = (TextField) mainView.getScene().lookup("#f" + id);
		return tf.getText();
	}

}
