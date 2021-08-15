package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionReader {
	
	private static final String _categoryQuestionsPath = "./gameData/questions/currentNewZealandQuestions";
	private static final String _allNewZealandQuestionsPath = "./gameData/questions/allNewZealandQuestions";
	private static final String _internationalSavePath = "./gameData/questions/internationalSave";
	
	private static List<Category> _currentCategories = new ArrayList<Category>();
	private static List<Category> _allNewZealandCategories = new ArrayList<Category>();
	private static List<Category> _internationalCategories = new ArrayList<Category>();
		
	/**
	 * readQuestions reads in the questions and categories from the the currentQuestions
	 * file and stores the categories and questions in a list to be accessed
	 */
	private static void readQuestions(List<Category> categoriesList, String filePath) {
		try {
			
			BufferedReader readFile = new BufferedReader(new FileReader(filePath));
			String line;
			
			while((line = readFile.readLine()) != null) {
				//If the line has no commas and is non-empty, it must be the category name
				if((line.indexOf(',') == -1)  && (!line.isBlank()) && !line.contains("unlock state:")) {
					//Creates new category in list
					Category category = new Category(line);
					categoriesList.add(category);
				} else if (!line.isBlank() && !line.contains("unlock state:")){ //Otherwise if the line is non-empty and has a comma, it is a question + answer line
					Category currentCategory = categoriesList.get(categoriesList.size() - 1);
					int splitPoint = line.indexOf('('); //Locates position in string to separate question and answer
					
					//Separates line into question and answer
					String question = line.substring(0, splitPoint -2);
					String answer = line.substring(splitPoint);
					
					//Adds the question to the current category
					currentCategory.addQuestion(new Question(question, answer));	
				}
			}
			readFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readCurrentCategories() {
		_currentCategories.clear();
		readQuestions(_currentCategories, _categoryQuestionsPath);
	}
	
	public static void readAllCategories() {
		_allNewZealandCategories.clear();
		readQuestions(_allNewZealandCategories, _allNewZealandQuestionsPath);
	}
	
	public static void readInternationalCategories() {
		_internationalCategories.clear();
		readQuestions(_internationalCategories, _internationalSavePath);
	}
	
	public static Category getCategory(List<Category> categoryList, int index) {
		return categoryList.get(index);
	}
	
	public static List<Category> getCurrentCategories() {
		return _currentCategories;
	}
	
	public static List<Category> getAllCategories() {
		return _allNewZealandCategories;
	}
	
	public static List<Category> getInternationalCategories() {
		return _internationalCategories;
	}
	
}
