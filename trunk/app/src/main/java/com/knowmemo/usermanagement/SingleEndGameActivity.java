package com.knowmemo.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;

import sqlite.GameQuestion;
import sqlite.tableDao;

public class SingleEndGameActivity extends AppCompatActivity{
    Button btn_check;
    TextView grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_end_game);

        btn_check = (Button) findViewById(R.id.btn_check);
        grade = (TextView) findViewById(R.id.text_grade);

        insertGrade();
        btn_check.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {
                jumpToRank();
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(SingleEndGameActivity.this, GameHomeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void jumpToRank() {
        setContentView(R.layout.single_rank);
        Intent intent = new Intent(SingleEndGameActivity.this, SingleRankActivity.class);
        startActivity(intent);
    }

    private void insertGrade() {
        tableDao tabledao = new tableDao(getApplicationContext());
        int record = tabledao.getLastGameRecord().getRecord();
        grade.setText(record + "");
    }
}