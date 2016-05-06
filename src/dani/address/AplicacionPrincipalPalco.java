package dani.address;

import java.io.IOException;

import dani.address.model.Person;
import dani.address.view.PalcoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AplicacionPrincipalPalco extends Application {

	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	/**
	 * Constructor
	 */
	public AplicacionPrincipalPalco() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Faena Palco");

		initRootLayout();
		
		mostrarPantallaPalco();
		
	}
	
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mostrarPantallaPalco() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AplicacionPrincipalPalco.class.getResource("view/PantallaPalco.fxml"));
			AnchorPane pantallaPalco = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(pantallaPalco);

			// Give the controller access to the main app.
			PalcoController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
