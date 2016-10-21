package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.application.HostServices;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.BorderPane;

/**
 * Main class to start the application.
 * 
 * @author Jake Nelson (github.com/jknlsn)
 */
public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Picnic Pairer");
		
		getHostServices().showDocument("http://www.yahoo.com");
		
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("PicnicPairer.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Scene scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Hello!");
		launch(args);
		// Cleanup here if needed
		System.out.println("Bye!");
	}
}