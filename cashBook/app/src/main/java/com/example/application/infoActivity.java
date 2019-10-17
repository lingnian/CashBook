package com.example.application;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class infoActivity extends AppCompatActivity {
    MyDatabaseHelper dataHelper;
    SQLiteDatabase db;
    int id;
    //for Spinner Adapter
    ArrayAdapter<String> adapter;
    ArrayList<String> mateNames = new ArrayList<String>();

    TextView txtName;
    Button btnDel,btnSubmit;
    EditText brief,time,record,money;
    Spinner spinner;
    RadioGroup type;
    RadioButton radioButtonf,radioButtont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //与main传递信息 name
        txtName = (TextView) findViewById(R.id.txtViewname);
        Intent intent = getIntent();
        txtName.setText(intent.getStringExtra("name")+":");
        txtName.setTextColor(Color.RED);
        //实例化数据库  创建Table
        dataHelper = new MyDatabaseHelper(this);
        db = dataHelper.getWritableDatabase();
        //init
        spinner = (Spinner)findViewById(R.id.spinner);
        type = (RadioGroup)findViewById(R.id.radioGroup);
        radioButtonf = (RadioButton)findViewById(R.id.radioButtonf);
        radioButtont = (RadioButton)findViewById(R.id.radioButtont);
        btnDel = (Button)findViewById(R.id.btnDel);
        btnSubmit = (Button)findViewById(R.id.btnSub);
        brief = (EditText)findViewById(R.id.txtBrief);
        time = (EditText)findViewById(R.id.txtDate);
        money = (EditText)findViewById(R.id.txtMoney);
        record = (EditText)findViewById(R.id.txtRecord);
        /*初始化时间 显示到time
        * */
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);


        time.setText(+year+"."+month+"."+day+"  "+hour+":"+minute);
        /* 初始化Spinner--------------------------------------------------------*/
        //for Spinner item
        spinner =(Spinner)findViewById(R.id.spinner);
        //init one adapter from mateName
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mateNames);
        //for AlertDialog ,one Layout
        //显示到spinner
        int length = (int)SharedPreferencesClass.get(getApplicationContext(),"length",0);
        //循环添加mateNames,通过Length 来控制循环
        for (int i=1;i<length;i++) {
            String string1 = (String) SharedPreferencesClass.get(getApplicationContext(), String.valueOf(i), "");
            if (!string1.equals("")) {
                mateNames.add(string1);
            }
        }
        spinner.setAdapter(adapter);
//--------------------------------------------------------------------

        //设置默认样式

        type.check(R.id.radioButtonf);
        if (intent.getStringExtra("btn").equals("New"))
        {
            btnDel.setVisibility(View.GONE);
            //btnSubmit.setGravity(center_horizontal);

        }
        else if (intent.getStringExtra("btn").equals("Item")){
            btnDel.setVisibility(View.VISIBLE);
            //btnSubmit.setText("Modified");
            btnSubmit.setVisibility(View.INVISIBLE);
            clickText();
        }

        //Button Onclick
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Click!",Toast.LENGTH_SHORT).show();
                dialog();
            }
        });
    }
    //Confirm dialog
     public void dialog(){
         AlertDialog.Builder dialog = new AlertDialog.Builder(this);
         dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 db.execSQL("delete from record where ID = ?",new String[]{String.valueOf(id)});
                 Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                 Intent i = new Intent(infoActivity.this,MainActivity.class);
                 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(i);
                 finish();
             }
         });
         dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
             }
         });
        dialog.setMessage("Are u sure delete this record？");
         dialog.show();

     }
    //submit Button onclick
    public void submit (View view){

            String name = txtName.getText().toString();
            name = name.substring(0, name.length() - 1);
            String stime = time.getText().toString();
            String sbrief = brief.getText().toString();
            String srecord = record.getText().toString();
            RadioButton click = (RadioButton) findViewById(type.getCheckedRadioButtonId());
            String stype = click.getText().toString();
            String smoney = money.getText().toString();
            String sspinner = spinner.getSelectedItem().toString();
            if (sspinner.equals(name)) {
                Toast.makeText(this, "Please select different name!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sbrief.equals("") || srecord.equals("") ||smoney.equals("")) {
                Toast.makeText(this, "Please fill out all forms !", Toast.LENGTH_SHORT).show();
                return;
            }

//        Log.d("name",name);
//        Log.d("stime",stime);
//        Log.d("sbrief",sbrief);
//        Log.d("srecord",srecord);
//        Log.d("stype",stype);
//        Log.d("smoney",smoney);
//        Log.d("sspinner",sspinner);
            //save to DAtaBase
        /*   "name text," +
                    "Brief text," +
                    "date text," +
                    "type text," +
                    "objname text," +
                    "money int," +
                    "record text)";
         */
           else {
                ContentValues cv = new ContentValues();
                cv.put("name", name);
                cv.put("Brief", sbrief);
                cv.put("date", stime);
                cv.put("type", stype);
                cv.put("money", smoney);
                cv.put("objname", sspinner);
                cv.put("record", srecord);
                db.insert("record", null, cv);
                Toast.makeText(this, "Save successful!", Toast.LENGTH_LONG).show();
                db.close();

                Intent i = new Intent(infoActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }

    }

    public void clickText(){
        Intent i = getIntent();
        id = i.getIntExtra("ID",0);
        String[] idString = new String[]{String.valueOf(id)};
        //txtName.setText(idString[0]);
        //Toast.makeText(getApplicationContext(),"Detail", Toast.LENGTH_SHORT).show();
        Cursor cursor = db.rawQuery("select * from record where ID = ?",idString);
        if(cursor.moveToFirst()) {
        txtName.setText(cursor.getString(cursor.getColumnIndex("name")));
        txtName.setTextColor(Color.GRAY);
        brief.setText(cursor.getString(cursor.getColumnIndex("Brief")));
        brief.setEnabled(false);
        brief.setTextColor(Color.GRAY);
        time.setText(cursor.getString(cursor.getColumnIndex("date")));
        time.setEnabled(false);
        time.setTextColor(Color.GRAY);
        record.setText("----"+cursor.getString(cursor.getColumnIndex("name"))+"  "
                +cursor.getString(cursor.getColumnIndex("type"))+"  "
                +cursor.getString(cursor.getColumnIndex("objname"))+" "
                +cursor.getInt(cursor.getColumnIndex("money"))+"-----\n"
                +cursor.getString(cursor.getColumnIndex("record")));
        record.setEnabled(false);
        record.setTextColor(Color.GRAY);
        money.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("money"))));
        money.setEnabled(false);
        money.setTextColor(Color.GRAY);
        spinner.setVisibility(View.GONE);
        type.setVisibility(View.GONE);
        }
        cursor.close();
    }


}
