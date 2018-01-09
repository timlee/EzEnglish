package com.knowmemo.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import java.util.ArrayList;
import java.util.HashMap;

import sqlite.tableDao;

public class SingleRankActivity extends AppCompatActivity {
    ListView game_record_listView;
    tableDao tabledao;
    ArrayList<HashMap<String, Object>> top10Record;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_rank);

        tabledao = new tableDao(getApplicationContext());
        game_record_listView = (ListView) findViewById(R.id.game_record_list);
        top10Record = tabledao.getTop10GameRecord();

        this.insertRecord();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent();
            myIntent = new Intent(SingleRankActivity.this, GameHomeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void insertRecord() {
        simpleAdapter = new SimpleAdapter(this,
                top10Record, R.layout.game_record_item_layout, new String[]{"排名", "答題數", "日期"},
                new int[]{R.id.col_num, R.id.col_grade, R.id.col_date});
        game_record_listView.setAdapter(simpleAdapter);
    }
}
