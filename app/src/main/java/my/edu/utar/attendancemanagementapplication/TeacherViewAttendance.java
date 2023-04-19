package my.edu.utar.attendancemanagementapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import my.edu.utar.attendancemanagementapplication.databinding.ActivityTeacherViewAttendanceBinding;

public class TeacherViewAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, EditDialog.CustomDialogInterface, ListAdapter.OnItemClick {

    private int PERMISSIONCODE=1;
    EditText classcode;
    ListView lv;
    LinearLayout linearLayout;
    ArrayList<String> typelist=new ArrayList<>();
    ArrayList<String> attendanceidlist=new ArrayList<>();
    ArrayAdapter<String> adapter;
    ActivityTeacherViewAttendanceBinding binding;
    ListAdapter listAdapter;
    String currentdate,type,statusshow,id,attendanceidget,code;
    Date cdate;
    String codeedit;
    String position;
    Bitmap bitmap;
    Handler handler=new Handler();
    ArrayList<Students> studentsArrayList=new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_attendance);
        classcode=findViewById(R.id.classcode);
        lv=findViewById(R.id.lv);
        binding=ActivityTeacherViewAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listAdapter=new ListAdapter(TeacherViewAttendance.this,studentsArrayList,getSupportFragmentManager(),this);
        binding.lv.setAdapter(listAdapter);
        linearLayout=findViewById(R.id.ll);
        typelist.add("Select one type");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.classcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                codeedit=editable.toString().toLowerCase();
                if(!editable.toString().isEmpty()){
                    typelist.clear();
                    handler.removeCallbacks(mFilterTask);
                    handler.postDelayed(mFilterTask, 2000);



                }else{
                    Toast.makeText(TeacherViewAttendance.this, "Class code is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.typespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.searchbutton.setOnClickListener(view -> {
            try{
                if(binding.classcode.getText().toString()!="" && !currentdate.equals("") && binding.typespinner.getSelectedItem() != null){
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    code=binding.classcode.getText().toString();
                    try {
                        cdate=(Date)formatter.parse(currentdate);
                        Log.d("tag",currentdate);
                        new fetchData().start();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }else if(currentdate.equals("")){
                    Toast.makeText(TeacherViewAttendance.this, "Choose a date", Toast.LENGTH_SHORT).show();
                }else if(classcode.getText().toString().equals("")){
                    Toast.makeText(TeacherViewAttendance.this, "Type class code to search", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(TeacherViewAttendance.this, "Select type of class to search", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                Toast.makeText(TeacherViewAttendance.this,"Info needed",Toast.LENGTH_SHORT).show();
            }


        });

        binding.datebtn.setOnClickListener(view -> {
            DialogFragment datepicker=new DatePickerFragment();
            datepicker.show(getSupportFragmentManager(),"date picker");
        });


        binding.savebtn.setOnClickListener(view -> {
            if(listAdapter.getCount()!=0){
               if(ContextCompat.checkSelfPermission(TeacherViewAttendance.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(TeacherViewAttendance.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        bitmap = LoadBitmap(linearLayout, linearLayout.getWidth()-80, linearLayout.getHeight()+60);
                        setPdf();
                    }else{
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }else{
                   requestStoragePermission();
               }

            }else{
                Toast.makeText(TeacherViewAttendance.this, "No data on list", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private Bitmap LoadBitmap(View v, int width, int height){
        Bitmap bitmap1=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap1);

        v.draw(canvas);
        return bitmap1;
    }

    private void setPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;


        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        Rect srcRect = new Rect(20, 0, bitmap.getWidth(),
                bitmap.getHeight() );
        Rect desRect = new Rect(60, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setColor(Color.BLUE);


        canvas.drawBitmap(bitmap, srcRect, desRect, null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/"+codeedit+type+" "+currentdate+".pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }////////////////////

        // close the document
        document.close();
        Toast.makeText(this, "successfully pdf created", Toast.LENGTH_SHORT).show();

        openPdf();

    }

    private void openPdf() {
        String path;
        File file = new File("/sdcard/"+codeedit+type+" "+currentdate+".pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(file);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No Application for pdf view", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(TeacherViewAttendance.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONCODE);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        currentdate=sdf.format(c.getTime());
        TextView datetxt=findViewById(R.id.datetxt);
        datetxt.setText(currentdate);
    }

    @Override
    public void applyText(String status)  {
        statusshow=status;
        id=studentsArrayList.get(Integer.parseInt(position)).getId();
        attendanceidget=attendanceidlist.get(Integer.parseInt(position));
        new updateData().start();
    }

    @Override
    public void onClick(String value) {
        position = value;
    }


    class fetchData extends Thread
    {
        @Override
        public void run(){
            handler.post(() -> {
                progressDialog=new ProgressDialog(TeacherViewAttendance.this);
                progressDialog.setMessage("Fetching Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });
            try {
                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/getattendance?select=status,userID,name,attendanceID&subjectCode=eq."+codeedit.toLowerCase()+"&date=eq."+currentdate+"&type=eq."+type);
                HttpURLConnection hc= (HttpURLConnection) url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));
                Log.i("tag",url.toString());
                InputStream input=hc.getInputStream();
                String result=readStream(input);
                if(hc.getResponseCode()==200){
                    Log.d("tag","Response Successful");
                    Log.d("result",result);

                    if(!result.isEmpty()){
                        JSONArray jsonArray=new JSONArray(result);
                        String output="";
                        studentsArrayList.clear();
                        attendanceidlist.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            output=output+jsonArray.getJSONObject(i).get("name").toString()+" "+jsonArray.getJSONObject(i).get("userID").toString()+" "+
                                    jsonArray.getJSONObject(i).get("status").toString()+jsonArray.getJSONObject(i).get("attendanceID").toString();
                            String name=jsonArray.getJSONObject(i).get("name").toString();
                            String id=jsonArray.getJSONObject(i).get("userID").toString();
                            String status=jsonArray.getJSONObject(i).get("status").toString();
                            String attendanceID=jsonArray.getJSONObject(i).get("attendanceID").toString();
                            Students students=new Students(name,id,status);
                            studentsArrayList.add(students);
                            attendanceidlist.add(attendanceID);
                        }
                        Log.d("tag",output);

                    }else{
                        Toast.makeText(TeacherViewAttendance.this, "No record found", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Log.d("tag",""+hc.getResponseCode());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d("tag",e.getMessage());
            }

            handler.post(() -> {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                }


            });
        }

    }


    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new
                    ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

   





    class updateData extends Thread{
        HttpURLConnection hc;
        @Override
        public void run() {
            try {

                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Session?userID=eq."+id+"&attendanceID=eq."+attendanceidget);
                hc= (HttpURLConnection) url.openConnection();

                hc.setRequestMethod("PATCH");
                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));
                hc.setRequestProperty("Content-Type","application/json");
                hc.setRequestProperty("Prefer", "return=minimal");
                Log.i("tag",url.toString());
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("status",statusshow);
                hc.setDoOutput(true);
                DataOutputStream outputStream=new DataOutputStream(hc.getOutputStream());
                outputStream.writeBytes(jsonObject.toString());
                outputStream.flush();
                outputStream.close();
                runOnUiThread(() -> {
                    studentsArrayList.get(Integer.parseInt(position)).setStatus(statusshow);
                    listAdapter.notifyDataSetChanged();
                });


                if(hc.getResponseCode()==204||hc.getResponseCode()==200){
                    Log.d("tag","Response Successful");

                }else{
                    Log.d("tag",""+hc.getResponseCode());

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d("tag",e.getMessage());
            }



        }


    }


    class getListofType extends Thread{
        HttpURLConnection hc;
        @Override
        public void run() {
            try {
                Log.d("tag","here");
                typelist.clear();
                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/gettype?select=type&subjectCode=eq."+codeedit.toLowerCase());
                HttpURLConnection hc= (HttpURLConnection) url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));
                Log.i("tag",url.toString());
                InputStream input=hc.getInputStream();
                String result=readStream(input);
                if(hc.getResponseCode()==200){
                    Log.d("tag","Response Successful");
                    Log.d("result",result);

                    if(!result.isEmpty()){
                        JSONArray jsonArray=new JSONArray(result);
                        String output="";
                        typelist.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            output=output+jsonArray.getJSONObject(i).get("type").toString()+" ";
                            String types=jsonArray.getJSONObject(i).get("type").toString();
                            typelist.add(types);
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                        Log.d("tag", String.valueOf(typelist));

                    }
                }else{
                    Log.d("tag",""+hc.getResponseCode());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d("tag",e.getMessage());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSIONCODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    bitmap = LoadBitmap(linearLayout, linearLayout.getWidth(), linearLayout.getHeight());
                    setPdf();

            }else{
                Toast.makeText(TeacherViewAttendance.this,"Permission Denied.",Toast.LENGTH_SHORT).show();
            }

        }


    }


    Runnable mFilterTask = new Runnable() {

        @Override
        public void run() {
           new getListofType().start();
            binding.typespinner.setAdapter(adapter);
        }
    };
}