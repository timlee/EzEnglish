package sqlite;

/**
 * Created by User on 2016/2/22.
 */
public class Meaning {
    int m_id;
    int w_id;
    String part_of_speech;
    String EngChiTra;


    public Meaning(){


    }

    public Meaning(int m_id, int w_id,String part_of_speech, String engChiTra) {
        this.m_id = m_id;
        this.w_id = w_id;
        this.part_of_speech = part_of_speech;
        this.EngChiTra = engChiTra;
    }

    public int getM_id() { return m_id; }

    public int getW_id() { return w_id; }

   public String getPart_of_speech() {
        return part_of_speech;
    }

    public String getEngChiTra() {
        return EngChiTra;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public void setW_id(int w_id) {
        this.w_id = w_id;
    }

   public void setPart_of_speech(String part_of_speech) {
        this.part_of_speech = part_of_speech;
    }

    public void setEngChiTra(String engChiTra) {
        EngChiTra = engChiTra;
    }
}
