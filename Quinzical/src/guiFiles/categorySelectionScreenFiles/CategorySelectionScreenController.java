package guiFiles.categorySelectionScreenFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utility.QuestionReader;
import utility.SoundPlayer;
import utility.Category;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mainClass.Main;

public class CategorySelectionScreenController {

	private List<CheckBox> inActiveCheckboxes = new ArrayList<CheckBox>();
	private List<String> chosenCategoryNames = new ArrayList<String>();
	private List<Category> allNZCategories = QuestionReader.getAllCategories();
	private int categoriesSelected = 0;

	@FXML
	private Button returnButton;

	@FXML
	private Label remaningCounter;

	@FXML
	private Label helpLabel;

	@FXML
	private Button confirmButton;

	@FXML
	private Polyline checkHelpArrow;

	@FXML
	private Line checkLineHelp;

	@FXML
	private Text checkHelpText;

	@FXML
	private Line lineHelpLine;

	@FXML
	private Polyline remaningHelpArrow;

	@FXML
	private Text remaningHelpText;

	@FXML
	private Polyline confirmButtonHelp;

	@FXML
	private Polyline returnHelpArrow;

	@FXML
	private Line confirmLineHelp;

	@FXML
	private Line returnHelpLine;

	@FXML
	private Text confirmHelpText;

	@FXML
	private Text returnHelpText;

	@FXML
	private TilePane checkBoxContainer;

    @FXML
    private ImageView questionImage;


	public void initialize() {


		//question image load into imageview
		File questionFile = new File("./resources/question-mark.png");
		Image tempQuestionImage = new Image(questionFile.toURI().toString());
		questionImage.setImage(tempQuestionImage);


		confirmButton.setDisable(true);

		//Generates checkboxes so the user can select which categories they want
		for(Category category: allNZCategories) {
			CheckBox checkbox = new CheckBox(category.getCategoryName());
			checkbox.setStyle("-fx-background-color: \r\n"
					+ "        #000000,\r\n"
					+ "        linear-gradient(#7ebcea, #2f4b8f),\r\n"
					+ "        linear-gradient(#426ab7, #263e75),\r\n"
					+ "        linear-gradient(#395cab, #223768);\r\n"
					+ "    -fx-background-insets: 0,1,2,3;\r\n"
					+ "    -fx-background-radius: 3,2,2,2;\r\n"
					+ "    -fx-padding: 6 15 6 15;\r\n"
					+ "    -fx-text-fill: white;\r\n"
					+ "    -fx-font-size: 15px;");

			inActiveCheckboxes.add(checkbox);

			checkbox.setPrefWidth(180);
			checkbox.setPrefHeight(40);
			checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
					//Updates the list of categories select and the list of inactive checkboxes depending
					//on whether a box has been checked or unchecked
					if(oldValue == true) {
						categoriesSelected--;
						inActiveCheckboxes.add(checkbox);
						chosenCategoryNames.remove(checkbox.getText());
					} else {
						categoriesSelected++;
						inActiveCheckboxes.remove(checkbox);
						chosenCategoryNames.add(checkbox.getText());
					}

					//Updates label indicating how many categories can still be selected
					remaningCounter.setText("Picks Remaining: " + (5 - categoriesSelected));

					//Disables all the other checkboxes if 5 checkboxes have already been selected
					if(categoriesSelected == 5) {
						for(CheckBox checkbox: inActiveCheckboxes) {
							checkbox.setDisable(true);
						}
						confirmButton.setDisable(false);
					} else {
						for(CheckBox checkbox: inActiveCheckboxes) {
							checkbox.setDisable(false);
						}
						confirmButton.setDisable(true);
					}
				}

			});
			//Add checkbox to container
			checkBoxContainer.getChildren().add(checkbox);
		}
	}


	@FXML
	void returnToMainMenu(ActionEvent event) {
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

	/**
	 * When the mouse leaves the label, hides all the help labels
	 * @param event
	 */
	@FXML
	void hideHelpLabels(MouseEvent event) {
		setHelpLabelVisibility(false);
	}


	/**
	 * shows the label when the mouse is over the help label
	 * @param event
	 */
	@FXML
	void showHelpLabels(MouseEvent event) {
		setHelpLabelVisibility(true);
	}


	@FXML
	void confirmChoices(ActionEvent event) {

		new SoundPlayer();

		try {
			//Selects random questions for each category selected and writes into a save file
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", "src/bashScripts/generateSingleNZCategoryQuestions.sh");
			Map<String, String> environment = pb.environment();
			for(String categoryName: chosenCategoryNames) {
				environment.put("CATEGORYNAME", categoryName);
				Process process = pb.start();
				process.waitFor();
			}

			//Reads in questions for the 
			QuestionReader.readCurrentCategories();		

			//creates the gui scene for the game module 
			Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();    
			double windowHeight = window.getHeight();    
			double windowWidth = window.getWidth();

			Parent gameRoot = FXMLLoader.load(getClass().getResource(Main.getGameGuiPath()));
			gameRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
			Scene gameScene = new Scene(gameRoot);
			Main.set_gameScene(gameScene);

			window.setHeight(windowHeight);	
			window.setWidth(windowWidth);
			window.setScene(gameScene);
			window.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void setHelpLabelVisibility(boolean visible) {
		checkHelpArrow.setVisible(visible);
		checkLineHelp.setVisible(visible);
		checkHelpText.setVisible(visible);
		lineHelpLine.setVisible(visible);
		remaningHelpArrow.setVisible(visible);
		remaningHelpText.setVisible(visible);
		confirmButtonHelp.setVisible(visible);
		returnHelpArrow.setVisible(visible);
		confirmLineHelp.setVisible(visible);
		returnHelpLine.setVisible(visible);
		confirmHelpText.setVisible(visible);
		returnHelpText.setVisible(visible);
	}
}

