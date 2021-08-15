package mainClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.*;
import javafx.stage.Stage;
import utility.QuestionReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application{

	private static final String _practiceGuiPath = "/guiFiles/practiceModuleFiles/PracticeModule.fxml";
	private static final String _gameGuiPath =  "/guiFiles/gameModuleFiles/GameModule.fxml";
	private static final String _mainScreenPath = "/guiFiles/mainScreenFiles/MainScreen.fxml";
	private static final String _categorySelectionScreenPath = "/guiFiles/categorySelectionScreenFiles/CategorySelectionScreen.fxml";
	private static final String _internationalModulePath = "/guiFiles/internationalModuleFiles/InternationalModule.fxml";
	private static Scene _gameScene;
	private static Scene _mainScene;
	private static Scene _practiceScene;
	private static Scene _categorySelectionScene;
	private static Scene _internationalModule;
	
	/**
	 * Starts the main Quizincal GUI. Loads main menu.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {			
			// Reads in all the questions allNewZealandQuestions
			QuestionReader.readAllCategories();

			File nzQuestionsSave = new File("./gameData/questions/currentNewZealandQuestions");
			if(nzQuestionsSave.exists()) {
				QuestionReader.readCurrentCategories();				
				//creates the gui scene for the game module 
				Parent gameRoot = FXMLLoader.load(getClass().getResource(_gameGuiPath));
				gameRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
				_gameScene = new Scene(gameRoot);
			} else {
				Parent gameRoot = FXMLLoader.load(getClass().getResource(_categorySelectionScreenPath));
				gameRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
				_categorySelectionScene = new Scene(gameRoot);
			}

			//creates the gui for the practice module
			Parent practiceRoot = FXMLLoader.load(getClass().getResource(_practiceGuiPath));
			practiceRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
			_practiceScene = new Scene(practiceRoot);	

			//Creates GUI for international module if its unlocked
			try {
				File file = new File("./gameData/questions/internationalSave");
				if(!file.exists()) {
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					String saveState = "unlock state: 0";
					fos.write(saveState.getBytes());
					fos.flush();
					fos.close();
				} else {
					QuestionReader.readInternationalCategories();
				}

				String cmd = "head -n 1 gameData/questions/internationalSave";
				ProcessBuilder builder = new ProcessBuilder("bash", "-c", cmd);
				Process process = builder.start();
				BufferedReader readWinnings = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String unlockState = readWinnings.readLine();

				if(unlockState.equals("unlock state: 1")){
					Parent internationalRoot = FXMLLoader.load(getClass().getResource(_internationalModulePath));
					internationalRoot.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
					_internationalModule = new Scene(internationalRoot);	
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			//creates the gui for the main screen
			Parent root = FXMLLoader.load(getClass().getResource(_mainScreenPath));			
			root.getStylesheets().add(getClass().getResource("/style/Style.css").toExternalForm());
			Scene startScene = new Scene(root);	

			//Sets the scene to the main screen
			_mainScene = startScene;
		
			primaryStage.setScene(startScene);
			primaryStage.setWidth(914);
			primaryStage.setHeight(719);
			primaryStage.setResizable(false);
			primaryStage.show();

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {	
		launch(args);
	}

	public static String getPracticeGuiPath() {
		return _practiceGuiPath;
	}

	public static String getGameGuiPath() {
		return _gameGuiPath;
	}

	public static String getMainScreenPath() {
		return _mainScreenPath;
	}

	public static Scene get_gameScene() {
		return _gameScene;
	}

	public static String getInternationalModulePath() {
		return _internationalModulePath;
	}

	public static Scene get_categorySelectionScene() {
		return _categorySelectionScene;
	}
	
	public static String getCategorySelectionScreenPath() {
		return _categorySelectionScreenPath;
	}
	
	public static void setCategorySelectionScreen(Scene categoryScene) {
		_categorySelectionScene = categoryScene;
	}

	public static void set_gameScene(Scene gameScene) {
		_gameScene = gameScene;
	}

	public static void set_mainScene(Scene mainScene) {
		_mainScene = mainScene;
	}

	public static void set_internationalScene(Scene internationalScene) {
		_internationalModule = internationalScene;
	}

	public static Scene get_mainScene() {
		return _mainScene;
	}

	public static Scene get_practiceScene() {
		return _practiceScene;
	}

	public static Scene get_internationalModule() {
		return _internationalModule;
	}

}