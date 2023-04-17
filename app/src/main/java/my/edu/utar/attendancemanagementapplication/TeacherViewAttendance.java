package my.edu.utar.attendancemanagementapplication;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.DocumentException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.edu.utar.attendancemanagementapplication.databinding.ActivityTeacherViewAttendanceBinding;

public class TeacherViewAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, EditDialog.CustomDialogInterface, ListAdapter.OnItemClick {

    EditText classcode;
    ListView lv;
    ArrayList<String> attendanceidlist=new ArrayList<>();
    ActivityTeacherViewAttendanceBinding binding;
    ListAdapter listAdapter;
    String currentdate,type,statusshow,id,attendanceidget,code;
    Date cdate;
    String position;
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
                Toast.makeText(TeacherViewAttendance.this,"Info needed,Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }


        });

        binding.datebtn.setOnClickListener(view -> {
            DialogFragment datepicker=new DatePickerFragment();
            datepicker.show(getSupportFragmentManager(),"date picker");
        });


        binding.savebtn.setOnClickListener(view -> {
            if(listAdapter.getCount()!=0){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                createPdf();
            }else{
                Toast.makeText(TeacherViewAttendance.this, "No data on list", Toast.LENGTH_SHORT).show();
            }

        });
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(TeacherViewAttendance.this,R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.typespinner.setAdapter(adapter);
        binding.typespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        Toast.makeText(TeacherViewAttendance.this, position, Toast.LENGTH_SHORT).show();
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
                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Session?select=status,attendanceID,userID,Students(Name),Attendance(subjectCode)&Attendance.subjectCode=eq."+classcode.getText().toString()+"&takenDate=eq."+currentdate+"&Attendance.type=eq."+type);
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
                            output=output+jsonArray.getJSONObject(i).getJSONObject("Students").get("Name").toString()+" "+jsonArray.getJSONObject(i).get("userID").toString()+" "+
                                    jsonArray.getJSONObject(i).get("status").toString()+jsonArray.getJSONObject(i).get("attendanceID").toString();
                            String name=jsonArray.getJSONObject(i).getJSONObject("Students").get("Name").toString();
                            String id=jsonArray.getJSONObject(i).get("userID").toString();
                            String status=jsonArray.getJSONObject(i).get("status").toString();
                            String attendanceID=jsonArray.getJSONObject(i).get("attendanceID").toString();
                            Students students=new Students(name,id,status);
                            studentsArrayList.add(students);
                            attendanceidlist.add(attendanceID);
                        }
                        Log.d("tag",output);

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

    private void createPdf() {
        if (binding.classcode.getText().toString()!=""){
            List<String> paragraphList = Arrays.asList("Class code: "+code,"Date: "+currentdate);
            PdfService pdfService = new PdfService();
            try {
                openFile(pdfService.createAttendanceTable(studentsArrayList, paragraphList));

            } catch (DocumentException e) {
                e.printStackTrace();
                Toast.makeText(TeacherViewAttendance.this, "Failed to create "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.d("tag","Class code is empty");
        }




    }

    private void openFile(File file) {
        String path = null;
        try {
            Uri urifile=Uri.fromFile(file);
            path = FileHandler.getPath(this, urifile);
            File pdfFile = new File(path);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri=Uri.fromFile(pdfFile);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(TeacherViewAttendance.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
}