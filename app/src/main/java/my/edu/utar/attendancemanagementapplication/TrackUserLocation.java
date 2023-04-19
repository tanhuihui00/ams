package my.edu.utar.attendancemanagementapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrackUserLocation extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    LocationTrack locationTrack;
    String mySubjectCode, attendanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_user_location);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted
            // Do your task here that requires location permission
        } else {
            // Permission is not granted yet
            // Request location permission from the user
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);
        }

        locationTrack = new LocationTrack(TrackUserLocation.this);

        if (locationTrack.canGetLocation()) {
            Double mLat = locationTrack.getLatitude();
            Double mLng = locationTrack.getLongitude();

            Intent intent = getIntent();
            String sessionID = intent.getStringExtra("sessionId");

            setSubjectCode(sessionID);

            Handler handler = new Handler();
            MyThread connectThread = new MyThread(sessionID, mLat, mLng, handler);
            connectThread.start();
        } else {
            locationTrack.showSettingsAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                // Do your task here that requires location permission
            } else {
                // Permission is denied
                // Show an explanation to the user and ask for permission again, or handle the denial gracefully
            }
        }
    }

    String result = "";
    Date currentDate;
    Time currentTime;

    private class MyThread extends Thread{

        private String attendanceID;
        private Double mLat, mLng;
        private Handler mHandler;

        public MyThread(String attendanceID, Double mLat, Double mLng, Handler handler){
            this.attendanceID = attendanceID;
            this.mLat = mLat;
            this.mLng = mLng;
            this.mHandler = handler;
        }

        public void run(){

            try {
                //filter based on email
                URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?attendanceID=eq."+attendanceID);
                HttpURLConnection hc = (HttpURLConnection)url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));

                InputStream input = hc.getInputStream();
                result = readStream(input);

                if(hc.getResponseCode() == 200){
                    mHandler.post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                result = jsonArray.getJSONObject(0).get("location").toString();

                                float distance = findDistanceBetween(mLat,mLng,covertStringToLatLng(result).latitude,covertStringToLatLng(result).longitude);

                                //if student's current location must around 500 meters with the designated area (campus)
                                if(distance <= 500){

                                    // Get current date
                                    java.util.Date currentDateTime = Calendar.getInstance().getTime();

                                    // Format the date to "yyyy-MM-dd" using SimpleDateFormat
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String dateString = dateFormat.format(currentDateTime);
                                    currentDate = new java.sql.Date(dateFormat.parse(dateString).getTime());

                                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                                    String timeString = sdfTime.format(currentDateTime);
                                    currentTime = new java.sql.Time(sdfTime.parse(timeString).getTime());

                                    // Retrieve login details
                                    SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                                    String userID = retrievePrefs.getString("username", "");

                                    Handler handler2 = new Handler();
                                    MySearchDuplicateThread connectSearchThread = new MySearchDuplicateThread(attendanceID, userID, currentDate,  currentTime, handler2);
                                    connectSearchThread.start();

                                }else{
                                    DecimalFormat decimalFormat = new DecimalFormat("#");
                                    String formattedNumber = decimalFormat.format(distance);
                                    String message = "Attendance cannot be taken because the location is too far from the designated area. \n\nYour distance to designated area is "+formattedNumber+" meters.\n\nPlease be within 500 meters and try again.";

                                    AlertDialog.Builder builder = new AlertDialog.Builder(TrackUserLocation.this);
                                    builder.setMessage(message)
                                            .setTitle("Error!")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(TrackUserLocation.this, MainActivity2.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Log.i("TrackUserLocation","Response code: "+hc.getResponseCode());
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

    private class MySearchDuplicateThread extends Thread{

        private String attendanceID, userID;
        Date takenDate;
        Time takenTime;
        private Handler mHandler;

        public MySearchDuplicateThread(String attendanceID, String userID, Date takenDate, Time takenTime, Handler handler){
            this.attendanceID = attendanceID;
            this.userID = userID;
            this.takenDate = takenDate;
            this.takenTime = takenTime;
            this.mHandler = handler;
        }

        public void run(){

            try {
                URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Session?userID=eq."+userID+"&attendanceID=eq."+attendanceID);
                HttpURLConnection hc = (HttpURLConnection)url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));

                InputStream input = hc.getInputStream();
                result = readStream(input);

                if(hc.getResponseCode() == 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);

                                Handler handler2 = new Handler();
                                MyInsertThread connectThread2 = null;

                                if(jsonArray.length()>0){
                                    connectThread2 = new MyInsertThread(attendanceID, userID, "SUCCESS", currentDate,  currentTime, false, handler2);
                                }else{
                                    connectThread2 = new MyInsertThread(attendanceID, userID, "SUCCESS", currentDate,  currentTime, true, handler2);
                                }
                                connectThread2.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Log.i("TrackUserLocation","Response code: "+hc.getResponseCode());
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

    private class MySearchSubjectCodeThread extends Thread{

        private String attendanceID;
        private Handler mHandler;

        public MySearchSubjectCodeThread(String attendanceID, Handler handler){
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
                mySubjectCode = readStream(input);

                if(hc.getResponseCode() == 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(mySubjectCode);
                                mySubjectCode = jsonArray.getJSONObject(0).get("subjectCode").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Log.i("TrackUserLocation","Response code: "+hc.getResponseCode());
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

    private class MyInsertThread extends Thread{

        private String attendanceID, userID, status;
        Date takenDate;
        Time takenTime;
        private Handler mHandler;
        private boolean insert;

        public MyInsertThread(String attendanceID, String userID, String status, Date takenDate, Time takenTime, Boolean insert, Handler handler){
            this.attendanceID = attendanceID;
            this.userID = userID;
            this.status = status;
            this.takenDate = takenDate;
            this.takenTime = takenTime;
            this.insert = insert;
            this.mHandler = handler;
        }

        public void run(){

            try {
                URL url = null;
                HttpURLConnection hc = null;

                if(insert){
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Session");
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setRequestMethod("POST");
                }else{
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Session?userID=eq."+ userID+"&attendanceID=eq."+attendanceID);
                    hc = (HttpURLConnection)url.openConnection();
                    hc.setRequestMethod("PATCH");
                }

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));
                hc.setRequestProperty("Content-Type","application/json");
                hc.setRequestProperty("Prefer","return=minimal");

                String requestBody = "{\"attendanceID\": \""+attendanceID+"\", \"userID\": \""+userID+"\",\"status\": \""+status+"\",\"takenDate\": \""+takenDate+"\", \"takenTime\": \""+takenTime+"\"}";
                hc.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(hc.getOutputStream());
                os.writeBytes(requestBody);
                os.flush();
                os.close();

                if(hc.getResponseCode() == 201 || hc.getResponseCode() == 204){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder builder = new AlertDialog.Builder(TrackUserLocation.this);
                            builder.setMessage("Your attendance for "+mySubjectCode+" has been taken.")
                                    .setTitle("Attendance")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(TrackUserLocation.this, MainActivity2.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }else{
                    Log.i("TrackUserLocation","Response code: "+hc.getResponseCode());
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

    private void setSubjectCode(String attendanceID){
        Handler handler = new Handler();
        MySearchSubjectCodeThread connectThread = new MySearchSubjectCodeThread(attendanceID, handler);
        connectThread.start();
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

    private float findDistanceBetween(double latA, double lngA, double latB, double lngB){
        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        float[] distance = new float[1];

        Location.distanceBetween(
                locationA.getLatitude(), locationA.getLongitude(),
                locationB.getLatitude(), locationB.getLongitude(),
                distance);

        float meters = distance[0];
        Log.e("TAG", "connected to SUPABASE Attendance table. Dsitance: " + meters);
        return meters;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


}