package utility;

/**
 * Question class to store question and answer
 *
 */

public class Question {
	
	private String _question;
	private String _answer;
	
	public Question(String question, String answer) {
		_question = question;
		_answer = answer;
	}
	
	/**
	 * Returns the question as a string
	 * @return Returns question as a string
	 */
	public String getQuestion() {
		return _question;
	}
	
	/**
	 * Returns the answer as a string
	 * @return Returns answer as a string
	 */
	public String getAnswer() {
		return _answer;
	}



}