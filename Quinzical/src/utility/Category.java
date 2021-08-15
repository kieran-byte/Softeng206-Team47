package utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Category Class stores category name and questions for that category
 * Questions can be added, removed and fetched
 *
 */

public class Category {
	private String _categoryName;
	private Stack<Question> _categoryQuestions = new Stack<Question>();
	
	public Category(String categoryName) {
		_categoryName = categoryName;
	}
	
	/**
	 * Adds a question to the list of questions in the category
	 * @param question
	 */
	public void addQuestion(Question question) {
		_categoryQuestions.push(question);
	}
	
	/**
	 * Removes a question from the list of questions in the category
	 * and updates the categoryQuestions file accordingly
	 */
	public void removeQuestion(String saveFile) {	
		//Updates save file by removing line from categoryQuestions
		String questionString = _categoryQuestions.peek().getQuestion();
		try {
			String cmd = "sed -i '/" + questionString +"/d' " + saveFile;
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Removes question from list
		_categoryQuestions.pop();
	}
	
	/**
	 * Fetches a question from the list
	 * @return Question object from the list of questions
	 */
	public Question getQuestion() {
		Question question = _categoryQuestions.peek();
		return question;
	}
	
	/**
	 * Returns the name of the category
	 * @return Name of the category
	 */
	public String getCategoryName() {
		return _categoryName;
	}
	
	/**
	 * Returns the number of questions still left in the category
	 * @return int representing number of questions remaining
	 */
	public int remainingQuestion() {
		return _categoryQuestions.size();
	}
	
	/**
	 * Returns a list of the questions remaining in the category
	 * @return List of questions remaining
	 */
	public List<Question> getCategoryQuestionsList(){
		List<Question> questionsList = new ArrayList<Question>(_categoryQuestions);
		return questionsList;
		
	}

}
