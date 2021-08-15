package guiFiles.internationalRewardScreenFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mainClass.Main;
import utility.QuestionReader;
import utility.SoundPlayer;

public class InternationalRewardScreenController {

	//these are the score boundaries, which can be cahnged easily
	private final int _perfectScore = 20000;
	private final int _highestScore = 16000;

	private final String _perfectTrophyPath = "./resources/trophy/gold.png";
	private final String _silverTrophyPath = "./resources/trophy/silver.png";
	private final String _bronzeTrophyPath = "./resources/trophy/bronze.png";


	@FXML
	private Label scoreLabel;

	@FXML
	private Button playAgain;

	@FXML
	private Button returnButton;

	@FXML
	private ImageView rewardImage;
	
    @FXML
    private Label scoreDiffLabel;


	/**
	 * Sets up the score label and resets the game when initialized
	 */
	public void initialize() {

		//Loads in the user's winnings and sets the score label
		try {
			String cmd = "cat internationalScore";
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
			Process process = builder.start();
			BufferedReader readWinnings = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int score = Integer.parseInt(readWinnings.readLine());
			scoreLabel.setText("Your score: " + score);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//resets the game
		try {
			File file = new File("./gameData/questions/internationalSave");
			FileOutputStream fos = new FileOutputStream(file);
			String unlockString = "unlock state: 1";
			fos.write(unlockString.getBytes());
			fos.flush();
			fos.close();
			
			file = new File("internationalScore");
			fos = new FileOutputStream(file);
			String score = "0";
			fos.write(score.getBytes());
			fos.flush();
			fos.close();

			String cmd = "./src/bashScripts/generateQuestionSave.sh";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			Process process = pb.start();
			process.waitFor();
			QuestionReader.readInternationalCategories();

//			//creates the gui scene for the game module 
			Parent internationalRoot = FXMLLoader.load(getClass().getResource(Main.getInternationalModulePath()));
			internationalRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
			Main.set_internationalScene(new Scene(internationalRoot));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setTrophyImage(scoreLabel.getText());

	}

	/**
	 * based on the score, gets the image of the trophy that should be displayed 
	 * @param text
	 */
	private void setTrophyImage(String labelText) {

		//first needs to concatenate the input string
		String string = labelText.substring(labelText.indexOf(':') + 1).replaceAll("\\s+", "");

		int score = Integer.parseInt(string);


		//now has the score and must check what trphy the user deserves
		String  imgPath = _bronzeTrophyPath;
		if(score == _perfectScore) {
			scoreDiffLabel.setText("You are now a master in this category, try another categroy to boost your knowledge in another field");
			imgPath = _perfectTrophyPath;

		} else if(score > _highestScore) {
			//the user deserves the highest reward
			int pointDifference = _perfectScore - score;

			scoreDiffLabel.setText("You need " + pointDifference + " more to get the perfect score");
			imgPath = _silverTrophyPath;

		} else{
			//user gets the middle trophy
			int pointDifference = _highestScore - score;
			scoreDiffLabel.setText("You need " + pointDifference + " more to get the silver trophy");
			imgPath = _bronzeTrophyPath;
		}

		//Displays the medal the player has earned
		File image = new File(imgPath);
		Image img = new Image(image.toURI().toString());
		rewardImage.setImage(img);

	}


	/**
	 * Returns the user to the main menu
	 * @param event
	 */
	@FXML
	void returnToMenu(ActionEvent event) {
		//generates sound when the button is clicked
		new SoundPlayer();


		//returns the user to the main menu
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
		double windowHeight = window.getHeight();    
		double windowWidth = window.getWidth();
		Scene mainScene = Main.get_mainScene();
		window.setHeight(windowHeight);	
		window.setWidth(windowWidth);
		window.setScene(mainScene);
		window.show();
	}

}
