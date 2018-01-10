package com.knowmemo.usermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class SingleReadyActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_ready);

        Button go_single_test = (Button) findViewById(R.id.btn_go);

        go_single_test.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                jumpToSingleTest();
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(SingleReadyActivity.this, GameHomeActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void jumpToSingleTest(){

        setContentView(R.layout.single_test);
        Intent intent = new Intent(SingleReadyActivity.this, SingleTestActivity.class);
        startActivity(intent);

    }
}