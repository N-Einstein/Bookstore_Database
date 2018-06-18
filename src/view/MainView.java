package view;

import javafx.application.Application;
import javafx.stage.Stage;
import viewControllers.LogInController;

public class MainView extends Application {

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			LogInView logInView = LogInView.getInstance();
			LogInController logInController = new LogInController(logInView);
			logInView.buildView(primaryStage);

		} catch (Exception e) {
			System.out.println(e);
		}
		primaryStage.show();

	}

}
