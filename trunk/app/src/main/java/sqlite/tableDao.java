package sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.knowmemo.usermanagement.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/1/30.
 */
public class tableDao {

    public static String user_id = "user_test";
    public static final String words = "words";
    public static final String meaning = "meaning";
    public static final String exp = "exp";
    public static final String categories = "categories";
    public static final String root = "root";
    public static final String favorites = "favorites";
    public static final String game_question = "game_question";
    public static final String game_record = "game_record";
    public static int box_level_1_Limit = 10;
    public static int box_level_2_Limit = 20;
    public static int box_level_3_Limit = 40;
    public static int box_level_4_Limit = 80;
    public static int box_level_5_Limit = 160;
    private SQLiteDatabase db;

    public static final String createWordsTable = "CREATE TABLE IF NOT EXISTS " + words
            + "(w_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "word VARCHAR(30) NOT NULL,"
            + "r_id INTEGER(3))";

    public static final String createMeaningTable = "CREATE TABLE IF NOT EXISTS " + meaning
            + "(m_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "w_id INTEGER NOT NULL,"
            + "part_of_speech VARCHAR(4),"
            + "EngChiTra VARCHAR(100))";


    public static final String createExpTable = "CREATE TABLE IF NOT EXISTS " + exp
            + " (user_id VARCHAR NOT NULL,"
            + "word_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "level INTEGER(3) NOT NULL,"
            + "position INTEGER(5) NOT NULL,"
            + "learned INTEGER(5),"
            + "Last_Learnt_Time VARCHAR(20))";

    public static final String createRootTable = "CREATE TABLE IF NOT EXISTS " + root
            + " (r_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "root VARCHAR(30) NOT NULL)";

    public static final String createCategoriesTable = "CREATE TABLE IF NOT EXISTS " + categories
            + " (sub_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "id INTEGER(10) NOT NULL,"
            + "word VARCHAR(20) NOT NULL,"
            + "catagory INTEGER(11) NOT NULL)";

    public static final String createFavoritesTable = "CREATE TABLE IF NOT EXISTS " + favorites
            + " (favor_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "favor_word VARCHAR(20) NOT NULL,"
            + "favor_meaning VARCHAR(20) NOT NULL)";

    public static final String createGameQuestion = "CREATE TABLE IF NOT EXISTS " + game_question
            + " (question_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "question VARCHAR(100) NOT NULL,"
            + "answer_id INTEGER(3))";

    public static final String createGameRocord = "CREATE TABLE IF NOT EXISTS " + game_record
            + " (record_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "record INTEGER(3) NOT NULL,"
            + "time VARCHAR(20) NOT NULL)";


    // 建構子，一般的應用都不需要修改
    public tableDao(Context context) {
        db = SqlHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }


    /*****
     * 跟　favorites相關的所有SQL語法
     *****/


    public boolean deleteFavorItem(String theWord) {

        String where = "favor_word" +  "=\"" + theWord + "\"";

        // 刪除指定資料並回傳刪除是否成功
        return db.delete(favorites, where, null) > 0;
    }
    public int getFavorsCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + favorites, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        cursor.close();
        return result;
    }
    public void insertFavorites(String favorWord, String meaning ) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
            // 加入ContentValues物件包裝的新增資料
            // 第一個參數是欄位名稱， 第二個參數是欄位的資料
            cv.put("favor_word", favorWord);
            cv.put("favor_meaning", meaning);

            // 第一個參數是表格名稱
            // 第二個參數是沒有指定欄位值的預設值
            // 第三個參數是包裝新增資料的ContentValues物件
            db.insert(favorites, null, cv);

    }
    public boolean getFavoritesbyWord(String theWord) {
        boolean result =true;
        Cursor cursor = null;

        String where = "favor_word" +  "=\"" + theWord + "\"";
        // 執行查詢
        cursor = db.query(
                favorites, null, where, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            result = false;
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }
    public ArrayList<HashMap<String,Object >> getFavoritesWords() {
        Cursor cursor = null;
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String,Object>>();
        // 執行查詢
        cursor = db.query(
                favorites, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("單字",cursor.getString(1));
            item.put("解釋",cursor.getString(2));
            item.put("刪除按鈕", R.drawable.trash);
            item.put("新增按鈕", R.drawable.add);
            result.add(item);
        }
        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    /*****
     * 跟word相關的所有SQL語法
     *****/

    // 新增Words物件
    public void insertWords(sqlite.Words addWords) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put("w_id", addWords.getW_id());
        cv.put("word", addWords.getWord());
        cv.put("r_id", addWords.getR_id());


        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(words, null, cv);
    }

//    // 讀取所有words記事資料
//    public List<sqlite.Words> getAllWords() {
//        List<sqlite.Words> result = new ArrayList<>();
//        Cursor cursor = db.query(
//                words, null, null, null, null, null, null, null);
//
//        while (cursor.moveToNext()) {
//            result.add(getWordsRecord(cursor));
//        }
//
//        cursor.close();
//        return result;
//    }

//    // 讀取相對應的words記事資料
//    public List<sqlite.Words> getWords(String wordsType) {
//        List<sqlite.Words> result = new ArrayList<>();
//        String where = "TOEIC" + "= " + "1";
//        Cursor cursor = db.query(
//                words, null, where, null, null, null, null, null);
//
//        while (cursor.moveToNext()) {
//            result.add(getWordsRecord(cursor));
//        }
//
//        cursor.close();
//        return result;
//    }

    public boolean getWordsByWord(String theWord) {

        boolean result =true;
        Cursor cursor = null;

        String where = "word" +  "=\"" + theWord + "\"";
        // 執行查詢
        cursor = db.query(
                words, null, where, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            result = false;
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }
    public List<sqlite.Words> getWordsById(List<Exp> expReturnList) {
        // 準備回傳結果用的物件
        List<sqlite.Words> result = new ArrayList<>();
        Cursor cursor = null;
        for (int i = 0; i < expReturnList.size(); i++) {
            // 使用編號為查詢條件
            String where = "w_id" + "=" + expReturnList.get(i).getWord_id();
            // 執行查詢
            cursor = db.query(
                    words, null, where, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                result.add(getWordsRecord(cursor));
            }
        }
        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    //for game answers
    public Words getWordById(int w_id) {
        Words word = null;
        String where = "w_id=" + w_id;
        Cursor cursor = db.query(
                words, null, where, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            word = getWordsRecord(cursor);
        }
        cursor.close();
        return word;
    }


    public boolean have10Word(int max) {
        boolean flag = false;
        Cursor cursor;
        String query = "";
        query = "SELECT * FROM words ORDER BY w_id ASC limit " + max + ",10";
        cursor = db.rawQuery(query, null);
        int cur_num = cursor.getCount();
        if(cur_num==0){
            flag = true;
        }
       return flag;
    }
    //選擇10張未學習過的卡片，新增至Exp table讓使用者學習
    public List<sqlite.Words> top10Words(int max) {
        List<sqlite.Words> result = new ArrayList<>();
        int i_exp = 10;
        System.out.println("going to query 10 data");
        Cursor cursor;
        String query = "";
        if (max == 0) {
            query = "SELECT * FROM words ORDER BY w_id ASC limit 0,10";

        } else {
            query = "SELECT * FROM words ORDER BY w_id ASC limit " + max + ",10";
            //query = "SELECT * FROM words ORDER BY id ASC limit " + max + ",10"; //max+1~~max+1+10    21~30
        }
        cursor = db.rawQuery(query, null);

        int cur_num = cursor.getCount();
        System.out.println("cur_num="+ cur_num);
        if(cur_num!=10){
            //int result = 0;
//            Cursor favword_cur = db.rawQuery("SELECT COUNT(*) FROM "+words, null);
//         //   System.out.println("allwords="+ favword_cur.getCount());
//            cur_num = (favword_cur.getCount())%10;
            String fav_query = "SELECT * FROM words ORDER BY w_id ASC limit " + max + ","+cur_num;
            cursor = db.rawQuery(fav_query, null);
            i_exp = cur_num;
//            System.out.println("favWordsCount = " + cur_num);
        }

        System.out.println("query = " + query);

        while (cursor.moveToNext()) {
            result.add(getWordsRecord(cursor));
        }

        for (int i = 0; i < i_exp; i++) {
            Exp expAdd = new Exp(user_id, result.get(i).getW_id(), 1, i, 1, "");
            this.insertExp(expAdd);
        }
        cursor.close();
        return result;
    }


    // 把Cursor目前的資料包裝為Words物件
    public sqlite.Words getWordsRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        sqlite.Words result = new sqlite.Words();

        result.setW_id(cursor.getInt(0));
        result.setWord(cursor.getString(1));
        result.setR_id(cursor.getInt(2));


        // 回傳結果
        return result;
    }

    // 取得words資料數量
    public int getWordsCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + words, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
      }

      cursor.close();
       return result;
    }

//    // 刪除參數指定編號的資料
//    public boolean deleteWords(int id) {
//        // 設定條件為編號，格式為「欄位名稱=資料」
//        String where = id + "=" + id;
//        // 刪除指定編號資料並回傳刪除是否成功
//        return db.delete(words, where, null) > 0;
//    }

    // 刪除words的資料
    public boolean deleteWords() {
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(words, null, null) > 0;
    }
    /*****
     * 跟root相關的SQL語法
     *****/
    public void insertRoot(Root addRoot){
        ContentValues rootCv = new ContentValues();

        rootCv.put("r_id",addRoot.getR_id());
        rootCv.put("root",addRoot.getRoot());

        db.insert(root,null,rootCv);
    }
    public List<Words> getWordByRootId(int root_id) {
        // 準備回傳結果用的物件
        List<Words> result = new ArrayList<>();
        // 使用編號為查詢條件
        String where = "r_id" + "=" + root_id;
        // 執行查詢
        Cursor cursor = db.query(
                words, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getWordsRecord(cursor));
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    /*****
     * 跟meaning相關的SQL語法
     *****/

    // 新增Meaning物件
    public void insertMeaning(Meaning addMeaning) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put("m_id", addMeaning.getM_id());
        cv.put("w_id", addMeaning.getW_id());
        cv.put("part_of_speech", addMeaning.getPart_of_speech());
        cv.put("EngChiTra", addMeaning.getEngChiTra());


        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(meaning, null, cv);
    }

    //透過ID搜尋meaning
    public List<Meaning> getMeaningById(int id) {
        // 準備回傳結果用的物件
        List<Meaning> result = new ArrayList<>();
        // 使用編號為查詢條件
        String where = "w_id" + "=" + id;
        // 執行查詢
        Cursor cursor = db.query(
                meaning, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getMeaningRecord(cursor));
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    // 把Cursor目前的資料包裝為Meaning物件
    public Meaning getMeaningRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Meaning result = new Meaning();

        result.setM_id(cursor.getInt(0));
        result.setW_id(cursor.getInt(1));
        result.setPart_of_speech(cursor.getString(2));
        result.setEngChiTra(cursor.getString(3));

        // 回傳結果
        return result;
    }
    // 刪除meaning的資料
    public boolean deleteMeaning() {
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(meaning, null, null) > 0;
    }


    // 取得meaning資料數量
    public int getMeaningCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + meaning, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }
    public int getMeaningwordCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(w_id)  FROM " + meaning, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }


    /*****
     * 跟exp相關的SQL語法
     *****/




    // 新增Exp物件
    public void insertExp(Exp addExp) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put("user_id", addExp.getUser_id());
        cv.put("word_id", addExp.getWord_id());
        cv.put("level", addExp.getLevel());
        cv.put("position", addExp.getPosition());
        cv.put("learned", addExp.getLearned());
        cv.put("Last_Learnt_Time", addExp.getLast_Learnt_Time());

        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(exp, null, cv);
    }

    // 取得exp資料數量
    public int getExpCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + exp, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    // 取得exp中word_id最大為多少
    public int getExpMaxWordId() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT Max(word_id) FROM " + exp, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        System.out.println("exp max word_id :" + result);
        cursor.close();
        return result;
    }

    // 刪除參數指定編號的資料
    public boolean deleteExp() {
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(exp, null, null) > 0;
    }

    public Exp getExpById(int id) {
        // 準備回傳結果用的物件
        Exp result = new Exp();
        // 使用編號為查詢條件
        String where = "word_id" + "=" + id;
        // 執行查詢
        Cursor cursor = db.query(
                exp, null, where, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            result = getExpRecord(cursor);
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    // 把Cursor目前的資料包裝為Exp物件
    public Exp getExpRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Exp result = new Exp();

        result.setUser_id(cursor.getString(0));
        result.setWord_id(cursor.getInt(1));
        result.setLevel(cursor.getInt(2));
        result.setPosition(cursor.getInt(3));
        result.setLearned(cursor.getInt(4));
        result.setLast_Learnt_Time(cursor.getString(5));

        // 回傳結果
        return result;
    }

    // 取得exp資料數量
    public int getBoxCount(int level) {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM exp WHERE level=" + level + "", null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    //取得箱子內該level的卡片
    public List<Exp> boxLevelData(int level) {
        // 準備回傳結果用的物件
        List<Exp> result = new ArrayList<>();
        // 使用編號為查詢條件
        String where = " level = " + level;
        // 執行查詢
        Cursor cursor = db.query(
                exp, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getExpRecord(cursor));
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    //確認要前往的箱子單字數
    public boolean checkBoxCount(int level){
        int count = this.getBoxCount(level)+1;
        System.out.println("method count : " + count);
        boolean insertOk = false;

        if(level==1) {
            if (count < box_level_1_Limit)
                insertOk = true;
            else
                insertOk = false;
        }else if(level==2) {
            if (count < box_level_2_Limit)
                insertOk = true;
            else
                insertOk = false;
        } else if(level==3) {
            if (count < box_level_3_Limit)
                insertOk = true;
            else
                insertOk = false;
        } else if(level==4) {
            if (count < box_level_4_Limit)
                insertOk = true;
            else
                insertOk = false;
        }else if(level==5) {
            if (count < box_level_5_Limit)
                insertOk = true;
            else
                insertOk = false;
        }
        return insertOk;
    }

    // 修改參數指定的物件
    public void updateExp(Exp expUpdate) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        cv.put("level", expUpdate.getLevel());
        cv.put("position", expUpdate.getPosition());
        cv.put("learned", expUpdate.getLearned());
        cv.put("Last_Learnt_Time", expUpdate.getLast_Learnt_Time());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = "word_id =" + expUpdate.getWord_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        db.update(exp, cv, where, null);
    }

    // 新增GameQuestion物件
    public void insertQuestions(sqlite.GameQuestion addQuestions) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put("question_id", addQuestions.getQuestion_id());
        cv.put("question", addQuestions.getQuestion());
        cv.put("answer_id", addQuestions.getAnswer_id());

        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(game_question, null, cv);
    }

    //以id取得一筆GameQuestion資料
    public GameQuestion getQuestionById(int q_id) {
        GameQuestion question = null;
        String where = "question_id=" + q_id;
        Cursor cursor = db.query(
                game_question, null, where, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            question = getQuestionRecord(cursor);
        }
        cursor.close();
        return question;
    }

    // 把Cursor目前的資料包裝為GameQuestion物件
    public GameQuestion getQuestionRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        GameQuestion result = new GameQuestion();

        result.setQuestion_id(cursor.getInt(0));
        result.setQuestion(cursor.getString(1));
        result.setAnswer_id(cursor.getInt(2));

        // 回傳結果
        return result;
    }

    public int getGameQuestionCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + game_question, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }


    //新增GameRecord物件
    public void insertGameRecord(sqlite.GameRecord addRecord) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put("record_id", addRecord.getRecord_id());
        cv.put("record", addRecord.getRecord());
        cv.put("time", addRecord.getTime());

        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(game_record, null, cv);
    }

    //取得前10名的遊戲紀錄
    public ArrayList<HashMap<String,Object >> getTop10GameRecord() {
        Cursor cursor = null;
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String,Object>>();
        // 執行查詢
        String query;
        if (getGameRecordCount() < 10) {
            query = "SELECT * FROM game_record ORDER BY record DESC LIMIT " + getGameRecordCount();
        } else {
            query = "SELECT * FROM game_record ORDER BY record DESC LIMIT 10";
        }
        cursor = db.rawQuery(query, null);
        int i = 1;
        while (cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("排名", i + "");
            item.put("答題數", cursor.getInt(1) + "");
            item.put("日期", cursor.getString(2));
            result.add(item);
            i++;
        }
        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return result;
    }

    // 把Cursor目前的資料包裝為GameQuestion物件
    public GameRecord getRecordRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        GameRecord result = new GameRecord();

        result.setRecord_id(cursor.getInt(0));
        result.setRecord(cursor.getInt(1));
        result.setTime(cursor.getString(2));

        // 回傳結果
        return result;
    }

    //以id取得一筆GameQuestion資料
    public GameRecord getRecordById(int r_id) {
        GameRecord record = null;
        String where = "question_id=" + r_id;
        Cursor cursor = db.query(
                game_record, null, where, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            record = getRecordRecord(cursor);
        }
        cursor.close();
        return record;
    }

    public int getGameRecordCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + game_record, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    public GameRecord getLastGameRecord() {
        int last_id = getGameRecordCount();
        GameRecord record = null;
        String where = "record_id=" + last_id;
        Cursor cursor = db.query(
                game_record, null, where, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            record = getRecordRecord(cursor);
        }
        cursor.close();
        return record;
    }

    public void sampleWord() {
        String data = "1,conform,27\n" +
                "2,generous,29\n" +
                "3,alleviate,43\n" +
                "4,sentimental,79\n" +
                "5,auction,0\n" +
                "6,resent,79\n" +
                "7,compel,67\n" +
                "8,relieve,43\n" +
                "9,suspect,11\n" +
                "10,halitosis,0\n" +
                "11,exaggerate,0\n" +
                "12,diameter,51\n" +
                "13,recede,17\n" +
                "14,dictate,21\n" +
                "15,exalt,13\n" +
                "16,embargo,2\n" +
                "17,explicate,69\n" +
                "18,misapprehend,73\n" +
                "19,synonym,62\n" +
                "20,transliterate,44\n" +
                "21,epilogue,46\n" +
                "22,elect,0\n" +
                "23,symmetry,51\n" +
                "24,quiescence,0\n" +
                "25,monologue,46\n" +
                "26,oral,63\n" +
                "27,contradict,21\n" +
                "28,innocent,58\n" +
                "29,impotent,71\n" +
                "30,philology,46\n" +
                "31,aggress,31\n" +
                "32,adjunct,40\n" +
                "33,junction,40\n" +
                "34,interject,38\n" +
                "35,degenerate,29\n" +
                "36,commit,54\n" +
                "37,explode,0\n" +
                "38,premonitory,55\n" +
                "39,infract,28\n" +
                "40,denigrate,0\n" +
                "41,obsess,78\n" +
                "42,subscribe,77\n" +
                "43,igneous,35\n" +
                "44,compact,65\n" +
                "45,abnegate,57\n" +
                "46,manner,0\n" +
                "47,ignite,35\n" +
                "48,exemption,0\n" +
                "49,denominate,59\n" +
                "50,superfluous,26\n" +
                "51,cease,0\n" +
                "52,negate,57\n" +
                "53,extraordinary,64\n" +
                "54,genesis,29\n" +
                "55,megacephalous,0\n" +
                "56,herb,33\n" +
                "57,disintegrate,36\n" +
                "58,concord,19\n" +
                "59,influence,26\n" +
                "60,abbreviate,3\n" +
                "61,omnipotent,71\n" +
                "62,marine,0\n" +
                "63,conscience,76\n" +
                "64,repress,72\n" +
                "65,confluent,26\n" +
                "66,convoke,10\n" +
                "67,dislocate,45\n" +
                "68,involve,7\n" +
                "69,decapitate,16\n" +
                "70,gravity,30\n" +
                "71,deprive,0\n" +
                "72,effluent,26\n" +
                "73,elegant,0\n" +
                "74,circumvolve,7\n" +
                "75,radical,74\n" +
                "76,renovate,0\n" +
                "77,lucent,47\n" +
                "78,spoil,0\n" +
                "79,consignment,0\n" +
                "80,suppress,72\n" +
                "81,optic,0\n" +
                "82,sedition,37\n" +
                "83,compunction,0\n" +
                "84,sacraed,0\n" +
                "85,integrate,36\n" +
                "86,peroral,63\n" +
                "87,habitant,32\n" +
                "88,replenish,0\n" +
                "89,preeminment,52\n" +
                "90,intermit,54\n" +
                "91,foreground,8\n" +
                "92,attain,1\n" +
                "93,elucidate,47\n" +
                "94,liberal,0\n" +
                "95,elapse,41\n" +
                "96,illuminate,48\n" +
                "97,liquefy,0\n" +
                "98,inhabit,32\n" +
                "99,herbal,33\n" +
                "100,reform,27\n" +
                "101,complicate,69\n" +
                "102,circumscribe,77\n" +
                "103,amphitheatre,6\n" +
                "104,merchandise,0\n" +
                "105,dominant,0\n" +
                "106,facile,23\n" +
                "107,boost,0\n" +
                "108,extempore,4\n" +
                "109,annotate,60\n" +
                "110,dialogue,46\n" +
                "111,judicious,39\n" +
                "112,mirage,53\n" +
                "113,propone,0\n" +
                "114,inhibit,34\n" +
                "115,eradicate,74\n" +
                "116,pedagogy,66\n" +
                "117,refract,28\n" +
                "118,remark,0\n" +
                "119,possess,78\n" +
                "120,perpend,68\n" +
                "121,barrel,2\n" +
                "122,bankrupt,75\n" +
                "123,prohibit,34\n" +
                "124,expel,67\n" +
                "125,compensate,0\n" +
                "126,immortal,56\n" +
                "127,aggravate,30\n" +
                "128,connate,0\n" +
                "129,allocate,45\n" +
                "130,suborn,0\n" +
                "131,hypersensitive,9\n" +
                "132,precedent,17\n" +
                "133,compress,72\n" +
                "134,congress,31\n" +
                "135,jury,0\n" +
                "136,circumstance,0\n" +
                "137,implant,0\n" +
                "138,remain,50\n" +
                "139,diction,21\n" +
                "140,inscribe,77\n" +
                "141,innominate,59\n" +
                "142,depress,72\n" +
                "143,expiration,0\n" +
                "144,exhibit,34\n" +
                "145,altitude,13\n" +
                "146,anonym,62\n" +
                "147,subordinate,64\n" +
                "148,append,68\n" +
                "149,comprehend,73\n" +
                "150,elevate,43\n" +
                "151,fragment,28\n" +
                "152,counsel,0\n" +
                "153,irrupt,75\n" +
                "154,benefactor,23\n" +
                "155,fraction,28\n" +
                "156,diminish,0\n" +
                "157,egress,31\n" +
                "158,collapse,41\n" +
                "159,melancholia,0\n" +
                "160,suspend,68\n" +
                "161,nocuous,58\n" +
                "162,lease,0\n" +
                "163,concur,20\n" +
                "164,prospect,11\n" +
                "165,integral,36\n" +
                "166,ambit,37\n" +
                "167,urgent,0\n" +
                "168,forecast,8\n" +
                "169,collide,0\n" +
                "170,detain,1\n" +
                "171,emit,54\n" +
                "172,prominent,52\n" +
                "173,oppress,72\n" +
                "174,abrade,0\n" +
                "175,immit,54\n" +
                "176,finite,24\n" +
                "177,equator,22\n" +
                "178,approximate,0\n" +
                "179,inject,38\n" +
                "180,impel,67\n" +
                "181,collocate,45\n" +
                "182,annihilate,0\n" +
                "183,apprehend,73\n" +
                "184,monarch,15\n" +
                "185,anarchy,15\n" +
                "186,potential,71\n" +
                "187,commemorate,0\n" +
                "188,literal,44\n" +
                "189,allotment,0\n" +
                "190,mortician,56\n" +
                "191,magnify,49\n" +
                "192,demarcate,0\n" +
                "193,affluent,0\n" +
                "194,adjust,0\n" +
                "195,cordial,19\n" +
                "196,deliberation,0\n" +
                "197,attend,5\n" +
                "198,cargo,0\n" +
                "199,transgress,31\n" +
                "200,insular,0\n" +
                "201,reflect,25\n" +
                "202,appetite,0\n" +
                "203,remit,54\n" +
                "204,admonish,55\n" +
                "205,equable,22\n" +
                "206,accord,19\n" +
                "207,adhere,0\n" +
                "208,assess,78\n" +
                "209,respect,11\n" +
                "210,magnific,49\n" +
                "211,exploitation,0\n" +
                "212,hypermilitant,9\n" +
                "213,depose,70\n" +
                "214,equivalent,22\n" +
                "215,immanent,50\n" +
                "216,inexorable,63\n" +
                "217,suicide,18\n" +
                "218,uniform,27\n" +
                "219,coordinate,64\n" +
                "220,prescient,76\n" +
                "221,recur,20\n" +
                "222,excurse,20\n" +
                "223,judicial,39\n" +
                "224,conscribe,77\n" +
                "225,negade,57\n" +
                "226,matriarch,0\n" +
                "227,excide,18\n" +
                "228,eject,38\n" +
                "229,extinct,12\n" +
                "230,propel,67\n" +
                "231,brevity,3\n" +
                "232,judicature,39\n" +
                "233,delegate,42\n" +
                "234,legend,42\n" +
                "235,discord,19\n" +
                "236,retrospect,11\n" +
                "237,imminent,52\n" +
                "238,nominate,59\n" +
                "239,inflect,25\n" +
                "240,antique,0\n" +
                "241,obliterate,44\n" +
                "242,impact,65\n" +
                "243,affair,0\n" +
                "244,navigate,0\n" +
                "245,biography,0\n" +
                "246,chronometer,51\n" +
                "247,authentic,0\n" +
                "248,disrupt,75\n" +
                "249,impede,66\n" +
                "250,assent,79\n" +
                "251,infrastructure,0\n" +
                "252,reposit,70\n" +
                "253,inferior,0\n" +
                "254,renounce,61\n" +
                "255,refine,24\n" +
                "256,admire,53\n" +
                "257,flection,25\n" +
                "258,connote,60\n" +
                "259,contend,5\n" +
                "260,audit,0\n" +
                "261,magnanimous,49\n" +
                "262,transit,37\n" +
                "263,neglect,57\n" +
                "264,corruption,75\n" +
                "265,permanent,50\n" +
                "266,reprehend,73\n" +
                "267,estate,0\n" +
                "268,deform,27\n" +
                "269,hazardous,0\n" +
                "270,complacent,0\n" +
                "271,adjourn,0\n" +
                "272,mortgage,56\n" +
                "273,deport,0\n" +
                "274,subjunction,40\n" +
                "275,barrier,0\n" +
                "276,insecticide,18\n" +
                "277,ambulance,14\n" +
                "278,embarrass,2\n" +
                "279,relegate,42\n" +
                "280,provoke,10\n" +
                "281,parliament,0\n" +
                "282,pedestrian,66\n" +
                "283,instinct,12\n" +
                "284,contemporary,4\n" +
                "285,capital,16\n" +
                "286,patriarch,15\n" +
                "287,cohabit,32\n" +
                "288,expedition,66\n" +
                "289,itinerary,37\n" +
                "290,announce,61\n" +
                "291,fragile,28\n" +
                "292,dispose,70\n" +
                "293,implicate,69\n" +
                "294,pseudonym,62\n" +
                "295,indicate,21\n" +
                "296,denounce,61\n" +
                "297,adequate,22\n" +
                "298,luminesce,48\n" +
                "299,circumambulate,14\n" +
                "300,amphibiology,6\n"
;

        String[] dataArray = data.split("\n");
        for (int i = 0; i < dataArray.length; i++) {

            String[] wordsArray = dataArray[i].split(",");
            int w_id = Integer.parseInt(wordsArray[0]);
            int r_id = Integer.parseInt(wordsArray[2]);
            sqlite.Words wordsAdd = new sqlite.Words(w_id, wordsArray[1],r_id);
            insertWords(wordsAdd);

        }
//        for (int i = 0; i < dataArray.length; i++) {
//
//            String[] wordsArray = dataArray[i].split(",");
//            int id = Integer.parseInt(wordsArray[0]);
//            sqlite.Words wordsAdd = new sqlite.Words(id, wordsArray[1], "", 0, 0, 0, 0, 0, 1, 0);
//            insertWords(wordsAdd);
//
//        }

    }

    public void sampleMeaning() {
        String data = "1,1,[v.],使適合、符合\n" +
                "2,2,[adj.],慷慨的、豐富的\n" +
                "3,3,[v.],減輕、緩和\n" +
                "4,4,[adj.],多愁善感的\n" +
                "5,5,[n.],拍賣\n" +
                "6,5,[v.],拍賣\n" +
                "7,6,[v.],憤慨、怨恨\n" +
                "8,7,[v.],強迫、強求\n" +
                "9,8,[v.],減輕、救濟、襯托\n" +
                "10,9,[n.],嫌疑犯\n" +
                "11,9,[v.],懷疑\n" +
                "12,9,[adj.],不可信的\n" +
                "13,10,[n.],口臭\n" +
                "14,11,[v.],誇大、誇張\n" +
                "15,12,[n.],直徑\n" +
                "16,13,[v.],後退、引退\n" +
                "17,14,[v.],口述、命令\n" +
                "18,15,[v.],提高、提升\n" +
                "19,16,[v.],禁運、禁止\n" +
                "20,17,[v.],解釋、闡明\n" +
                "21,18,[v.],誤解\n" +
                "22,19,[n.],同義詞\n" +
                "23,20,[v.],翻譯\n" +
                "24,21,[n.],結尾語、收場白\n" +
                "25,22,[v.],選舉、決定\n" +
                "26,22,[adj.],當選的、卓越的\n" +
                "27,23,[n.],對稱、均衡\n" +
                "28,24,[n.],沉默、靜止\n" +
                "29,25,[n.],獨白、獨角戲、長篇大論\n" +
                "30,26,[n.],口試\n" +
                "31,26,[adj.],口頭的\n" +
                "32,27,[v.],反駁、與..相矛盾\n" +
                "33,28,[adj.],無罪的、天真的\n" +
                "34,29,[adj.],無力氣的、無能為力的\n" +
                "35,30,[n.],語言學、文獻學\n" +
                "36,31,[n.],侵略、挑釁\n" +
                "37,32,[n.],附屬物、助手\n" +
                "38,33,[n.],連接、接合點\n" +
                "39,34,[v.],插嘴、插話\n" +
                "40,35,[v.],墮落、衰退、退化\n" +
                "41,35,[adj.],墮落、衰退、退化\n" +
                "42,36,[v.],委派、指定\n" +
                "43,37,[n.],爆發、推翻\n" +
                "44,38,[adj.],預告的、前兆的\n" +
                "45,39,[v.],破壞\n" +
                "46,40,[v.],使變黑、詆毀\n" +
                "47,41,[v.],迷住、纏住、困擾\n" +
                "48,42,[v.],捐助、簽屬、訂購\n" +
                "49,43,[adj.],火的\n" +
                "50,44,[n.],合約\n" +
                "51,44,[adj.],結實的、簡潔的\n" +
                "52,45,[v.],放棄、克制\n" +
                "53,46,[n.],舉止、風度\n" +
                "54,47,[v.],著火、點燃\n" +
                "55,48,[n.],解除、免除、免稅\n" +
                "56,49,[v.],命名\n" +
                "57,49,[adj.],有特定名稱的\n" +
                "58,50,[adj.],過剩的\n" +
                "59,51,[v.],終止、結束\n" +
                "60,52,[v.],否定、取消\n" +
                "61,53,[adj.],特別的\n" +
                "62,54,[n.],起源，創始\n" +
                "63,55,[adj.],巨額的\n" +
                "64,56,[n.],藥草\n" +
                "65,57,[v.],使瓦解、使崩潰、使衰退\n" +
                "66,58,[n.],協調、同意\n" +
                "67,59,[n.],影響、權勢\n" +
                "68,59,[v.],影響、感化\n" +
                "69,60,[v.],縮短、縮寫\n" +
                "70,61,[adj.],全能的\n" +
                "71,62,[adj.],海的、航海的\n" +
                "72,63,[n.],良知、道德心\n" +
                "73,64,[v.],鎮壓、抑制\n" +
                "74,65,[n.],支流\n" +
                "75,65,[adj.],匯合的\n" +
                "76,66,[v.],召集\n" +
                "77,67,[v.],使位置移動、弄亂\n" +
                "78,68,[v.],牽連\n" +
                "79,69,[v.],斬首、解雇\n" +
                "80,70,[n.],重力、嚴重性\n" +
                "81,71,[v.],剝奪、使喪失\n" +
                "82,72,[n.],流出物\n" +
                "83,72,[adj.],流出的\n" +
                "84,73,[adj.],精緻的、簡要明確的\n" +
                "85,74,[v.],盤繞\n" +
                "86,75,[n.],基礎\n" +
                "87,75,[adj.],基本的\n" +
                "88,76,[v.],修復、改善、更新\n" +
                "89,77,[adj.],發光的、透明的\n" +
                "90,78,[v.],寵壞、損壞\n" +
                "91,79,[n.],委託\n" +
                "92,80,[v.],查禁\n" +
                "93,81,[adj.],光學的、視覺的\n" +
                "94,82,[n.],暴動\n" +
                "95,83,[n.],內疚\n" +
                "96,84,[adj.],神聖的、宗教的\n" +
                "97,85,[v.],合併\n" +
                "98,86,[adj.],口服的\n" +
                "99,87,[n.],居民\n" +
                "100,88,[v.],填充\n" +
                "101,89,[adj.],顯著的、卓越的\n" +
                "102,90,[v.],中斷、暫停\n" +
                "103,91,[n.],前景\n" +
                "104,91,[v.],強調\n" +
                "105,92,[v.],達到\n" +
                "106,93,[v.],闡明、說明\n" +
                "107,94,[adj.],自由的、慷慨的\n" +
                "108,95,[v.],(時間)過去、消逝\n" +
                "109,96,[v.],照明、啟發\n" +
                "110,97,[v.],使液化\n" +
                "111,98,[n.],居住於、棲息於\n" +
                "112,99,[adj.],草本植物的、草藥書\n" +
                "113,100,[v.],改革\n" +
                "114,101,[v.],使複雜、使陷入\n" +
                "115,102,[v.],限制、為…下定義\n" +
                "116,103,[n.],圓形劇場\n" +
                "117,104,[v.],買賣、經營\n" +
                "118,104,[n.],商品、貨物\n" +
                "119,105,[adj.],有統治權的、占優勢的、支配的\n" +
                "120,106,[adj.],易得到的、流暢的\n" +
                "121,107,[n.],促進、推進\n" +
                "122,108,[adj.],即席的\n" +
                "123,108,[adv.],即興地\n" +
                "124,109,[v.],注解\n" +
                "125,110,[n.],對話、對白\n" +
                "126,111,[adj.],明智而審慎的\n" +
                "127,112,[n.],妄想、海市蜃樓\n" +
                "128,113,[v.],提議、倡言\n" +
                "129,114,[v.],抑制、約束\n" +
                "130,115,[v.],消滅、連根拔除\n" +
                "131,116,[n.],教育法\n" +
                "132,117,[v.],折射\n" +
                "133,118,[n.],談及、評論、注意\n" +
                "134,118,[v.],談及、評論、注意\n" +
                "135,119,[v.],擁有、掌握、控制\n" +
                "136,120,[v.],仔細考慮\n" +
                "137,121,[n.],大桶、大量\n" +
                "138,121,[v.],將…裝桶\n" +
                "139,122,[n.],破産者\n" +
                "140,122,[v.],使破產\n" +
                "141,122,[adj.],破產的、徹底缺乏的\n" +
                "142,123,[v.],妨礙、禁止\n" +
                "143,124,[v.],驅逐、開除、排出\n" +
                "144,125,[v.],補償、賠償、報酬\n" +
                "145,126,[adj.],不朽的\n" +
                "146,127,[v.],使惡化、加重\n" +
                "147,128,[adj.],天賦的\n" +
                "148,129,[v.],分配\n" +
                "149,130,[v.],教唆、收買\n" +
                "150,131,[adj.],過敏的\n" +
                "151,132,[n.],前例、慣例\n" +
                "152,132,[adj.],前面的\n" +
                "153,133,[v.],壓縮、歸納\n" +
                "154,134,[n.],會議、國會 \n" +
                "155,135,[n.],陪審團\n" +
                "156,136,[n.],情形、環境\n" +
                "157,137,[v.],灌輸、種植\n" +
                "158,138,[n.],剩下、保持、歸屬\n" +
                "159,139,[n.],措辭\n" +
                "160,140,[v.],雕、刻\n" +
                "161,141,[adj.],無名的、匿名\n" +
                "162,142,[v.],壓下、使喪志\n" +
                "163,143,[n.],期滿、終止\n" +
                "164,144,[n.],展示物\n" +
                "165,144,[v.],展出、陳列\n" +
                "166,145,[n.],高度\n" +
                "167,146,[n.],匿名\n" +
                "168,147,[n.],部屬\n" +
                "169,147,[v.],使服從\n" +
                "170,147,[adj.],次要的\n" +
                "171,148,[v.],掛上、附加\n" +
                "172,149,[v.],瞭解、領悟、包含\n" +
                "173,150,[v.],抬起、舉起\n" +
                "174,151,[n.],碎片、未完成部分\n" +
                "175,152,[n.],忠告、商議\n" +
                "176,153,[v.],闖入、大量繁殖\n" +
                "177,154,[n.],恩人、施恩者\n" +
                "178,155,[n.],碎片、片段\n" +
                "179,156,[v.],減少縮減\n" +
                "180,157,[n.],外出\n" +
                "181,158,[n.],衰竭、瓦解\n" +
                "182,158,[v.],倒塌、瓦解\n" +
                "183,159,[n.],憂鬱症\n" +
                "184,160,[v.],懸浮、暫時取消\n" +
                "185,161,[adj.],有害的\n" +
                "186,162,[n.],租約、租契\n" +
                "187,162,[v.],出租\n" +
                "188,163,[v.],同時發生、同意\n" +
                "189,164,[n.],期待、景象\n" +
                "190,164,[v.],勘探\n" +
                "191,165,[adj.],完整的\n" +
                "192,166,[n.],周圍、範圍、領域\n" +
                "193,167,[adj.],急迫的\n" +
                "194,168,[n.],預測\n" +
                "195,168,[v.],預測\n" +
                "196,169,[v.],碰撞、衝突\n" +
                "197,170,[v.],使延遲、耽擱\n" +
                "198,171,[v.],散發、發行\n" +
                "199,172,[adj.],傑出的、重要的\n" +
                "200,173,[v.],壓迫、鬱悶\n" +
                "201,174,[n.],擦掉、磨損\n" +
                "202,175,[v.],注入、注射\n" +
                "203,176,[adj.],有限的\n" +
                "204,177,[n.],赤道\n" +
                "205,178,[v.],接近、模擬\n" +
                "206,178,[adj.],近似的\n" +
                "207,179,[v.],注射、引入\n" +
                "208,180,[v.],推動、激勵\n" +
                "209,181,[v.],排列、搭配\n" +
                "210,182,[v.],消滅、殲滅\n" +
                "211,183,[v.],逮捕、理解\n" +
                "212,184,[n.],君主\n" +
                "213,185,[n.],無政府狀態\n" +
                "214,186,[n.],可能性\n" +
                "215,186,[adj.],潛在的\n" +
                "216,187,[v.],慶祝、紀念\n" +
                "217,188,[adj.],不誇張的、字母的\n" +
                "218,189,[n.],分配\n" +
                "219,190,[n.],殯儀業者\n" +
                "220,191,[v.],放大\n" +
                "221,192,[v.],定…的界線、區分\n" +
                "222,193,[adj.],豐富的、富裕的\n" +
                "223,194,[v.],調節、校正、解決\n" +
                "224,195,[adj.],衷心的、熱心的\n" +
                "225,196,[n.],考慮、從容、商議\n" +
                "226,197,[v.],出席、護理、致力於\n" +
                "227,198,[n.],貨物、船貨\n" +
                "228,199,[v.],違背、越過\n" +
                "229,200,[adj.],島嶼的、孤立的\n" +
                "230,201,[v.],反射、反映\n" +
                "231,202,[n.],食慾、愛好\n" +
                "232,203,[v.],寬恕、免除\n" +
                "233,204,[v.],告誡、警告\n" +
                "234,205,[adj.],穩定的、和平的\n" +
                "235,206,[n.],一致、調和\n" +
                "236,206,[v.],一致、調和\n" +
                "237,207,[v.],堅持、黏附\n" +
                "238,208,[v.],評估、估價\n" +
                "239,209,[n.],問候、關係\n" +
                "240,209,[v.],尊敬\n" +
                "241,210,[adj.],宏大的\n" +
                "242,211,[n.],開發、開採\n" +
                "243,212,[adj.],極度好戰的\n" +
                "244,213,[v.],罷免、置放\n" +
                "245,214,[n.],同義字\n" +
                "246,214,[adj.],相等的、等值的\n" +
                "247,215,[adj.],內在的\n" +
                "248,216,[adj.],無法改變的\n" +
                "249,217,[n.],自殺\n" +
                "250,217,[v.],自殺\n" +
                "251,218,[n.],制服\n" +
                "252,218,[v.],使一樣\n" +
                "253,218,[adj.],一致的\n" +
                "254,219,[adj.],同等的\n" +
                "255,219,[v.],協調\n" +
                "256,220,[adj.],預知的\n" +
                "257,221,[v.],再發生、被重新提出\n" +
                "258,222,[v.],遠足、旅行\n" +
                "259,223,[adj.],司法的、評判的、公正明斷的\n" +
                "260,224,[v.],徵招、限制\n" +
                "261,225,[n.],叛徒\n" +
                "262,225,[v.],背叛\n" +
                "263,225,[adj.],背棄的\n" +
                "264,226,[n.],女統治者、女家長\n" +
                "265,227,[v.],切除\n" +
                "266,228,[v.],噴射、逐出\n" +
                "267,229,[adj.],絕種的、失效的\n" +
                "268,230,[v.],推進\n" +
                "269,231,[n.],時間短暫、說話簡潔\n" +
                "270,232,[n.],司法、法官\n" +
                "271,233,[n.],代表\n" +
                "272,233,[v.],委派\n" +
                "273,234,[n.],傳說、銘文\n" +
                "274,235,[n.],不和、不一致\n" +
                "275,236,[n.],回顧、追溯\n" +
                "276,236,[v.],回顧、追溯\n" +
                "277,237,[adj.],逼近的、即將發生的\n" +
                "278,238,[v.],提名、任命\n" +
                "279,239,[v.],彎曲、變調\n" +
                "280,240,[n.],古董\n" +
                "281,241,[v.],擦掉..的痕跡\n" +
                "282,242,[n.],衝擊、影響\n" +
                "283,242,[v.],衝擊、影響\n" +
                "284,243,[n.],事情、業務\n" +
                "285,244,[v.],旅行、航行\n" +
                "286,245,[n.],傳記\n" +
                "287,246,[n.],精密計時表、經線表\n" +
                "288,247,[adj.],可信的、貨真價實的\n" +
                "289,248,[v.],賄賂\n" +
                "290,249,[v.],阻礙\n" +
                "291,250,[n.],同意、贊成\n" +
                "292,250,[v.],同意、贊成\n" +
                "293,251,[n.],公共設施、基礎建設\n" +
                "294,252,[v.],保存、使回復\n" +
                "295,253,[n.],部下、晚輩\n" +
                "296,253,[adj.],較低的、較差的\n" +
                "297,254,[v.],聲明放棄\n" +
                "298,255,[v.],精製、純煉\n" +
                "299,256,[v.],讚美、欽佩\n" +
                "300,257,[n.],彎曲、曲折\n" +
                "301,258,[v.],暗示、意味\n" +
                "302,259,[v.],競爭、辯論、聲稱\n" +
                "303,260,[n.],查帳\n" +
                "304,261,[adj.],寬大的、表現品德高尚的\n" +
                "305,262,[n.],運輸、轉變\n" +
                "306,262,[v.],通過\n" +
                "307,263,[v.],疏忽、忽視\n" +
                "308,264,[n.],腐敗\n" +
                "309,265,[adj.],永久的、固定性的\n" +
                "310,266,[v.],指責、申斥\n" +
                "311,267,[n.],地產\n" +
                "312,268,[v.],變形、使醜\n" +
                "313,269,[adj.],冒險的\n" +
                "314,270,[adj.],自滿的、滿足的\n" +
                "315,271,[v.],延期、休會\n" +
                "316,272,[n.],抵押\n" +
                "317,273,[v.],放逐、驅逐\n" +
                "318,274,[n.],添加的事物\n" +
                "319,275,[n.],障礙、隔閡\n" +
                "320,276,[n.],殺蟲劑\n" +
                "321,277,[n.],救護車\n" +
                "322,278,[v.],使困窘、妨礙\n" +
                "323,279,[v.],放逐、貶謫\n" +
                "324,280,[v.],煽動、誘導\n" +
                "325,281,[n.],國會、議會\n" +
                "326,282,[n.],行人\n" +
                "327,282,[adj.],步行的\n" +
                "328,283,[n.],本能、天性\n" +
                "329,284,[n.],現代人\n" +
                "330,284,[adj.],當代的、同齡的\n" +
                "331,285,[n.],首都\n" +
                "332,285,[adj.],重要的、資金的\n" +
                "333,286,[n.],家長、族長 \n" +
                "334,287,[v.],同居\n" +
                "335,288,[n.],遠征(隊)\n" +
                "336,289,[n.],路線、旅程\n" +
                "337,290,[v.],宣佈、聲稱\n" +
                "338,291,[adj.],易碎的、纖細的\n" +
                "339,292,[v.],處理、配置\n" +
                "340,293,[v.],牽連、意味著\n" +
                "341,294,[n.],假名、筆名\n" +
                "342,295,[v.],指示、暗示\n" +
                "343,296,[v.],指責、彈劾\n" +
                "344,297,[adj.],足夠的\n" +
                "345,298,[v.],發光\n" +
                "346,299,[v.],步行、試探\n" +
                "347,300,[n.],兩棲動物學\n";

        String[] dataArray = data.split("\n");
        for (int i = 0; i < dataArray.length; i++) {
            String[] wordsArray = dataArray[i].split(",");
            int m_id = Integer.parseInt(wordsArray[0]);
            int w_id = Integer.parseInt(wordsArray[1]);
                Meaning meaningAdd = new Meaning(m_id,w_id, wordsArray[2], wordsArray[3]);
                insertMeaning(meaningAdd);

        }
    }

    public void sampleQuestion() {
        String data = "1,This price of equipment does not _____ to the official safety standards.,1\n" +
                "2,John was _____ to each friend with money.,2\n" +
                "3,How about a back rub to _____ some of your stress?,3\n" +
                "4,A highly _____ artistic， cinematic， or dramatic work always touches people.,4\n" +
                "5,They  sold the  furniture  by _____.,5\n" +
                "6,We _____ your insistence that the debt (should) be paid at once.,6\n" +
                "7,They often _____ us to work twelve or fourteen hours a day.,7\n" +
                "8,The pill will _____ you from pain.,8\n" +
                "9,The police absolved the _____.,9\n" +
                "10,That dog has got a serious case of _____.,10\n" +
                "11,He _____ed the importance of the event.,11\n" +
                "12,Measure the _____ of this circle.,12\n" +
                "13,His hair is beginning to _____ from his forehead.,13\n" +
                "14,I won't have him _____ing to me.,14\n" +
                "15,He was _____ed to the position of president.,15\n" +
                "16,They _____ed those ships.,16\n" +
                "17,Please _____ your opinion about this topic.,17\n" +
                "18,You seem to _____ her explanation.,18\n" +
                "19,\"Sad\" and \"unhappy\" are _____s.,19\n" +
                "20,The teacher _____s Arabic words into English letters.,20\n" +
                "21,The whole essay is consisted of six parts， including introduction and _____.,21\n" +
                "22,Chairman of the committee was _____ed by ballot.,22\n" +
                "23,The kite is a tetragon with one line of _____. ,23\n" +
                "24,The fish stayed _____ in water when sleeping.,24\n" +
                "25,Morris ignored the question and continued his _____.,25\n" +
                "26,He passed his German _____ exam.,26\n" +
                "27,The facts _____ his theory.,27\n" +
                "28,There is ample reason to believe that the man is _____.,28\n" +
                "29,We felt quite _____ to resist the will of the dictator.,29\n" +
                "30,Professor Smith is recognized to be one of the great scholars in English _____.,30\n";

        String[] dataArray = data.split("\n");
        for (int i = 0; i < dataArray.length; i++) {
            String[] questionsArray = dataArray[i].split(",");
            int question_id = Integer.parseInt(questionsArray[0]);
            int answer_id = Integer.parseInt(questionsArray[2]);
            GameQuestion questionAdd = new GameQuestion(question_id, questionsArray[1], answer_id);
            insertQuestions(questionAdd);
        }
    }


    public void sampleRoot(){
        String rootData = "1,tain\n" +
                "2,bar\n" +
                "3,brev\n" +
                "4,tempor\n" +
                "5,tend\n" +
                "6,amphi\n" +
                "7,volv\n" +
                "8,fore\n" +
                "9,hyper\n" +
                "10,voke\n" +
                "11,spect\n" +
                "12,tinct\n" +
                "13,alti\n" +
                "14,ambul\n" +
                "15,arch\n" +
                "16,cap\n" +
                "17,ced\n" +
                "18,cide\n" +
                "19,cord\n" +
                "20,cur\n" +
                "21,dict\n" +
                "22,equ\n" +
                "23,fact\n" +
                "24,fin\n" +
                "25,flect\n" +
                "26,flu\n" +
                "27,form\n" +
                "28,frag\n" +
                "29,gen\n" +
                "30,grav\n" +
                "31,gress\n" +
                "32,habit\n" +
                "33,herb\n" +
                "34,hibit\n" +
                "35,ign\n" +
                "36,integr\n" +
                "37,it\n" +
                "38,ject\n" +
                "39,jud\n" +
                "40,junct\n" +
                "41,laps\n" +
                "42,leg\n" +
                "43,lev\n" +
                "44,liter\n" +
                "45,loc\n" +
                "46,log\n" +
                "47,luc\n" +
                "48,lumin\n" +
                "49,magn\n" +
                "50,main\n" +
                "51,meter\n" +
                "52,min\n" +
                "53,mir\n" +
                "54,mit\n" +
                "55,mon\n" +
                "56,mort\n" +
                "57,neg\n" +
                "58,noc\n" +
                "59,nomin\n" +
                "60,not\n" +
                "61,nounce\n" +
                "62,onym\n" +
                "63,ora\n" +
                "64,ord\n" +
                "65,pact\n" +
                "66,ped\n" +
                "67,pel\n" +
                "68,pend\n" +
                "69,plic\n" +
                "70,pose\n" +
                "71,pot\n" +
                "72,press\n" +
                "73,prehend\n" +
                "74,radic\n" +
                "75,rupt\n" +
                "76,sci\n" +
                "77,scribe\n" +
                "78,sess\n" +
                "79,sent\n";
        String[] rootDataArray = rootData.split("\n");
        for (int i = 0; i < rootDataArray.length; i++) {
            String[] rootArray = rootDataArray[i].split(",");
            int root_id = Integer.parseInt(rootArray[0]);
            Root rootAdd = new Root(root_id,rootArray[1]);
            insertRoot(rootAdd);
        }
    }
    public void sampleExp() {
        for (int i = 1; i <= 300; i++) {
            Exp expAdd = new Exp("user_test", i, 0, 0, 0, "");
            insertExp(expAdd);
        }
    }




}
