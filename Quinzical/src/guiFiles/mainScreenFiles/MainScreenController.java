package guiFiles.mainScreenFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import mainClass.Main;
import utility.SoundPlayer;
import utility.SoundVariables;

public class MainScreenController {

	@FXML
	private ToggleButton toogleClickSounds;

	@FXML
	private Button practiceButton;

	@FXML
	private Button gameButton;

	@FXML
	private Button internationalButton;

	@FXML
	private Button quitButton;

	@FXML
	private Label helpLabel;

	@FXML
	private Label practiceHelpText;

	@FXML
	private Line practiceHelpLine;

	@FXML
	private Polyline practiceHelpArrow;

	@FXML
	private Line gameHelpLine;

	@FXML
	private Polyline gameHelpArrow;

	@FXML
	private Label gameHelpText;

	@FXML
	private Line internationalHelpLine;

	@FXML
	private Polyline internationalHelpArrow;

	@FXML
	private Label internationalHelpText;

	@FXML
	private Polyline quitHelpArrow;

	@FXML
	private Line quitHelpLine;

	@FXML
	private Label quitHelpText;

	@FXML
	private Polyline clickHelpArrow;

	@FXML
	private Line clickHelpLine;

	@FXML
	private Label clickHelpText;

	@FXML
	private ImageView resetImage;

	@FXML
	private ImageView questionImage;
	
	/**
	 * setts all the help lines labels and poly lines to be invisible.
	 * This will also set whether the interantional section button will be enabled or disabled
	 */
	public void initialize() {

		//question image load into imageview
		File questionFile = new File("./resources/question-mark.png");
        Image tempQuestionImage = new Image(questionFile.toURI().toString());
        questionImage.setImage(tempQuestionImage);
        
        //loading in reset image
        File resetFile = new File("./resources/reset-button.jpg");
        Image tempResetImage = new Image(resetFile.toURI().toString());
        resetImage.setImage(tempResetImage);
		
		//reads in the international save state and, dependeing on the state. sets the button to be enabled or disabled
		String saveState = "unlock state: 0";
		int count  = 0;
		BufferedReader br;
		try {

			File file = new File("./gameData/questions/internationalSave");
			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				if (count == 0) {
					saveState = line;
				}
				
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//checks what the first line says
		if(saveState.equals("unlock state: 0")) {
			internationalButton.setDisable(true);
		} else {
			internationalButton.setDisable(false);
		}
	}

	/**
	 * Switches scene to game module on button press
	 * @param event
	 */
	@FXML
	void startGame(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();

		File nzQuestionsSave = new File("./gameData/questions/currentNewZealandQuestions");

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
		double windowHeight = window.getHeight();    
		double windowWidth = window.getWidth();
		Scene nextScene;
		if(nzQuestionsSave.exists()) {
			nextScene = Main.get_gameScene();
		} else {
			try {
				Parent gameRoot = FXMLLoader.load(getClass().getResource(Main.getCategorySelectionScreenPath()));
				gameRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
				Main.setCategorySelectionScreen(new Scene(gameRoot));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			nextScene = Main.get_categorySelectionScene();
		}
		window.setHeight(windowHeight);	
		window.setWidth(windowWidth);
		window.setScene(nextScene);
		window.show();
	}

	/**
	 * takes the user into an international game
	 * @param event
	 */
	@FXML
	void startInternationalGame(ActionEvent event) {
		//generates sound when the button is clicked
		new SoundPlayer();

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
		double windowHeight = window.getHeight();    
		double windowWidth = window.getWidth();	
		Scene startScene = Main.get_internationalModule();	
		window.setHeight(windowHeight);	
		window.setWidth(windowWidth);
		window.setScene(startScene);
		window.show();
	}

	/**
	 * Resets all the saved games
	 * @param event
	 */
	@FXML
	void resetGames(MouseEvent event) {
		//handles the game module save
		
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to reset the game?");
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		if(result.get() == ButtonType.OK) {
			File saveGame = new File("./gameData/questions/currentNewZealandQuestions"); 
			saveGame.delete();
			
			//handles the international save
			File internationalSaveGame = new File("./gameData/questions/internationalSave");
			try {
				FileOutputStream fos = new FileOutputStream(internationalSaveGame);
				fos.write("unlock state: 0".getBytes());
				fos.flush();
				fos.close();
				
				//creates the gui for the main screen
				Parent root = FXMLLoader.load(getClass().getResource(Main.getMainScreenPath()));			
				root.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
				Main.set_mainScene(new Scene(root));
				
				//returns the user to the main menu
				Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
				double windowHeight = window.getHeight();    
				double windowWidth = window.getWidth();
				Scene mainScene = Main.get_mainScene();
				window.setHeight(windowHeight);	
				window.setWidth(windowWidth);
				window.setScene(mainScene);
				window.show();
				
			} catch (IOException e) {
				e.printStackTrace();
			}   
			
			Alert alert2 = new Alert(AlertType.INFORMATION, "Your game has been reset");
			alert2.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert2.showAndWait();

			
		}
		
		
		

	}


	/**
	 * Sets the global click variable which determines if button click sounds occur
	 * @param event
	 */
	@FXML
	void setGlobalClickVariable(ActionEvent event) {
		//sets the stored boolean to be the opposite of what it is
		if(SoundVariables.isClickSound()) {
			SoundVariables.setClickSound(false);
		} else {
			SoundVariables.setClickSound(true);
		}
	}

	/**
	 * 
	 * hides all the help labels when the user stops mousing over the question mark
	 * @param event
	 */
	@FXML
	void hideHelpLabels(MouseEvent event) {
		setHelpLabelVisibility(false);
	}

	/**
	 * 
	 * Shows all the help labels when the user mouses over the question mark
	 * @param event
	 */
	@FXML
	void showHelpLabels(MouseEvent event) {
		setHelpLabelVisibility(true);
	}

	/**
	 * Switches scene to practice game module on button press
	 * @param event
	 */
	@FXML
	void startPracticeGame(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
		double windowHeight = window.getHeight();    
		double windowWidth = window.getWidth();	
		Scene startScene = Main.get_practiceScene();	
		window.setHeight(windowHeight);	
		window.setWidth(windowWidth);
		window.setScene(startScene);
		window.show();
	}


	@FXML
	void quitGame(ActionEvent event) {
		//generates sound when the button is clicked
		new SoundPlayer();
		System.exit(0);
	}
	
	private void setHelpLabelVisibility(boolean visible) {
		practiceHelpText.setVisible(visible);
		practiceHelpLine.setVisible(visible);
		practiceHelpArrow.setVisible(visible);
		gameHelpLine.setVisible(visible);
		gameHelpArrow.setVisible(visible);
		gameHelpText.setVisible(visible);
		internationalHelpLine.setVisible(visible);
		internationalHelpArrow.setVisible(visible);
		internationalHelpText.setVisible(visible);
		quitHelpArrow.setVisible(visible);
		quitHelpLine.setVisible(visible);
		quitHelpText.setVisible(visible);
		clickHelpArrow.setVisible(visible);
		clickHelpLine.setVisible(visible);
		clickHelpText.setVisible(visible);
	}
}


