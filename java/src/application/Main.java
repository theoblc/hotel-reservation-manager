package application;

import application.controller.onglets.*;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		Parent root;

		try {
			// On récupère le chemin absolu vers Main.fxml qui coordonne tous les fxml
			URL fxmlLocation = getClass().getResource("fxml/onglets/Authentification.fxml");
			// On charge Main.fxml
			FXMLLoader loader = new FXMLLoader(fxmlLocation);
			loader.setLocation(fxmlLocation);
			root = loader.load();

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Scene scene = new Scene(root);
		primaryStage.setTitle("Authentification");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}