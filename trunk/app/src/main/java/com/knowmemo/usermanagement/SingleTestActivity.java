package com.knowmemo.usermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;

import sqlite.Words;
import sqlite.tableDao;
import sqlite.GameQuestion;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class SingleTestActivity extends AppCompatActivity{
    tableDao tabledao;
    GameQuestion currentQ = null;
    protected int correct = 0;
    TextView question;
    TextView timer;
    TextView record;
    Button btn_ans1;
    Button btn_ans2;
    Button btn_ans3;
    Button btn_ans4;
    Button btn_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_test);

        create();
    }

    protected void create() {
        this.layoutInit();
        this.databaseInit();
        this.timeCountDown();
        this.listenerInit();
        this.insertQuestion();
        this.insertAnswer();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(SingleTestActivity.this, GameHomeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void layoutInit() {
        question = (TextView) findViewById(R.id.single_question);
        timer = (TextView) findViewById(R.id.single_test_timer);
        record = (TextView) findViewById(R.id.right_answer);
        btn_ans1 = (Button) findViewById(R.id.btn_ans1);
        btn_ans2 = (Button) findViewById(R.id.btn_ans2);
        btn_ans3 = (Button) findViewById(R.id.btn_ans3);
        btn_ans4 = (Button) findViewById(R.id.btn_ans4);
        btn_skip = (Button) findViewById(R.id.btn_single_skip);
    }

    protected void databaseInit() {
        tabledao = new tableDao(getApplicationContext());
        if (tabledao.getWordsCount() == 0) {
            tabledao.sampleWord();
        }
        if (tabledao.getGameQuestionCount() == 0) {
            tabledao.sampleQuestion();
        }
    }

    protected void listenerInit() {
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化
                currentQ = null;
                //置入下一題
                insertQuestion();
                //置入答案
                insertAnswer();
            }
        });

        btn_ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClick(btn_ans1.getText().toString());
            }
        });

        btn_ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClick(btn_ans2.getText().toString());
            }
        });

        btn_ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClick(btn_ans3.getText().toString());
            }
        });

        btn_ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClick(btn_ans4.getText().toString());
            }
        });
    }

    private void timeCountDown() {
        new CountDownTimer(200000,1000){

            @Override
            public void onFinish() {
                insertRecord();
                finish();
                setContentView(R.layout.single_end_game);
                Intent intent = new Intent(SingleTestActivity.this, SingleEndGameActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished/1000);
            }
        }.start();
    }

    protected void insertQuestion() {
        int questionCnt = tabledao.getGameQuestionCount();
        long seed = System.nanoTime();
        Random random = new Random(seed);
        int q_id= random.nextInt(questionCnt) + 1;
        currentQ = tabledao.getQuestionById(q_id);
        question.setText(currentQ.getQuestion());
    }

    protected void insertAnswer() {
        String[] answerList = new String[4];
        //初始化答案陣列
        for (int i = 0; i < answerList.length; i++) {
            answerList[i] = null;
        }
        int right_ans_id = currentQ.getAnswer_id();
        String right_answer = tabledao.getWordById(right_ans_id).getWord();
        long seed = System.nanoTime();
        Random random = new Random(seed);
        int right_ans_index = random.nextInt(4);
        //先填入正確答案id的按鈕位置
        answerList[right_ans_index] = right_answer;
        //產生其他答案
        int[] otherAnswers = pickOtherAnswers(right_ans_id);
        for (int i = 0, j = 0; i < 4 && j < otherAnswers.length; i++) {
            if (answerList[i] == null && otherAnswers[j] != right_ans_id) {
                answerList[i] = tabledao.getWordById(otherAnswers[j]).getWord().toString();
                j++;
            }
        }

        btn_ans1.setText(answerList[0]);
        btn_ans2.setText(answerList[1]);
        btn_ans3.setText(answerList[2]);
        btn_ans4.setText(answerList[3]);
    }

    private int[] pickOtherAnswers(int right_ans_id) {
        int[] otherAnswerList = new int[3];
        long seed = System.nanoTime();
        HashSet set = new HashSet<Integer>(3);
        set.add(right_ans_id);
        for (int i = 0; i < otherAnswerList.length; i++) {
            Random random = new Random(seed);
            int tmp = random.nextInt(tabledao.getWordsCount()) + 1;
            while (!set.add(tmp)) {
                tmp = random.nextInt(tabledao.getWordsCount()) + 1;
            }
            otherAnswerList[i] = tmp;
        }
        return otherAnswerList;
    }

    protected boolean checkAnswer(String chosenAnswer) {
        boolean isCorrect = false;
        String rightAnswer = tabledao.getWordById(currentQ.getAnswer_id()).getWord().toString();
        if (chosenAnswer != null && chosenAnswer.toLowerCase().equals(rightAnswer)) {
            isCorrect = true;
        } else {
            isCorrect = false;
        }
        return isCorrect;
    }

    protected void btnClick(String btnText) {
        if (checkAnswer(btnText.toLowerCase())) {
            correct++;
            record.setText(correct + "");
            insertQuestion();
            insertAnswer();
        } else {
            insertQuestion();
            insertAnswer();
        }
    }

    private void insertRecord() {
        int r_id = tabledao.getGameRecordCount() + 1;
        int record = correct;
        String time = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        sqlite.GameRecord addRecord = new sqlite.GameRecord(r_id, record, time);
        tabledao.insertGameRecord(addRecord);
    }
}