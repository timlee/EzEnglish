package com.knowmemo.usermanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import sqlite.tableDao;
import sqlite.SqlHelper;
import sqlite.Words;
import com.spreada.utils.chinese.ZHConverter;
/**
 * Created by TingEn on 2016/10/11.
 */
public class SearchByhandActivity extends Activity {
    int countCursor = 0 ;
    tableDao tabledao;
    String inputfind = "";
    String Interpret = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbyhand);
        Button btn_Search = (Button) findViewById(R.id.buttonSearch);
        Button button_Add = (Button) findViewById(R.id.buttonAdd);


        btn_Search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {

                new Thread(new Runnable()
                {
                    EditText et = (EditText) findViewById(R.id.editText);
                    TextView output = (TextView) findViewById(R.id.enginfo);

                    public void run()
                    {
                        try
                        {
                            String input = et.getText().toString();
                            inputfind = input.toString().toLowerCase();
                            String url = "https://glosbe.com/gapi/translate?from=eng&dest=zh&format=json&phrase="+inputfind+"&pretty=true" ;
                            HttpClient mHttpClient = new DefaultHttpClient();
                            HttpGet mHttpGet = new HttpGet(url);
                            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);

                            if(mHttpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK )
                            {
                                String mJsonText = EntityUtils.toString(mHttpResponse.getEntity());


                                countCursor++;
                                if(countCursor!=1){
                                    Interpret = "";
                                }
                               A: for (int i=0;i<3;i++)
                                {
                                    JSONArray jsonarray =new JSONArray(new JSONObject(mJsonText).getString("tuc"));
                                    if(jsonarray.length()!=0){
                                        String JsonString =new JSONObject( jsonarray.getJSONObject(i).getString("phrase")).getString("text");

                                        if( i==2){
                                            Interpret+=JsonString;
                                        }else{
                                            Interpret+=JsonString+"、";
                                        }
                                    }
                                    else{
                                        break A;
                                    }

                                }
                                if(Interpret!=""){
                                    ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
                                    String zhResault = converter.convert(Interpret, ZHConverter.TRADITIONAL);
                                    Interpret = zhResault;
                                    output.setText("中文解釋：\n"+ zhResault);
                                }else{
                                    output.setText(" NOT found");
                                    Toast toast2 = Toast.makeText(getApplicationContext(),"Oops!", Toast.LENGTH_LONG);
                                    toast2.show();
                                }

                            }
                            else
                                output.setText("wrong");
                        }
                        catch(Exception e)
                        {
                        }
                    }
                }).start();
            }});
        button_Add.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {


                tabledao = new tableDao(getApplicationContext());

                if (tabledao.getFavorsCount() == 0){

                    tabledao.insertFavorites(inputfind, Interpret);
                }else
                {
                    if(Interpret == ""){
                        Toast toast4 = Toast.makeText(getApplicationContext(),"沒有搜尋結果", Toast.LENGTH_LONG);
                        toast4.show();
                    }else if(tabledao.getFavoritesbyWord(inputfind)){
                        Toast toast3 = Toast.makeText(getApplicationContext(),"無法加入!此單字已存在", Toast.LENGTH_LONG);
                        toast3.show();
                    }else{

                        tabledao.insertFavorites(inputfind, Interpret);

                        Toast toast = Toast.makeText(getApplicationContext(),"已新增!", Toast.LENGTH_LONG);
                        toast.show();



                    }

                }
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent ;
            myIntent = new Intent(SearchByhandActivity.this, MainChoiceActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}




