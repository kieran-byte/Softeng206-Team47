package guiFiles.practiceModuleFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import mainClass.Main;
import utility.Category;
import utility.TTSThread;
import utility.Question;
import utility.QuestionReader;
import utility.SoundPlayer;
import utility.SoundVariables;

public class PracticeGameController {

	//Stores read speed for festival
	private Double _readSpeed = 1.0;

	//Stores game state
	private int _numTrys = 0;
	private Question _currentQuestion = null;
	private int _consecutiveCorrect = 0;

	//List of categories the user can select
	private List<Category> _categoriesList = QuestionReader.getAllCategories();

	@FXML
	private ImageView questionImage;

	@FXML
	private Slider readSpeedBar;

	@FXML
	private ComboBox<String> categoryDropDown;

	@FXML
	private Button confirmationButton;

	@FXML
	private Label questionLabel;

	@FXML
	private Label consecutiveNumberLabel;

	@FXML
	private Label tryNumberLabel;

	@FXML
	private TextField answerArea;

	@FXML
	private Button repeatQuestionButton;

	@FXML
	private Button submitButton;

	@FXML
	private Button returnButton;

	@FXML
	private Label menuHelpLabel;

	@FXML
	private Polyline menuHelpArrow;

	@FXML
	private Line menuHelpLine;

	@FXML
	private Polyline speedHelpLabel;

	@FXML
	private Line speedHelpArrow;

	@FXML
	private Label speedHelpText;

	@FXML
	private Polyline confirmHelpArrow;

	@FXML
	private Line confirmHelpLine1;

	@FXML
	private Line confirmHelpLine2;

	@FXML
	private Label confirmHelpLabel;

	@FXML
	private Polyline dropHelpArrow;

	@FXML
	private Line dropHelpLine2;

	@FXML
	private Line dropHelpLine;

	@FXML
	private Label dropHelpText;
	
	@FXML
	private Label hintLabel;

	/**
	 * 
	 * Adds the changeListener to the start of the scene creation, this allows the user to set the words per minute when the question is read 
	 * 
	 */
	public void initialize() {


		//question image load into imageview
		File questionFile = new File("./resources/question-mark.png");
		Image tempQuestionImage = new Image(questionFile.toURI().toString());
		questionImage.setImage(tempQuestionImage);

		readSpeedBar.setValue(SoundVariables.get_readSpeed());		

		//Sets up category names in dropdown box
		for(Category category: _categoriesList) {
			categoryDropDown.getItems().add(category.getCategoryName());
		}

		//Sets up listener for the read speed bad
		readSpeedBar.valueProperty().addListener(new ChangeListener<Number>() {

			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue,  Number newValue) {
				_readSpeed = 1/readSpeedBar.getValue();
				SoundVariables.set_readSpeed(_readSpeed);
			}

		});
	}

	/**
	 * Checks the answer that was inputed by the user against the answer stored in the text files also changes number of tries and consecutive correct as needed
	 * 
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	void checkAnswer(ActionEvent event) throws IOException {
		//generates sound when the button is clicked
		new SoundPlayer();

		String userAnswer = answerArea.getText();
		String formattedAnswer = _currentQuestion.getAnswer().replaceAll("[()]", "");

		if(_numTrys < 2) {//numTrys is 2 because we start at 0
			checkAnswer(userAnswer, formattedAnswer);

		} else if (_numTrys == 2) {

			Boolean userCorrect = checkAnswer(userAnswer, formattedAnswer);

			if(!userCorrect) {
				//needs to find the first letter of the current question
				String answer = _currentQuestion.getAnswer();
				answer = answer.substring(answer.indexOf(')') + 1 );
				answer = answer.substring(1, 2);

				hintLabel.setVisible(true);
				hintLabel.setText("The first letter of the answer is: " + answer);
			}			

		} else if (_numTrys > 2){
			//this is the players last chance, if the answer is incorrect here, it is incorrect fully

			//the user gets it right
			if(userAnswer.toLowerCase().equals(formattedAnswer.toLowerCase())) {
				//				String message = "The answer is correct";
				//				HelperThread helper = new HelperThread(message, _readSpeed);
				//				helper.start();
				showUserAlert("Your answer is correct");

				//increments the consecutive correct number
				_consecutiveCorrect++;

			} else { //else the user got it incorrects
				//				String message = "You have run out of tries. the correct answer was " + formattedAnswer;
				//				HelperThread helper = new HelperThread(message, _readSpeed);
				//				helper.start();
				showUserAlert("You have run out of tries. the correct answer was " + formattedAnswer);

				//resets the consectuive correct field
				_consecutiveCorrect = 0;
			}

			//then resets the elements for the next question
			_currentQuestion = null;
			_numTrys = 0;
			hintLabel.setVisible(false);
			tryNumberLabel.setText("Current try number: " + _numTrys);
			answerArea.clear();

			//now that the question is answered either correctly or incorrectly another question must be selected
			randomQuestion();
		}
		consecutiveNumberLabel.setText("Consecutive correct: " + _consecutiveCorrect);
	}

	/**
	 * 
	 * The user is given a random question when this button is pressed
	 * @param event
	 */
	@FXML
	void confirmChoice(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();
		if(categoryDropDown.getValue() != null) {
			randomQuestion();
			answerArea.setDisable(false);
			submitButton.setDisable(false);
			repeatQuestionButton.setDisable(false);
			questionLabel.setText("Question: " + _currentQuestion.getQuestion());
		}
	}

	/**
	 * 
	 * When clicked needs to repeat the current question to the user
	 * @param event
	 */
	@FXML
	void repeatQuestion(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();

		//repeats the question
		TTSThread helper = new TTSThread(_currentQuestion.getQuestion(), _readSpeed);
		helper.start();
	}

	/**
	 * Returns the user to the main menu
	 * @param event
	 */
	@FXML
	void returnToMainMenu(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();

		//returns the user to the main screen gui
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
		double windowHeight = window.getHeight();    
		double windowWidth = window.getWidth();
		Scene mainScene = Main.get_mainScene();
		window.setHeight(windowHeight);	
		window.setWidth(windowWidth);
		window.setScene(mainScene);
		window.show();
	}

	/**
	 * 
	 * Created a random question method so that both the initial question call and all others can use this method
	 * @param selectedCategory
	 * @param pickChoice 
	 * @param firstPick
	 * @return
	 */
	private void randomQuestion() {

		//creates the variable and presets it to be null
		Question selectedQuestion = null;

		String selectedCategoryName = categoryDropDown.getValue();
		Category selectedCategory = null;

		//Finds which category the user has selected based on the category name
		for(Category category: _categoriesList) {
			if(category.getCategoryName().equals(selectedCategoryName)) {
				selectedCategory = category;
				break;
			}
		}

		//Collects a list of questions from the category
		List<Question> questionList = selectedCategory.getCategoryQuestionsList();

		//Generates a random number to select the question index
		Random rand = new Random();
		int randomIndex = rand.nextInt(questionList.size());

		//Selects random question using random index generated
		selectedQuestion = questionList.get(randomIndex);
		_currentQuestion = selectedQuestion;

		TTSThread helper = new TTSThread(selectedQuestion.getQuestion(), _readSpeed);
		helper.start();

		//Resets the number of trys
		_numTrys = 0;
	}

	/**
	 * Helper method which checks whether the user's answer was correct or not
	 * @param userAnswer - String containing user's answer
	 * @param actualAnswer - Sting containing the actual answer
	 * @return Boolean value which corresponds to which is true if the user is correct and false otherwise
	 */
	private Boolean checkAnswer(String userAnswer, String actualAnswer) {
		Boolean userCorrect = false;

		if(userAnswer.toLowerCase().equals(actualAnswer.toLowerCase())) {
			//Tells the user their answer was correct
			showUserAlert("Your answer is correct");



			_currentQuestion = null;
			_numTrys = 0;
			_consecutiveCorrect++;

			//finds a new random question
			//now that the question is answered either correctly or incorrectly another question must be selected
			answerArea.clear();
			randomQuestion();
			userCorrect = true;

		} else {
			//Tells the user their answer was incorrect
			showUserAlert("Your answer is incorrect. Please try again");


			//Updates the number of trys by the user
			_numTrys++;
		}
		tryNumberLabel.setText("Current try number: " + _numTrys);

		return userCorrect;
	}

	/**
	 * Hides all help labels
	 * @param event
	 */
	@FXML
	void hideHelpLabels(MouseEvent event) {
		setLabelVisibility(false);
	}

	/**
	 * Shows all help labelss
	 * @param event
	 */
	@FXML
	void showHelpLabels(MouseEvent event) {
		setLabelVisibility(true);
	}

	private void showUserAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION, message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	private void setLabelVisibility(boolean visible) {
		menuHelpLabel.setVisible(visible);
		menuHelpArrow.setVisible(visible);
		menuHelpLine.setVisible(visible);
		speedHelpLabel.setVisible(visible);
		speedHelpArrow.setVisible(visible);
		speedHelpText.setVisible(visible);
		confirmHelpArrow.setVisible(visible);
		confirmHelpLine1.setVisible(visible);
		confirmHelpLine2.setVisible(visible);
		confirmHelpLabel.setVisible(visible);
		dropHelpArrow.setVisible(visible);
		dropHelpLine2.setVisible(visible);
		dropHelpLine.setVisible(visible);
		dropHelpText.setVisible(visible);
	}
}
