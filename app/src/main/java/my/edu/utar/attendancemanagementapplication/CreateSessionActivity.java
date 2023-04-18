package my.edu.utar.attendancemanagementapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.maps.model.LatLng;

public class CreateSessionActivity extends AppCompatActivity {

    private EditText mSubjectEditText;
    private EditText mGroupEditText;
    private EditText mLocationEditText;
    private Button mDoneButton, btnPickDateTime, delBtn;
    private String subject, group, location, selectedD, selectedT, action = "";
    private String username, userRole;
    private Boolean loggedIn;

    AttendanceListDetails retrievedResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        // Retrieve login details
        SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        username = retrievePrefs.getString("username", "");
        userRole = retrievePrefs.getString("role", "");
        loggedIn = retrievePrefs.getBoolean("loggedIn", false);

        if(username != "" && loggedIn != false && userRole.equalsIgnoreCase("lecturer")){

            mSubjectEditText = findViewById(R.id.session_subject);
            mGroupEditText = findViewById(R.id.session_group);
            mLocationEditText = findViewById(R.id.session_location);
            btnPickDateTime = findViewById(R.id.btn_pick_datetime);
            mDoneButton = findViewById(R.id.session_done_button);
            delBtn = findViewById(R.id.session_delete_button);

            //modify attendance session's details
            String retrievedAttendanceID = getIntent().getStringExtra("retrievedAttendanceID");

            if(!(retrievedAttendanceID == null)){
                Handler handler = new Handler();
                MyModifySessionThread connectModifySessionThread = new MyModifySessionThread(retrievedAttendanceID, handler);
                connectModifySessionThread.start();

                mDoneButton.setText("Update");
                action = "update";
                delBtn.setVisibility(View.VISIBLE);
            }

            btnPickDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateTimePicker();
                }
            });

            // Define a contract to launch the activity and receive a result
            ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent lecturerLocation = result.getData();
                                location = lecturerLocation.getStringExtra("lecturerLocation");

                                // Extract the latitude and longitude values from the string
                                int startIndex = location.indexOf("(") + 1;
                                int endIndex = location.indexOf(")");
                                String[] latLngArray = location.substring(startIndex, endIndex).split(",");
                                double myLatitude = Double.parseDouble(latLngArray[0].trim());
                                double myLongitude = Double.parseDouble(latLngArray[1].trim());

                                String address = getCompleteAddressString(myLatitude, myLongitude);
                                mLocationEditText.setText(address);
                            }
                        }
                    });


            mLocationEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CreateSessionActivity.this, MapsActivity.class);
                    myActivityResultLauncher.launch(intent);

                }
            });

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = "delete";

                    Handler handler = new Handler();
                    MyThread connectThread = new MyThread(username, subject, group, selectedD, selectedT, null, location, handler);
                    connectThread.start();
                }
            });

            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subject = mSubjectEditText.getText().toString();
                    group = mGroupEditText.getText().toString();

                    Handler handler = new Handler();
                    MyThread connectThread = new MyThread(username, subject, group, selectedD, selectedT, null, location, handler);
                    connectThread.start();
                }
            });

        }else{
            if(userRole.equalsIgnoreCase("student")){
                Toast.makeText(getApplicationContext(),"You don't have the permission to access this page.",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(CreateSessionActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        }
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateSessionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format date and time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        String selectedTime = timeFormat.format(calendar.getTime());

                        //display selected date and time
                        btnPickDateTime.setText(selectedDate+", "+selectedTime);

                        selectedD = selectedDate;
                        selectedT = selectedTime;
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private class MyThread extends Thread{

        private String userID, subjectCode, type, date, time, qrCode, location;
        private Handler mHandler;

        public MyThread(String userID, String subjectCode, String type, String date, String time, String qrCode, String location, Handler handler){
            this.userID = userID;
            this.subjectCode = subjectCode;
            this.type = type;
            this.date = date;
            this.time = time;
            this.qrCode = qrCode;
            this.location = location;
            this.mHandler = handler;
        }

        public void run(){
            try {
                URL url = null;
                HttpURLConnection hc = null;
                if(mDoneButton.getText().equals("Save")){
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances");
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setRequestMethod("POST");
                }else if (action.equals("delete")){
                    Log.e("TAG", "run: action"+action );
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?attendanceID=eq."+ retrievedResult.getAttendanceID());
                    hc = (HttpURLConnection)url.openConnection();
                    Log.e("TAG", "run: action"+action );
                    hc.setRequestMethod("DELETE");
                    Log.e("TAG", "run: action"+action );
                }else{
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?attendanceID=eq."+ retrievedResult.getAttendanceID());
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setRequestMethod("PATCH");
                }

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));
                Log.e("TAG", "run: action"+action );
                if(!(action.equals("delete"))){
                    hc.setRequestProperty("Content-Type","application/json");
                    hc.setRequestProperty("Prefer","return=minimal");

                    String requestBody = "{\"userID\": \""+userID+"\", \"subjectCode\": \""+subjectCode.toUpperCase()+"\",\"type\": \""+type+"\",\"date\": \""+date+"\", \"time\": \""+time+"\",\"qrCode\":\""+qrCode+"\",\"location\":\""+location+"\"}";
                    hc.setDoOutput(true);
                    DataOutputStream os = new DataOutputStream(hc.getOutputStream());
                    os.writeBytes(requestBody);
                    os.flush();
                    os.close();
                }
                Log.e("TAG", "run: code "+hc.getResponseCode());
                if(hc.getResponseCode() == 201 || hc.getResponseCode() == 204){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String state = "";

                            if(mDoneButton.getText().equals("Save")){
                                state = "created";
                            }else{
                                state = "updated";
                                if(action.equals("delete")){
                                    state = "delete";
                                }
                            }

                            Toast.makeText(getApplicationContext(),"Attendance session is "+state+" successfully.",Toast.LENGTH_SHORT).show();

                            goToAttendanceListPage();
                        }
                    });
                }else{
                    Log.i("CreateSession","Response code: "+hc.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("TAG", "MalformedURLException: "+e.getMessage() );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "IOException: "+e.getMessage() );
            }
        }
    }

    private class MyModifySessionThread extends Thread{

        private String attendanceID;
        private Handler mHandler;

        public MyModifySessionThread(String attendanceID, Handler handler){
            this.attendanceID = attendanceID;
            this.mHandler = handler;
        }

        public void run(){
            try {
                URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?attendanceID=eq."+attendanceID);

                HttpURLConnection hc = (HttpURLConnection)url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));

                InputStream input = hc.getInputStream();
                String dbOutput = readStream(input);

                if(hc.getResponseCode() == 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(dbOutput);
                                retrievedResult = new AttendanceListDetails(jsonArray.getJSONObject(0).get("attendanceID").toString(),jsonArray.getJSONObject(0).get("userID").toString(),jsonArray.getJSONObject(0).get("subjectCode").toString(),jsonArray.getJSONObject(0).get("type").toString(),jsonArray.getJSONObject(0).get("date").toString(),jsonArray.getJSONObject(0).get("time").toString(),jsonArray.getJSONObject(0).get("qrCode").toString(),jsonArray.getJSONObject(0).get("location").toString());

                                subject = retrievedResult.getSubjectCode();
                                group = retrievedResult.getType();
                                location = retrievedResult.getLocation();
                                selectedD = retrievedResult.getDate();
                                selectedT = retrievedResult.getTime();

                                mSubjectEditText.setText(subject);
                                mGroupEditText.setText(group);

                                mLocationEditText.setText(getCompleteAddressString(covertStringToLatLng(location).latitude, covertStringToLatLng(location).longitude));
                                btnPickDateTime.setText(selectedD+", "+selectedT);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Log.i("LoginActivity","Response code: "+hc.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("TAG", "MalformedURLException: "+e.getMessage() );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "IOException: "+e.getMessage() );
            }
        }
    }

    private LatLng covertStringToLatLng(String strLat){
        // Extract the latitude and longitude values from the string
        int startIndex = strLat.indexOf("(") + 1;
        int endIndex = strLat.indexOf(")");
        String[] latLngArray = strLat.substring(startIndex, endIndex).split(",");
        double myLatitude = Double.parseDouble(latLngArray[0].trim());
        double myLongitude = Double.parseDouble(latLngArray[1].trim());

        LatLng result = new LatLng(myLatitude, myLongitude);

        return result;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
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

    public void goToAttendanceListPage(){
        Intent intent =new Intent(CreateSessionActivity.this, AttendanceList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Cannot get address!",Toast.LENGTH_LONG).show();
        }
        return strAdd;
    }
}