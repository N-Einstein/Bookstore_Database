package viewControllers;

import java.sql.ResultSet;

import javafx.scene.control.Button;
import sqlResultsParsers.SQLResultParser;

public interface ControllerInterface {
	

	public SQLResultParser getParser();
	public ResultSet getResultSet();
	void switchView(Button btn);
	
}
