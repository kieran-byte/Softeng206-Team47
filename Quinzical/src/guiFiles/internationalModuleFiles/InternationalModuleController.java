package guiFiles.internationalModuleFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.util.Duration;
import mainClass.Main;
import utility.Category;
import utility.TTSThread;
import utility.Question;
import utility.QuestionReader;
import utility.SoundPlayer;
import utility.SoundVariables;

public class InternationalModuleController {

	//Speed to read tts
	private double _readSpeed = 1.0;

	//Keeps track of current game state
	private boolean _questionInProgress = false;
	private Question _currentQuestion;
	private Button _currentCategoryButton;
	private int _completedCategories = 0;
	private int _score = 0;

	private final int MAX_TIME = 10;
	private Timeline timeline;
	private int remainingTime = MAX_TIME;

	//Lists to hold category buttons and labels 
	private List<Button> categoryButtons = new ArrayList<Button>();
	private List<Label> categoryLabels = new ArrayList<Label>();

	@FXML
	private Label helpLabel;

	@FXML
	private Label returnHelpText1;

	@FXML
	private Line returnHelpLine1;

	@FXML
	private Slider readSpeedBar;

	@FXML
	private Button categoryButton1;

	@FXML
	private Button categoryButton2;

	@FXML
	private Button categoryButton3;

	@FXML
	private Button categoryButton4;

	@FXML
	private Button categoryButton5;

	@FXML
	private Label categoryLabel1;

	@FXML
	private Label categoryLabel5;

	@FXML
	private Label categoryLabel4;

	@FXML
	private Label categoryLabel3;

	@FXML
	private Label categoryLabel2;

	@FXML
	private Label currentWinningsText;

	@FXML
	private Button mainMenuButton;

	@FXML
	private TextField userAnswer;

	@FXML
	private Button repeatButton;

	@FXML
	private Button submitButton;

	@FXML
	private Line returnHelpLine;

	@FXML
	private Label returnHelpText;

	@FXML
	private Polyline winningsHelpArrow;

	@FXML
	private Line winningsHelpLine;

	@FXML
	private Label winningsHelpText;

	@FXML
	private Polyline repeatArrowHelp;

	@FXML
	private Line repeatLineHelp;

	@FXML
	private Label repeatHelpText;

	@FXML
	private Polyline submitArrowHelp;

	@FXML
	private Line submitHelpLine;

	@FXML
	private Label submitHelpText;

	@FXML
	private Polyline herlpSpeedArrow;

	@FXML
	private Polyline returnHelpArrow;

	@FXML
	private Label timerLabel;

	@FXML
	private ImageView questionImage;

	/**
	 * Initializes everything required for main screen. This includes setting up category buttons and labels, the score
	 * label and setting up listener for speed bar.
	 * 
	 */
	public void initialize() {

		//question image load into imageview
		File questionFile = new File("./resources/question-mark.png");
		Image tempQuestionImage = new Image(questionFile.toURI().toString());
		questionImage.setImage(tempQuestionImage);

		readSpeedBar.setValue(SoundVariables.get_readSpeed());

		//Sets up list of buttons
		categoryButtons.add(categoryButton1);
		categoryButtons.add(categoryButton2);
		categoryButtons.add(categoryButton3);
		categoryButtons.add(categoryButton4);
		categoryButtons.add(categoryButton5);

		//Sets up list of categoryLabels
		categoryLabels.add(categoryLabel1);
		categoryLabels.add(categoryLabel2);
		categoryLabels.add(categoryLabel3);
		categoryLabels.add(categoryLabel4);
		categoryLabels.add(categoryLabel5);

		//Sets up all labels and buttons with appropriate text
		for(int i = 0; i< categoryLabels.size(); i++) {
			setCategoryLabel(categoryLabels.get(i));
			updateCategoryButton(categoryButtons.get(i));

			categoryLabels.get(i).setWrapText(true);
			categoryButtons.get(i).setWrapText(true);
		}

		//Loads in the user's winnings and sets the score label to reflect score
		try {
			String cmd = "cat gameData/internationalScore";
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
			Process process = builder.start();
			BufferedReader readWinnings = new BufferedReader(new InputStreamReader(process.getInputStream()));
			_score = Integer.parseInt(readWinnings.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentWinningsText.setText("Current Winnings: $" + _score);

		//Adds listener to the read speed bar to change read speed when moved
		readSpeedBar.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue,  Number newValue) {				
				//Calculates read speed for festival
				_readSpeed = 1/readSpeedBar.getValue();
				SoundVariables.set_readSpeed(_readSpeed);
			}

		});
	}


	/**
	 * Checks the answer that was inputted by the user against the answer stored in the text files
	 * The updates the GUI accordingly and updates save files aswell
	 * 
	 * @param event
	 */
	@FXML
	void checkAnswer(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();

		//Stops and resets timer 
		timeline.stop();
		remainingTime = MAX_TIME;
		timerLabel.setText("Time Left: " + MAX_TIME);

		//Gathers correct answer and user's answer
		String answer = _currentQuestion.getAnswer();
		String userInputAnswer= userAnswer.getText();

		//Removes brackets from answer
		String formattedAnswer = answer.replaceAll("[()]", "");

		//Checks the user's answer
		if(formattedAnswer.toLowerCase().equals(userInputAnswer.toLowerCase())) {
			showUserAlert("Your answer was correct");

			//Calculates how much to add to the user's score
			int remainingQuestions = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(_currentCategoryButton)).remainingQuestion();
			int questionsAnswered = 5 - remainingQuestions;
			int currentQuestionValue = 200 * (questionsAnswered + 1);
			_score += currentQuestionValue;

			//Updates the user's score by adding value and writing to file
			updateScore();

		} else {
			showUserAlert("Your answer was incorrect. The correct answer was " + formattedAnswer);
		}

		//Updates the category Label accordingly
		updateCategoryButton(_currentCategoryButton);		
		setCategoryLabel(categoryLabels.get(getCategoryIndex(_currentCategoryButton)));					
		userAnswer.clear();
		disableAnswerInputs(true);

		for(Button button: categoryButtons) {
			if(QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(button)).remainingQuestion() != 0) {
				button.setDisable(false);
			}
		}

		//Updates the category Label accordingly
		setCategoryLabel(categoryLabels.get(getCategoryIndex(_currentCategoryButton)));					
		userAnswer.clear();

		/**
		 * Code below used to switch to reward screen when all categories have been completed.
		 */
		if(_completedCategories == 5) {

			//creates the gui for the practice scene
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
			double windowHeight = window.getHeight();    
			double windowWidth = window.getWidth();

			String _rewardScreenPath = "/guiFiles/internationalRewardScreenFiles/InternationalRewardModule.fxml";
			Parent rewardRoot;

			//Loads in and changes scene to the reward screen
			try {
				rewardRoot = FXMLLoader.load(getClass().getResource(_rewardScreenPath));
				rewardRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
				System.out.println(rewardRoot);
				Scene rewardScene = new Scene(rewardRoot);
				window.setHeight(windowHeight);	
				window.setWidth(windowWidth);
				window.setScene(rewardScene);
				window.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//Resets game state so next question can be asked
		_currentCategoryButton = null;
		_questionInProgress = false;
	}


	/**
	 * checks if the button clicked was valid, in this context valid means that the user has not selected a different
	 * category without answering that question first
	 * @param event
	 */
	@FXML
	void checkValid(ActionEvent event) {
		//generates sound when the button is clicked
		new SoundPlayer();

		//If a question hasn't been selected previously ask the question
		if(!_questionInProgress) {
			_questionInProgress = true;			

			//Enables submit, repeat and submit buttons
			disableAnswerInputs(false);

			//Gets Id for button which was pressed
			Button buttonPressed = (Button)event.getSource();
			_currentCategoryButton = buttonPressed;

			//Disables all other buttons
			for(Button button: categoryButtons){
				if(!button.equals(_currentCategoryButton)) {
					button.setDisable(true);
				}
			}

			String buttonId = buttonPressed.getId();

			//Gets category number for button pressed
			int categoryIndex = Integer.parseInt(buttonId.substring(buttonId.length() -1 )) - 1;

			//Gets the category
			Category currentCategory = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),categoryIndex);
			Question question = currentCategory.getQuestion();
			_currentQuestion = question;

			//Reads out question to user
			TTSThread helper = new TTSThread(_currentQuestion.getQuestion(), _readSpeed);
			helper.start();

			//Removes a question from the category after a question has been asked
			currentCategory.removeQuestion("gameData/questions/internationalSave");

			startTimer();
		}
	}

	/**
	 * 
	 * Repeats the question to the user when clicked
	 * @param event
	 */
	@FXML
	void repeatQuestion(ActionEvent event) {

		//generates sound when the button is clicked
		new SoundPlayer();
		TTSThread helper = new TTSThread(_currentQuestion.getQuestion(), _readSpeed);
		helper.start();
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

	@FXML
	void hideHelpLabels(MouseEvent event) {
		setHelpLabelVisiblity(false);
	}

	@FXML
	void showHelpLabels(MouseEvent event) {
		setHelpLabelVisiblity(true);
	}

	/** Helper methods **/

	/** 
	 * Updates the value displayed on the button according to number of questions remaining
	 * in the category
	 * @param button
	 */
	private void updateButtonValueText(Button button) {
		int remainingQuestions = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(button)).remainingQuestion();
		int questionsAnswered = 6 - remainingQuestions;
		int currentQuestionValue = 200 * (questionsAnswered + 1);
		button.setText("Click for next question\n$" + currentQuestionValue);
	}

	/**
	 * Sets the text on the category label according to how many questions remaining in the category
	 * @param label
	 */
	private void setCategoryLabel(Label label) {
		String categoryName = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(label)).getCategoryName();
		int questionsRemaining = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(label)).remainingQuestion();
		label.setText(categoryName + "\n" + "Questions Remaining: " + questionsRemaining);
	}

	/**
	 * Sets up the buttons when the main menu is initialized. Disables the category buttons if there are no
	 * questions remaining otherwise updates the buttons value.
	 * @param button
	 */
	private void updateCategoryButton(Button button) {
		int remainingQuestions = QuestionReader.getCategory(QuestionReader.getInternationalCategories(),getCategoryIndex(button)).remainingQuestion();
		if(remainingQuestions == 0) {
			button.setText("Category Completed");
			button.setDisable(true);
			_completedCategories++;
		} else {
			updateButtonValueText(button);
		}
	}

	/**
	 * Returns the category index from the element provided by looking at its Id.
	 * @param element
	 * @return
	 */
	private int getCategoryIndex(Control element) {
		String elementId = element.getId();
		int categoryIndex = Integer.parseInt(elementId.substring(elementId.length() -1 )) - 1;
		return categoryIndex;
	}

	/**
	 * Makes updates to displayed winnings and saved winnings
	 */
	private void updateScore() {
		//Updates winnings label
		currentWinningsText.setText("Current Winnings: $" + _score);

		//Updates the winnings save file
		try {
			File file = new File("./gameData/internationalScore");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(Integer.toString(_score).getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setHelpLabelVisiblity(boolean visible) {
		herlpSpeedArrow.setVisible(visible);
		returnHelpArrow.setVisible(visible);
		returnHelpText1.setVisible(visible);
		returnHelpLine1.setVisible(visible);
		returnHelpLine.setVisible(visible);
		returnHelpText.setVisible(visible);
		winningsHelpArrow.setVisible(visible);
		winningsHelpLine.setVisible(visible);
		winningsHelpText.setVisible(visible);
		repeatArrowHelp.setVisible(visible);
		repeatLineHelp.setVisible(visible);
		repeatHelpText.setVisible(visible);
		submitArrowHelp.setVisible(visible);
		submitHelpLine.setVisible(visible);
		submitHelpText.setVisible(visible);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void startTimer() {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add( new KeyFrame(Duration.seconds(1), new EventHandler() {
			public void handle(Event event) {
				remainingTime--;
				timerLabel.setText("Time Left: " + Integer.toString(remainingTime));

				//Prompts user and resets timer when time has run out
				if(remainingTime == 0) {
					//Prompts the user they have run out of time
					String answer = _currentQuestion.getAnswer();
					//Removes brackets from answer
					String formattedAnswer = answer.replaceAll("[()]", "");
					String message = "You have run out of time. The correct answer was " + formattedAnswer;
					TTSThread helper = new TTSThread(message, _readSpeed);
					helper.start();


					//Stops the timer
					timeline.stop();
					remainingTime = MAX_TIME;
					timerLabel.setText("Time Left: 0");

					Alert alert = new Alert(AlertType.INFORMATION, "You have run out of time. The correct answer was " + formattedAnswer);
					alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
					alert.show();

					//Updates the category Label accordingly
					updateCategoryButton(_currentCategoryButton);		
					setCategoryLabel(categoryLabels.get(getCategoryIndex(_currentCategoryButton)));					
					userAnswer.clear();
					disableAnswerInputs(true);

					for(Button button: categoryButtons) {
						if(QuestionReader.getCategory(QuestionReader.getCurrentCategories(),getCategoryIndex(button)).remainingQuestion() != 0) {
							button.setDisable(false);
						}
					}


					//Resets game state so next question can be asked
					_currentCategoryButton = null;
					_questionInProgress = false;
				}
			}
		}));
		timeline.playFromStart();
	}

	private void showUserAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION, message);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	private void disableAnswerInputs(boolean disabled) {
		submitButton.setDisable(disabled);
		userAnswer.setDisable(disabled);
		repeatButton.setDisable(disabled);
	}
}
