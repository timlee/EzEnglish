package wordsCard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.knowmemo.usermanagement.ButtonTransparent;
import com.knowmemo.usermanagement.MainActivity;
import com.knowmemo.usermanagement.MainChoiceActivity;
import com.knowmemo.usermanagement.R;

import java.util.List;
import java.util.Locale;

import sqlite.Exp;
import sqlite.Meaning;
import sqlite.SqlHelper;
import sqlite.Words;
import sqlite.tableDao;

public class ShowWordActivity extends ActionBarActivity {

    int count = 0;
    String user_id = "user_test";
    TextToSpeech textToSpeech;
    SqlHelper sqlHelper;
    List<Words> wordsReturnList;
    List<Meaning> meaningReturnList;
    List<Words> RootResult;
    List<Exp> expReturnList;
    Exp expReturn;
    tableDao tabledao;
    Toolbar toolbar;
    TextView wordsText;
    TextView meaningText;
    TextView level1;
    TextView level2;
    TextView level3;
    TextView level4;
    TextView level5;
    Button rememberBtn;
    Button forgetBtn;
    ImageView imageSpeaker;
    Button showRoot;
    TextView showWord;
    TextView showMeaning;
    boolean flaghaveword = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_word);
        this.layoutInit();
        this.listenerInit();
        this.databaseInit();
        //顯示每一個箱子內單字數量
        setBoxCount();

    }

    //初始化所有的框架
    private void layoutInit() {

        wordsText = (TextView) findViewById(R.id.wordsText);
        meaningText = (TextView) findViewById(R.id.meaningText);
        level1 = (TextView) findViewById(R.id.level1);
        level2 = (TextView) findViewById(R.id.level2);
        level3 = (TextView) findViewById(R.id.level3);
        level4 = (TextView) findViewById(R.id.level4);
        level5 = (TextView) findViewById(R.id.level5);
        rememberBtn = (Button) findViewById(R.id.rememberBtn);
        rememberBtn.setOnTouchListener(new ButtonTransparent());
        forgetBtn = (Button) findViewById(R.id.forgetBtn);
        forgetBtn.setOnTouchListener(new ButtonTransparent());
        imageSpeaker = (ImageView)findViewById(R.id.imageSpeaker);
        showRoot = (Button) findViewById(R.id.showRoot);
        showWord = (TextView)findViewById(R.id.showWord);
        showMeaning = (TextView)findViewById(R.id.showMeaning);
        //初始化TextToSpeech 定義語言是US
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void databaseInit(){
        // 建立資料庫物件及資料
        tabledao = new tableDao(getApplicationContext());
        if (tabledao.getExpCount() == 0){
            tabledao.deleteWords();
            tabledao.sampleWord();
            tabledao.deleteMeaning();
            tabledao.sampleMeaning();
            tabledao.deleteExp();
            tabledao.sampleRoot();
        }
        //取10個單字加入levle1的箱子
        wordsReturnList = tabledao.top10Words(0);
        //顯示單字
        wordsText.setText(wordsReturnList.get(count).getWord());
    }

    //button click後的動作
    private void listenerInit() {

        //點擊記得
        rememberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int expCount = tabledao.getExpCount();
                System.out.println("flaghaveword ="+flaghaveword);
                if((flaghaveword==false)&(tabledao.getWordsCount()> expCount)){
                    wordsReturnList=tabledao.top10Words(expCount);
                    count = 0 ;
                    flaghaveword =true;

                }
                //取得目前單字的id
                int wordId = wordsReturnList.get(count).getW_id();
                //利用id去找該id的exp
                expReturn = tabledao.getExpById(wordId);
                int level = expReturn.getLevel();
                int learnedTimes = expReturn.getLearned();
                int levelCount = tabledao.getBoxCount(level + 1);

                //如果本來就在第五層，則level改成6後updateExp
                if (level == 5) {
                    Exp exp = new Exp(user_id, wordId, 6, 0, learnedTimes + 1, "");
                    tabledao.updateExp(exp);
                    count ++;
                } else { //如果在一到四層，要判斷要前往的箱子是否會滿
                    if (tabledao.checkBoxCount(level + 1)) { //要前往的箱子 加上這一個單字後還沒滿
                        Exp exp = new Exp(user_id, wordId, level + 1, levelCount + 1, learnedTimes + 1, "");
                        tabledao.updateExp(exp);
                        count++;
                    } else { //要前往的箱子 加上這一個單字後滿了
                        Exp exp = new Exp(user_id, wordId, level + 1, levelCount + 1, learnedTimes + 1, "");
                        tabledao.updateExp(exp);
                        expReturnList = tabledao.boxLevelData(level + 1);
                        wordsReturnList = tabledao.getWordsById(expReturnList);
                        count = 0;
                    }
                }
                //經過上述處理後判斷count是否等於目前的單字數
                // 等於的話，count歸零，再取得沒記過的10個單字
                if (count == wordsReturnList.size()) {
                    System.out.println("enter check");
                    int max = tabledao.getExpMaxWordId();
                    System.out.println(max);

                    if (tabledao.getBoxCount(1) == 0) {

                        if(tabledao.have10Word(max)){  //判斷word裡是否還有單字

                            System.out.println("nothave10="+tabledao.have10Word(max));
                            flaghaveword = false;
                            ConfirmExit();
                        }
                        else {
                            wordsReturnList = tabledao.top10Words(max);
                            count = 0;
                        }

                    }else {
                        expReturnList = tabledao.boxLevelData(1);
                        wordsReturnList = tabledao.getWordsById(expReturnList);
                        count = 0;
                    }
                }

                if(((flaghaveword)!= false)) {
                    setBoxCount();
                    wordsText.setText(wordsReturnList.get(count).getWord());
                    System.out.println(tabledao.getExpCount());
                    meaningText.setText("點擊以顯示解釋");
                    showWord.setText("");
                    showMeaning.setText("");
                }


            }
        });

        //點擊忘記
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int wordId = wordsReturnList.get(count).getW_id();
                expReturn = tabledao.getExpById(wordId);
                int level = expReturn.getLevel();
                int learnedTimes = expReturn.getLearned();
                int levelCount = tabledao.getBoxCount(level - 1);

                if (level == 1) {
                    int level1Count = tabledao.getBoxCount(1);
                    Exp exp = new Exp(user_id, wordId, level, level1Count, learnedTimes + 1, "");
                    tabledao.updateExp(exp);
                    count++;

                } else {
                    if (tabledao.checkBoxCount(level - 1)) { //要前往的箱子 加上這一個單字後還沒滿
                        Exp exp = new Exp(user_id, wordId, level - 1, levelCount + 1, learnedTimes + 1, "");
                        tabledao.updateExp(exp);
                        count++;
                    } else { //要前往的箱子 加上這一個單字後滿了
                        Exp exp = new Exp(user_id, wordId, level - 1, levelCount + 1, learnedTimes + 1, "");
                        tabledao.updateExp(exp);
                        expReturnList = tabledao.boxLevelData(level - 1);
                        wordsReturnList = tabledao.getWordsById(expReturnList);
                        count = 0;
                    }
                }
                if (count == wordsReturnList.size()) {
                    if (tabledao.getBoxCount(1) == 0) {
                        int max = tabledao.getExpMaxWordId();
                        wordsReturnList = tabledao.top10Words(max);
                        count = 0;
                    } else {
                        expReturnList = tabledao.boxLevelData(1);
                        wordsReturnList = tabledao.getWordsById(expReturnList);
                        count = 0;
                    }

                }
                setBoxCount();
                wordsText.setText(wordsReturnList.get(count).getWord());
                meaningText.setText("點擊以顯示解釋");
                showWord.setText("");
                showMeaning.setText("");

            }
        });

        //點擊顯示解釋
        meaningText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得現在那個單字的id
                int searchId = wordsReturnList.get(count).getW_id();
                //由id去找所有的單字
                meaningReturnList = tabledao.getMeaningById(searchId);
                String meaningResult = "";
                for (int i = 0; i < meaningReturnList.size(); i++) {
                    //目前只顯示詞性和中文解釋，之後可再新增其他meaning欄位
                    meaningResult += meaningReturnList.get(i).getPart_of_speech() + " "
                            + meaningReturnList.get(i).getEngChiTra() + "  ";
                }
                meaningText.setText(meaningResult);
            }
        });
        //點擊顯示同字根字首單字
        showRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //找到目前單字ID的r_id
                int searchRootId = wordsReturnList.get(count).getR_id();
                String rootWordResult = "";
                String meaningResult = "";
                if(searchRootId != 0)
                {
                    //找出所有同root_id的word_id,word,root_id
                    RootResult = tabledao.getWordByRootId(searchRootId);
                    for (int i = 0; i < RootResult.size(); i++) {
                        //取出同字根的單字放在rootWordResult
                        rootWordResult += RootResult.get(i).getWord() + "\n";
                        int rootWordId = RootResult.get(i).getW_id();
                        meaningReturnList = tabledao.getMeaningById(rootWordId);
                        for (int j = 0; j < meaningReturnList.size(); j++) {
                            meaningResult += meaningReturnList.get(j).getPart_of_speech() + " "
                                    + meaningReturnList.get(j).getEngChiTra() + "\n";
                        }
                    }

                }

                showWord.setText(rootWordResult);
                showMeaning.setText(meaningResult);
            }
        });

        //點擊發聲
        imageSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = wordsText.getText().toString();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    private void openDatabase() {
        sqlHelper = new SqlHelper(this);
    }

    //顯示各個箱子內的單字數
    private void setBoxCount() {
        level1.setText(Integer.toString(tabledao.getBoxCount(1)));
        level2.setText(Integer.toString(tabledao.getBoxCount(2)));
        level3.setText(Integer.toString(tabledao.getBoxCount(3)));
        level4.setText(Integer.toString(tabledao.getBoxCount(4)));
        level5.setText(Integer.toString(tabledao.getBoxCount(5)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu.add(0, 0, 0, "新增單字");
//        menu.add(0, 1, 1, "登出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //按下返回鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(wordsCard.ShowWordActivity.this, com.knowmemo.usermanagement.MainChoiceActivity.class);
            startActivity(intent);
            //ConfirmExit();//按返回鍵，則執行退出確認
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("學習完");
        ad.setMessage("若要繼續學習請新增單字! 將離開此畫面...");
        ad.setPositiveButton("離開", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {

                Intent intent = new Intent(wordsCard.ShowWordActivity.this, com.knowmemo.usermanagement.MainChoiceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("flag",flaghaveword);
                //將Bundle物件assign給intent
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ad.show();//示對話框
    }

}