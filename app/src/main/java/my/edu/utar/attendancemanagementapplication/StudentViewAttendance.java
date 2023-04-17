package my.edu.utar.attendancemanagementapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import my.edu.utar.attendancemanagementapplication.databinding.ActivityStudentViewAttendanceBinding;

public class StudentViewAttendance extends AppCompatActivity {
    ActivityStudentViewAttendanceBinding binding;
    Handler handler=new Handler();
    ListAdapterStudent listAdapter;
    ArrayList<StudentOverallAttendance> attendances=new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStudentViewAttendanceBinding.inflate(getLayoutInflater());
        listAdapter=new ListAdapterStudent(StudentViewAttendance.this,attendances);
        binding.studentlistview.setAdapter(listAdapter);
        binding.studentlistview.setClickable(true);
        binding.studentlistview.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent=new Intent(StudentViewAttendance.this,AttendanceDetails.class);
            StudentOverallAttendance studentOverallAttendance=attendances.get(i);
            intent.putExtra("classcode",studentOverallAttendance.getClasscode());
            intent.putExtra("type",studentOverallAttendance.getType());
            intent.putExtra("id",binding.idnum.getText().toString());
            startActivity(intent);
        });
        setContentView(binding.getRoot());

        binding.checkbtn.setOnClickListener(view -> {
            if(binding.idnum.getText().toString()!=""){
                new getAttendance().start();
            }else{
                Toast.makeText(StudentViewAttendance.this, "Please fill in student id", Toast.LENGTH_SHORT).show();
            }
        });

    }
    class getAttendance extends Thread
    {

        @Override
        public void run(){
            handler.post(() -> {
                progressDialog=new ProgressDialog(StudentViewAttendance.this);
                progressDialog.setMessage("Fetching Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });
            try {
                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/getoverallattendance?select=subjectCode,type,count&studentid=eq."+binding.idnum.getText().toString());
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
                        attendances.clear();
                        for(int i=0;i<jsonArray.length();i++){
                            output=output+jsonArray.getJSONObject(i).get("subjectCode").toString()+" "+jsonArray.getJSONObject(i).get("type").toString()+" "+
                                    jsonArray.getJSONObject(i).get("count").toString();
                            String classcode=jsonArray.getJSONObject(i).get("subjectCode").toString();
                            String type=jsonArray.getJSONObject(i).get("type").toString();
                            String count=jsonArray.getJSONObject(i).get("count").toString();
                            StudentOverallAttendance attendance=new StudentOverallAttendance(classcode,count,type);
                            attendances.add(attendance);
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
}