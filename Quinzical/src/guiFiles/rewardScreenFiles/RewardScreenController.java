package guiFiles.rewardScreenFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mainClass.Main;
import utility.SoundPlayer;

public class RewardScreenController {

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
			String cmd = "cat gameData/score";
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
			String cmd = "./src/bashScripts/resetGame.sh";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.start();
			
			File gameFile = new File("./gameData/questions/currentNewZealandQuestions");
			gameFile.delete();
			
			//Update score file
			File scoreFile = new File("./gameData/score");
			FileOutputStream fos = new FileOutputStream(scoreFile);
			fos.write(Integer.toString(0).getBytes());
			fos.flush();
			fos.close();

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
