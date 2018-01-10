package com.knowmemo.usermanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GameHomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);

        Button btn_single = (Button) findViewById(R.id.btn_single);
        //Button btn_multi = (Button) findViewById(R.id.btn_multi);
        Button btn_rank = (Button) findViewById(R.id.btn_rank);

        btn_single.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                jumpToSingle();
            }

        });

        /*btn_multi.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {

                jumpToMultiHome();
            }

        });*/

        btn_rank.setOnClickListener(new Button.OnClickListener(){

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
            myIntent = new Intent(GameHomeActivity.this, MainChoiceActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void jumpToSingle(){

        setContentView(R.layout.single_ready);
        Intent intent = new Intent(GameHomeActivity.this, SingleReadyActivity.class);
        startActivity(intent);

    }

    public void jumpToMultiHome(){

        setContentView(R.layout.multi_home);
        Intent intent = new Intent(GameHomeActivity.this, SingleReadyActivity.class);
        startActivity(intent);

    }

    public void jumpToRank(){

        setContentView(R.layout.single_rank);
        Intent intent = new Intent(GameHomeActivity.this, SingleRankActivity.class);
        startActivity(intent);
    }
}
