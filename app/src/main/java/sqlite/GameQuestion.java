package sqlite;

/**
 * Created by User on 2016/3/11.
 */
public class GameQuestion {
    String question;
    int question_id;
    int answer_id;

    public GameQuestion(int question_id, String question, int answer_id) {
        this.question_id = question_id;
        this.question = question;
        this.answer_id = answer_id;
    }

    public GameQuestion() {

    }

    public String getQuestion() {
        return question;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public int getAnswer_id() {
        return answer_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }

}
