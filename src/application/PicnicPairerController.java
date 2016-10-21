package application;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
//import javafx.scene.control.ProgressBar;
//import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * View-Controller for the person table.
 * 
 * @author Jake Nelson (github.com/jknlsn)
 */
public class PicnicPairerController {
	
	@FXML
	private Button myButton;
	
	@FXML
	private CheckBox myCheckBox;
	
	@FXML
	private Hyperlink myHyperlink;
	
	@FXML
	private Slider mySlider;
	
	@FXML
	private TextField maleCSVField;
	
	@FXML
	private TextField femaleCSVField;
	
	@FXML
	private TextField pairsCSVField;
	
	@FXML
	private TextField gMapsAPI;
	
	@FXML
	private TextField outputField;
	
//	@FXML
//	private ProgressBar progressBar;
//	
//	@FXML
//	private ProgressIndicator progressIndicator;
	
	@FXML
	private TextField pairsFound;
	
	public boolean testmode;
	public int experience;
	public String maleFile;
	public String femaleFile;
	// Need to write code to actually check for previous pairs, not implemented yet	
	public String pairsFile;
	public String outputFile;
	
	public String gMapsKey;
	
	/**
	 * The constructor (is called before the initialize()-method).
	 */
	public PicnicPairerController() {	
		testmode = true;
		experience = 3;
		return;
	}
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Handle Button event.
		myButton.setOnAction((event) -> {			
			// Check if both files are defined
			if (maleFile != null && femaleFile != null){
				UCFKPairer ucfk;
				int found = 0;
				// If testing then only need these defined
				if (testmode){
					ucfk = new UCFKPairer(experience, testmode, maleFile, femaleFile, pairsFile, outputFile, gMapsKey);
					found = ucfk.maximumPairs();
				}
				// Otherwise need full fields defined
				else if (outputFile != null && gMapsKey != null){
					System.out.println("All files defined.\n");
					ucfk = new UCFKPairer(experience, testmode, maleFile, femaleFile, pairsFile, outputFile, gMapsKey);				
					found = ucfk.maximumPairs();
				}
				System.out.println(found);
				pairsFound.setText("" + found);
			}
			else{
//				System.out.println("Please fill out both file fields.\n");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Please fill out the required fields.");

				alert.showAndWait();
			}			
		});
		
		// Handle CheckBox event.
		myCheckBox.setOnAction((event) -> {
			testmode = myCheckBox.isSelected();
			System.out.println("CheckBox Action (selected: " + testmode + ")\n");
		});
		
		// Handle Hyperlink event.
		myHyperlink.setOnAction((event) -> {
			System.out.println("Hyperlink Action\n");
		});
		
		// Handle Slider value change events.
		mySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			experience = newValue.intValue();
			System.out.println("Slider Value Changed (newValue: " + experience + ")\n");
		});
		
		// Handle maleCSVField text changes.
		maleCSVField.textProperty().addListener((observable, oldValue, newValue) -> {
			maleFile = newValue;
			System.out.println("Malefile Changed (newValue: " + maleFile + ")\n");
		});
		
		femaleCSVField.textProperty().addListener((observable, oldValue, newValue) -> {
			femaleFile = newValue;
			System.out.println("Femalefile Changed (newValue: " + femaleFile + ")\n");
		});
		
		pairsCSVField.textProperty().addListener((observable, oldValue, newValue) -> {
			pairsFile = newValue;
			System.out.println("Malefile Changed (newValue: " + pairsFile + ")\n");
		});
		
		outputField.textProperty().addListener((observable, oldValue, newValue) -> {
			outputFile = newValue;
			System.out.println("Output File Changed (newValue: " + outputFile + ")\n");
		});
		
		gMapsAPI.textProperty().addListener((observable, oldValue, newValue) -> {
			gMapsKey = newValue;
			System.out.println("API Key Changed (newValue: " + gMapsKey + ")\n");
		});
		
	}
	
}