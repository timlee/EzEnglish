package sqlite;

import java.util.Date;

import jxl.write.DateTime;

/**
 * Created by User on 2016/3/11.
 */
public class GameRecord {
    int record_id;
    int record;
    String time;

    public GameRecord(int record_id, int record, String time) {
        this.record_id = record_id;
        this.record = record;
        this.time = time;
    }

    public GameRecord() {

    }

    public int getRecord_id() {
        return record_id;
    }

    public int getRecord() {
        return record;
    }

    public String getTime() {
        return time;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
