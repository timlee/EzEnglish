package sqlite;

/**
 * Created by User on 2016/2/3.
 */
public class Words {
    int w_id;
    String word;
    int r_id;


    public Words(int w_id, String word, int r_id) {
        this.w_id = w_id;
        this.word = word;
        this.r_id = r_id;

    }

    public Words() {

    }

    public int getW_id() {
        return w_id;
    }

    public String getWord() {
        return word;
    }

    public int getR_id() {
        return r_id;
    }

    public void setW_id(int w_id) {
        this.w_id = w_id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setR_id(int r_id) { this.r_id = r_id; }


}
