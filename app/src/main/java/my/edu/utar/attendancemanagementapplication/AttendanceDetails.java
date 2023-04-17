package my.edu.utar.attendancemanagementapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import my.edu.utar.attendancemanagementapplication.databinding.ActivityAttendanceDetailsBinding;

public class AttendanceDetails extends AppCompatActivity {
    ActivityAttendanceDetailsBinding binding;
    Handler handler=new Handler();
    String classcode,id,type;
    ListAdapterAttendance listAdapter;
    ArrayList<AttendanceClass> attendances=new ArrayList<>();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAttendanceDetailsBinding.inflate(getLayoutInflater());
        listAdapter=new ListAdapterAttendance(AttendanceDetails.this,attendances);
        binding.attendancelv.setAdapter(listAdapter);
        setContentView(binding.getRoot());
        Intent intent=this.getIntent();
        if(intent!=null){
            classcode=intent.getStringExtra("classcode");
            id=intent.getStringExtra("id");
            type=intent.getStringExtra("type");
        }

        new getDetails().start();
    }


    class getDetails extends Thread
    {

        @Override
        public void run(){
            handler.post(() -> {
                progressDialog=new ProgressDialog(AttendanceDetails.this);
                progressDialog.setMessage("Fetching Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });
            try {
                URL url=new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/classdetails?select=takenDate,takenTime,status&subjectCode=eq."+classcode+"&userID=eq."+id+"&type=eq."+type);
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
                            output=output+jsonArray.getJSONObject(i).get("takenTime").toString()+" "+jsonArray.getJSONObject(i).get("takenDate").toString()+" "+
                                    jsonArray.getJSONObject(i).get("status").toString();
                            String time=jsonArray.getJSONObject(i).get("takenTime").toString();
                            String date=jsonArray.getJSONObject(i).get("takenDate").toString();
                            String status=jsonArray.getJSONObject(i).get("status").toString();
                            AttendanceClass attendance=new AttendanceClass(time,status,date);
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