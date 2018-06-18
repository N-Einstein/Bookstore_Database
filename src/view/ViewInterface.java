package view;

import java.sql.ResultSet;

import javafx.scene.Scene;
import javafx.stage.Stage;
import viewControllers.ControllerInterface;
import viewControllers.SearchResultViewController;

public interface ViewInterface {
	
	public void buildView(Stage primaryStage);
	public void setController(ControllerInterface controller);
	public Stage getPrimaryStage();
	public Scene getScene();
	public void setUpdated(boolean updated);
}
