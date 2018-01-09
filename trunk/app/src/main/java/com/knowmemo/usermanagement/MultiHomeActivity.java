package com.knowmemo.usermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MultiHomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_home);

        Button btn_random = (Button) findViewById(R.id.btn_random);
        Button btn_choose_rival = (Button) findViewById(R.id.btn_choose_rival);

        btn_random.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                jumpToMultiRandom();
            }

        });

        btn_choose_rival.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                jumpToMultiChooseRival();
            }

        });
    }

    public void jumpToMultiRandom(){

        setContentView(R.layout.multi_connecting);
        Intent intent = new Intent(MultiHomeActivity.this, MultiConnectActivity.class);
        startActivity(intent);

    }

    public void jumpToMultiChooseRival(){

        setContentView(R.layout.multi_insert_id);
        Intent intent = new Intent(MultiHomeActivity.this, MultiChooseRivalActivity.class);
        startActivity(intent);

    }
}