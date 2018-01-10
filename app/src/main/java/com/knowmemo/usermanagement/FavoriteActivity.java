package com.knowmemo.usermanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import sqlite.Meaning;
import sqlite.tableDao;

/**
 * Created by TingEn on 2016/11/17.
 */
public class FavoriteActivity extends Activity {
    ListView lv;
    tableDao tabledao;
    ArrayList<HashMap<String, Object>> favorReturnList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfavorite);
        this.loadInit();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(FavoriteActivity.this, MainChoiceActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void loadInit() {
        tabledao = new tableDao(getApplicationContext());
        lv = (ListView) findViewById(R.id.favorInfo);
        favorReturnList = tabledao.getFavoritesWords();

        FavorviewAdapter Btnadapter = new FavorviewAdapter(
                this,
                favorReturnList,
                R.layout.favoritem_layout,
                new String[] {"單字","解釋", "刪除按鈕","新增按鈕"},
                new int[] {R.id.worditem,R.id.meanitem,R.id.btndelete,R.id.btnadd}
        );
        lv.setAdapter(Btnadapter);
    }
    //        SimpleAdapter adapter = new SimpleAdapter(this, favorReturnList, R.layout.favoritem_layout, new String[]{"單字", "解釋"}, new int[]{R.id.worditem, R.id.meanitem});
    ////        lv.setAdapter(new CustomAdapterArrayAdapter(this,favorReturnList));
    //        lv.setAdapter(adapter);

    public class FavorviewAdapter extends BaseAdapter {

        private ArrayList<HashMap<String, Object>> mAppList;
        private LayoutInflater mInflater;
        private Context mContext;
        private String[] keyString;
        private int[] valueViewID;

        private ItemView itemView;

        private class ItemView {
            TextView ItemWord;
            TextView ItemInfo;
            ImageButton ItemDeButton;
            ImageButton ItemAddButton;

        }

        public FavorviewAdapter(Context c, ArrayList<HashMap<String, Object>> appList, int resource, String[] from, int[] to) {
            mAppList = appList;
            mContext = c;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            keyString = new String[from.length];
            valueViewID = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, valueViewID, 0, to.length);

        }

        @Override
        public int getCount() {

            //return 0;
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {

            //return null;
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {

            //return 0;
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //return null;

            if (convertView != null) {
                itemView = (ItemView) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.favoritem_layout, null);
                itemView = new ItemView();

                itemView.ItemWord = (TextView) convertView.findViewById(valueViewID[0]);
                itemView.ItemInfo = (TextView) convertView.findViewById(valueViewID[1]);
                itemView.ItemDeButton = (ImageButton) convertView.findViewById(valueViewID[2]);
                itemView.ItemAddButton = (ImageButton) convertView.findViewById(valueViewID[3]);
                convertView.setTag(itemView);
            }

            HashMap<String, Object> appInfo = mAppList.get(position);
            if (appInfo != null) {
                String name = (String) appInfo.get(keyString[0]);
                String info = (String) appInfo.get(keyString[1]);
                int bid = (Integer) appInfo.get(keyString[2]);
                itemView.ItemWord.setText(name);
                itemView.ItemInfo.setText(info);
                itemView.ItemDeButton.setOnClickListener(new ItemDeButton_Click(position));
                itemView.ItemAddButton.setOnClickListener(new ItemAddButton_Click(position));
                itemView.ItemInfo.setOnLongClickListener(new LongClick(position));


            }

            return convertView;
        }

        class LongClick implements View.OnLongClickListener {
            private int position;
            tableDao tabledao;

            LongClick(int pos) {
                position = pos;

            }

            @Override
            public boolean onLongClick(View view) {
                final View item = LayoutInflater.from(FavoriteActivity.this).inflate(R.layout.edit_layout, null);
                new AlertDialog.Builder(FavoriteActivity.this)
                        .setTitle("請輸入:")
                        .setView(item)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item.findViewById(R.id.editmeaning);
                                String edittext = editText.getText().toString();
                                if (edittext != "") {
                                    HashMap<String, Object> appInfo = mAppList.get(position);
                                    appInfo.put(keyString[1], edittext);
                                    notifyDataSetChanged();

                                }

                            }
                        })
                        .show();
                return true;

            }

        }

        class ItemDeButton_Click implements View.OnClickListener {
            private int position;
            tableDao tabledao;

            ItemDeButton_Click(int pos) {
                position = pos;

            }

            @Override
            public void onClick(View v) {
                tabledao = new tableDao(getApplicationContext());
                HashMap<String, Object> appInfo = mAppList.get(position);
                String name = (String) appInfo.get(keyString[0]);
                System.out.println(name);
                tabledao.deleteFavorItem(name);
                mAppList.remove(position);
                notifyDataSetChanged();


            }

        }

        class ItemAddButton_Click implements View.OnClickListener {
            private int position;
            tableDao tabledao;

            ItemAddButton_Click(int pos) {
                position = pos;

            }

            @Override
            public void onClick(View v) {
                tabledao = new tableDao(getApplicationContext());
                HashMap<String, Object> appInfo = mAppList.get(position);
                String name = (String) appInfo.get(keyString[0]);
                String info = (String) appInfo.get(keyString[1]);
                System.out.println(name);


                if( tabledao.getWordsByWord(name) ==false) {
                    sqlite.Words wordsAdd = new sqlite.Words(tabledao.getWordsCount() + 1, name, 0);
                    tabledao.insertWords(wordsAdd);
                    Meaning meaningAdd = new Meaning(tabledao.getMeaningCount() + 1, tabledao.getMeaningwordCount() + 1, "", info);
                    tabledao.insertMeaning(meaningAdd);
                    String temp = Integer.toString(tabledao.getWordsCount());
                    Toast toast = Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    Toast toast3 = Toast.makeText(getApplicationContext(),"無法加入!因單字已存在學習卡片箱", Toast.LENGTH_LONG);
                    toast3.show();
                }

            }

        }
    }





}
