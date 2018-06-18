package view;

import java.util.ArrayList;
import java.util.Arrays;

import dataTransfer.DBConnector;
import dataTransfer.DataEditor;
import queryGenerator.InsertQuery;
import queryGenerator.QueryFactory;
import queryGenerator.QueryType;
import sqlResultsParsers.TablesColsNames;

public class Insersion {

	public static void main(String[] args) {
        for(int i = 1; i < 500; i++){
        	ArrayList<String>  keys = new ArrayList<String>();
        	ArrayList<String>  values = new ArrayList<String>();
        	keys.add("FName");
        	keys.add("LName");
        	keys.add("UserName");
        	keys.add("Password");
        	keys.add("Role");
        	keys.add("Email");
        	values.add("'"+Integer.toString(i)+"'");
        	values.add("'"+Integer.toString(i)+"'");
        	values.add("'N"+Integer.toString(i)+"'");
        	values.add("'123'");
        	values.add("1");
        	values.add("'EE"+Integer.toString(i) + "@gg.com'");
        	InsertQuery insertQuery = (InsertQuery) QueryFactory.getInstance().makeQuery(QueryType.INSERT);
    		insertQuery.setTables(new ArrayList<String>(Arrays.asList(TablesColsNames.USER)));
    		insertQuery.addRecord(keys, values);
    		insertQuery.build();

    		try {
    			DBConnector con = new DBConnector();
    			DataEditor dr = new DataEditor(con, insertQuery);
    			dr.updateDatabase();
    			con.close();
    		} catch (Exception e) {
    		}
    		

        }
	}

}
