package com.knowmemo.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class AlarmSingleTestActivity extends SingleTestActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_test);

        create();
    }

    @Override
    protected void create() {
        super.layoutInit();
        super.databaseInit();
        super.listenerInit();
        super.insertQuestion();
        super.insertAnswer();
        timer.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(AlarmSingleTestActivity.this, GameHomeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    protected void btnClick(String btnText) {
        if (checkAnswer(btnText.toLowerCase())) {
            correct++;
            record.setText(correct + "");
            if (correct < 3) {
                insertQuestion();
                insertAnswer();
            } else {
                finish();
            }
        } else {
            insertQuestion();
            insertAnswer();
        }
    }
}