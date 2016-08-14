package com.example.sharath.qrscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity  {

    //private ZXingScannerView mScannerView;
    TextView no_points,clue;
    MyDatabase db;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    List<String> total_clues,clues;
    Spinner progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        no_points=(TextView)findViewById(R.id.tv_number_points);
        clue=(TextView)findViewById(R.id.tv_clue);
        progress=(Spinner)findViewById(R.id.s_progress);
        pref=getSharedPreferences("NEXT_CLUE", MODE_PRIVATE);
        editor=pref.edit();
       // editor.putInt("no_clues", 0);
        //editor.commit();
        total_clues= new ArrayList<>(Arrays.asList("Gajendra Circle", "Himalaya", "Cafe Coffee Day", "Guru", "Velachery", "Taramani",
                "Library", "Shopping Complex", "Admin", "Main Gate"));
        db=new MyDatabase(this);
        /*db.emptyTable();
        db.addRow("gc", 10, 0,"Gajendra Circle");
        db.addRow("him",8,1,"Himalaya");
        db.addRow("ccd",12,2,"Cafe Coffee Day");
        db.addRow("guru",10,3,"Guru");
        db.addRow("vel",7,4,"Velachery");
        db.addRow("tar",9,5,"Taramani");
        db.addRow("lib",10,6,"Library");
        db.addRow("shop",12,7,"Shopping Complex");
        db.addRow("adm",9,8,"Admin");
        db.addRow("main",7,9,"Main Gate");*/
        int num=0,point=0;
        for(int j=0;j<db.getCount();++j)
            if(db.getCell("MARKED",j).equals("1")) {
                num++;
                point+=Integer.parseInt(db.getCell("POINTS",j));
                Log.i("point",String.valueOf(point));
            }
        no_points.setText(String.valueOf(point));
        clues=new ArrayList<>();
        if(!pref.contains("current")){
            db.emptyTable();
            db.addRow("gc", 10, 0,"Gajendra Circle");
            db.addRow("him",8,1,"Himalaya");
            db.addRow("ccd",12,2,"Cafe Coffee Day");
            db.addRow("guru",10,3,"Guru");
            db.addRow("vel",7,4,"Velachery");
            db.addRow("tar",9,5,"Taramani");
            db.addRow("lib",10,6,"Library");
            db.addRow("shop",12,7,"Shopping Complex");
            db.addRow("adm",9,8,"Admin");
            db.addRow("main",7,9,"Main Gate");
            editor.putInt("no_clues",0);
            clues=total_clues;
            randomize();
            List<String> clues_scanned=new ArrayList<>();
            clues_scanned.add(0, "No clues scanned");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,clues_scanned);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            progress.setAdapter(adapter);
        }else if(num>0){
            List<String> clues_scanned = new ArrayList<>();
            int cur_number = pref.getInt("no_clues", 0);
            for (int i = 1; i <= cur_number; ++i)
                clues_scanned.add(i - 1, pref.getString("scanned_" + String.valueOf(i), ""));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clues_scanned);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            progress.setAdapter(adapter);
            progress.setSelection(clues_scanned.size() - 1);
        }else{
            List<String> clues_scanned=new ArrayList<>();
            clues_scanned.add(0, "No clues scanned");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,clues_scanned);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            progress.setAdapter(adapter);
        }
        clue.setText(pref.getString("current", ""));
    }
    public void randomize(){
        Random r= new Random();
        int random=r.nextInt(10-pref.getInt("no_clues",0));
        editor.putString("current", clues.get(random));
        clues.remove(random);
        String string="";
        for(int i=0;i<clues.size();++i){
            string=string+clues.get(i);
            if(i!=clues.size()-1)
                string+=":";
        }
        editor.putString("clues_left",string);
        editor.commit();
        clue.setText(pref.getString("current",""));
    }
    /*@Override
    public void handleResult(Result rawResult) {
        // Do something with the result here</p>
        //  Log.e(handler;, rawResult.getText()); // Prints scan results<br />
        //Log.e(&quot;handler&quot;, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)</p>
        // show the scanner result into dialog box.<br />
        mScannerView.stopCameraPreview();
        setContentView(R.layout.activity_main);
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();
               *//* AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Scan Result");
                builder.setMessage(rawResult.getText());
                AlertDialog alert1 = builder.create();
                alert1.show();  *//* // If you would like to resume scanning, call this method below:<br />
        // mScannerView.resumeCameraPreview(this);<br />
    }*/

    public void qrscanner(View view) {// Programmatically initialize the scanner view<br />

        /*mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.<br />
        mScannerView.startCamera();  */
            new IntentIntegrator(this).initiateScan();// Start camera<br />
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if(scanResult.getContents().trim().length()>0)
            {
                /*for(int i=0;i<db.getCount();++i){
                    if(!scanResult.getContents().equals(pref.getString("current",""))){
                            Toast.makeText(this, "Incorrect hint scanned", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                        int cur_number=pref.getInt("no_clues",0);
                        editor.putInt("no_clues",cur_number+1);
                            int points=Integer.parseInt(db.getCell("POINTS",i));
                            db.updateRow(scanResult.getContents(),points,i,1);
                            int num=0,point=0;
                            for(int j=0;j<db.getCount();++j)
                                if(db.getCell("MARKED",j).equals("1")) {
                                    num++;
                                    point+=Integer.parseInt(db.getCell("POINTS",j));
                                }
                            no_points.setText(String.valueOf(point));
                            no_hints.setText(String.valueOf(num));
                            Toast.makeText(this,"Hint successfully scanned",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }*/
                String cur_clue=pref.getString("current","");
                Bundle bundle=new Bundle();
                int i=0,pos=0;
                for(i=0;i<10;++i)
                    if(cur_clue.equals(total_clues.get(i))) {
                        bundle = db.getRow(i);
                        pos=i;
                    }
                String tag=bundle.getString("TAG");
                if(!scanResult.getContents().equals(tag)){
                    Toast.makeText(this, "Incorrect hint scanned", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    db.updateRow(pos, 1);
                    Toast.makeText(this, "Hint scanned successfully", Toast.LENGTH_SHORT).show();
                    String[] clues_left=pref.getString("clues_left","").split(":");
                    clues=new ArrayList<>(Arrays.asList(clues_left));
                    int cur_number=pref.getInt("no_clues", 0);
                    editor.putInt("no_clues", cur_number + 1);
                    editor.putString("scanned_"+String.valueOf(cur_number+1),pref.getString("current",""));
                    editor.commit();
                    int point=0;
                    for(int j=0;j<db.getCount();++j) {
                        Log.i("point",String.valueOf(point)+"for");
                        if (db.getCell("MARKED", j).equals("1")) {
                            point += Integer.parseInt(db.getCell("POINTS", j));
                        }
                    }
                    no_points.setText(String.valueOf(point));
                    randomize();
                    List<String> clues_scanned=new ArrayList<>();
                    for(i=1;i<=cur_number+1;++i)
                        clues_scanned.add(i-1,pref.getString("scanned_"+String.valueOf(i),""));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,clues_scanned);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    progress.setAdapter(adapter);
                    progress.setSelection(clues_scanned.size()-1);
                }
                }
            }
        else{
            super.onActivityResult(requestCode,resultCode,intent);
        }
    }
}