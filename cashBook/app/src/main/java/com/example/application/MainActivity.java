package com.example.application;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  //  -----------------
       private Button btnDetail, btnNew,btnOk;
       private EditText txtName;
       private TextView record,logic;
       private Spinner spinner;
    //-----------------
       public ArrayAdapter<String> adapter;//spinner
       public ArrayList<String> mateNames = new ArrayList<String>();//adapter
       private  int[] names;//for SP to get value to delete matename
       private static int nameCount = 1;// for Sp to store values
       private static  int ij=0;// for matename
       private Boolean first=true;//first run
       private int count;//count the records
       private String item;//spinner item
//--------------------
       private SQLiteDatabase db;


    //private static Context ctx = getApplication();?



     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         record = (TextView)findViewById(R.id.txtRecord);
         logic = (TextView)findViewById(R.id.txtlogic);
         record.setMovementMethod(ScrollingMovementMethod.getInstance());
         logic.setMovementMethod(ScrollingMovementMethod.getInstance());
/* 初始化Spinner与点击事件--------------------------------------------------------*/
         //for init Spinner item
         spinner =(Spinner)findViewById(R.id.spinner);
        //init matenames
         names = new int[20];// store index of SP!!creazy will down!
         int length = (int)SharedPreferencesClass.get(getApplicationContext(),"length",0);
         if(length==0)
             mateNames.add("--Add your mates-->");
         //循环添加mateNames,通过Length 来控制循环
         else{
                 for (int i=1;i<length;i++) {
                     String string1 = (String) SharedPreferencesClass.get(getApplicationContext(), String.valueOf(i), "");
                     if (!string1.equals("")) {
                         mateNames.add(string1);
                         names[ij] = i;
                         ij++;
                     }
                 }
         }
         //init one adapter from mateName
             adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mateNames);
             spinner.setAdapter(adapter);

              clickItem();
             // LogicFunction();
              spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

             @Override
             public  void onItemSelected(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                        clickItem();
                        LogicFunction();

             }

             @Override
             public void onNothingSelected(AdapterView<?> arg0) {

             }
         });


 //-----------------------------------------------------------------------------------
    }
//spinner 点击事件
    public  void  addMateName(View v){

        final View layoutView = View.inflate(this, R.layout.dialoglayout, null);
        btnOk = (Button)layoutView.findViewById(R.id.btnOk);
        txtName = (EditText)layoutView.findViewById(R.id.txtDia);

        //create AlertDialog setView (VIEW)
        final AlertDialog builder = new AlertDialog.Builder(this).create();
              builder.setTitle("New");
              builder.setIcon(R.mipmap.add);
              builder.setCancelable(true);
              builder.setView(layoutView);
              builder.setMessage("------Add new mate------");
              builder.show();


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = txtName.getText().toString().trim();
                if(mateNames.get(0).equals("--Add your mates-->"))
                    mateNames.clear();
                if (s.equals(""))
                    builder.dismiss();
                else {
                    nameCount = (int)SharedPreferencesClass.get(getApplicationContext(),"length",nameCount);
                    if (mateNames.contains(s))
                           builder.dismiss();
                    else {
                        SharedPreferencesClass.put(getApplicationContext(), String.valueOf(nameCount), s);
                        nameCount++;
                        SharedPreferencesClass.put(getApplicationContext(), "length", nameCount);//length -1

                        mateNames.add(s);
                        //spinner.setAdapter(adapter);
                        //((BaseAdapter)spinner.getAdapter()).notifyDataSetChanged();
                        int pos = mateNames.indexOf(s);
                        spinner.setSelection(pos, true);//spinner问题出在这，第一次加入不返回位置？当pos为0 有小bug 不过挺好的
//                    int k= spinner.getCount();
//                    for(int i=0;i<k;i++){
//                        if(s.equals(adapter.getItem(i).toString())){
//                            spinner.setSelection(i,true);
//                            break;
//                        }
//                    }
                        builder.dismiss();
                    }
                }
            }
        });

}
   //New Record Onclick NEW()
    public void New(View v){
        //spinner.removeAllViewsInLayout();
        int length = (int)SharedPreferencesClass.get(getApplicationContext(),"length",0);
        if (length<3)
        {
            Toast.makeText(this,"Please add two mates at lest! and select one.",Toast.LENGTH_LONG).show();
        }
        else{
            item =  spinner.getSelectedItem().toString();
            Intent intent = new Intent(MainActivity.this,infoActivity.class);
            intent.putExtra("name",item);
            intent.putExtra("btn","New");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //youhua

            startActivity(intent);
//            finish();

        }

    }
//？？？
    public void Delete(View v){
        final View layoutView = View.inflate(this, R.layout.dialoglayout, null);
        btnOk = (Button)layoutView.findViewById(R.id.btnOk);
        txtName = (EditText)layoutView.findViewById(R.id.txtDia);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("Delete");
        builder.setIcon(R.mipmap.del);
        builder.setCancelable(true);
        builder.setView(layoutView);
        builder.setMessage("------Delete one  mate------");
        builder.show();
        txtName.setHint("Enter your mate name here.");

        final int length = (int)SharedPreferencesClass.get(getApplicationContext(),"length",0);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                String s = txtName.getText().toString();
                if (s.equals(""))
                    builder.dismiss();
                 else{
                    int index = mateNames.indexOf(s);//exist for index or noexist for -1
                    if (index == -1)
                        Toast.makeText(getApplicationContext(),"No exist!",Toast.LENGTH_SHORT).show();
                    else {
                        SharedPreferencesClass.remove(getApplicationContext(),String.valueOf(names[index]));
                        mateNames.remove(s);
                        spinner.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                        builder.dismiss();
                    }

                          }

            }
        });


    }

    //for hypeLink
    private SpannableStringBuilder addClickablePart(final int ID, String str) {

        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                ssb.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View view) {
                        //崩溃的原因：接受的GetIntent，寻找"name"与..找不到就会崩溃！！！！！！！！！
                        Intent intent = new Intent(MainActivity.this,infoActivity.class);
                        intent.putExtra("ID",ID);
                        intent.putExtra("name",item);
                        intent.putExtra("btn","Item");
                        startActivity(intent);


                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                         ds.setColor(Color.BLUE); // 设置文本颜色
                        // 去掉下划线
                        ds.setUnderlineText(true);
                    }

                }, 0,str.indexOf('['), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


        return ssb;
    } // end of addClickablePart
  //for Click  Spinner item
    public void clickItem(){

        if(first){
            first=false;
        }else{

            record.setText("");
            record.setTextSize(15);

            MyDatabaseHelper helper =  new MyDatabaseHelper(getApplicationContext());
            db = helper.getReadableDatabase();
            //use db.rawQuery(return Cursor ) and Cursor
            String item = spinner.getSelectedItem().toString();
            this.item = item;
            String[] s = new String[]{item};
            Cursor c = db.rawQuery("Select * from record where name = ?",s);
            if(c.getCount()==0) {
                Toast.makeText(getApplicationContext(), "No records of " +item, Toast.LENGTH_SHORT).show();
            }
            else{
                //one StringBuilder for SpannableStringBuilder use setSpan(new ClickableSpan( ....))
                count=c.getCount();
                SpannableString spanStr = new SpannableString("All :"+count+"\n");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF0000"));
                spanStr.setSpan(colorSpan, 0, 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                record.setText(spanStr);
                StringBuilder sb =  new StringBuilder();
                if(c.moveToFirst()){
                    do{
                        //将取出的每一行的Brief 添加到sb中，并显示到view ，添加超链接
                        String brief = c.getString(c.getColumnIndex("Brief"));
                        String time = c.getString(c.getColumnIndex("date"));
                        int recordID = c.getInt(c.getColumnIndex("ID"));//for read data from table
                        String ss = brief+"  [ "+time+" ]"+"\n"  ;
                        sb.append(ss);

                        //添加点击事件

                        record.append(addClickablePart(recordID,ss));
                        record.setMovementMethod(LinkMovementMethod.getInstance());

                    }while(c.moveToNext());//移动成功继续，否则退出循环
                }

            }
            if (!c.isClosed())
                c.close();
        }
    }

    //for Logic Edittext
    public void LogicFunction(){
         logic = (TextView)findViewById(R.id.txtlogic);
         logic.setText("");
         logic.setTextSize(15);

         int from=0,to=0;
         int fromCount=0,toCount=0;

        StringBuffer sbfrom = new StringBuffer();
        StringBuffer sbto = new StringBuffer();

         SpannableStringBuilder ssb = new SpannableStringBuilder();
         StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);//粗体


         Cursor c = db.rawQuery("select * from record where name = ?",new  String[]{item});
        if (c.moveToFirst()){
            do {
                if (c.getString(c.getColumnIndex("type")).equals("Borrow From"))//first select
                {
                   //循环归类
                    // 先分 借入 借出。每一个内通过objname 细分 借了谁 钱，最后加一个汇总与总和
                    //second select
                    fromCount++;
                    sbfrom.append(c.getString(c.getColumnIndex("objname"))+": -"+c.getString(c.getColumnIndex("money"))+"\n");
                    from += c.getInt(c.getColumnIndex("money"));


                }else{
                    toCount++;
                    sbto.append(c.getString(c.getColumnIndex("objname"))+": +"+c.getString(c.getColumnIndex("money"))+"\n");
                    to += c.getInt(c.getColumnIndex("money"));


                }
            }while (c.moveToNext());
        }
        if (!c.isClosed())
            c.close();
        SpannableString ssFrom = new SpannableString("Borrow From:("+fromCount+")\n");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF9797"));
        ssFrom.setSpan(colorSpan, 0, 12, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        SpannableString ssTo = new SpannableString("Lend To:("+toCount+")\n");
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#009ad6"));
        ssTo.setSpan(colorSpan1, 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        sbfrom.append("You borrow from friends all : -"+from+" .\n\n\n");//like last record of ...
        sbto.append("You Lend to friends all : +"+to+" !\n\n");
        ssb.append(ssFrom);//tital
        ssb.append(sbfrom);//record

        ssb.append(ssTo);//tital
        ssb.append(sbto);//record
        int sum =to - from;
        ssb.append("Summary : "+sum);

        ssb.setSpan(styleSpan,0,ssb.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        logic.setText(ssb);




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setTitle("EXIT");
            dialog.setMessage("Are u sure exit ?");
            dialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

