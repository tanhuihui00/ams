package my.edu.utar.attendancemanagementapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AttendanceList extends AppCompatActivity {

    private String username, userRole;
    private Boolean loggedIn;

    private LinearLayout parentLayout;
    String searchedSubject = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        // Retrieve login details
        SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        username = retrievePrefs.getString("username", "");
        userRole = retrievePrefs.getString("role", "");
        loggedIn = retrievePrefs.getBoolean("loggedIn", false);

        parentLayout = findViewById(R.id.linearView);

        if(username != "" && loggedIn != false && userRole.equals("lecturer")){
            Handler handler = new Handler();
            MyThread connectThread = new MyThread(handler, "retrieveAllRecord");
            connectThread.start();
        }else{
            if(userRole.equals("student")){
                Toast.makeText(getApplicationContext(),"You don't have the permission to access this page.",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(AttendanceList.this, MainActivity2.class);
                startActivity(intent);
            }
        }
    }

    AttendanceListDetails result;

    private class MyThread extends Thread{

        private Handler mHandler;
        private String action;

        public MyThread(Handler handler, String action){
            this.mHandler = handler;
            this.action = action;
        }

        public void run(){

            try {
                URL url = null;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parentLayout.removeAllViews();
                            }
                        });
                    }
                }).start();

                if(action.equals("retrieveAllRecord") || searchedSubject == ""){
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?userID=eq."+username+"&order=date.desc");
                }else{
                    url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?userID=eq."+username+"&subjectCode=eq."+searchedSubject.toUpperCase()+"&order=date.desc");
                }

                HttpURLConnection hc = (HttpURLConnection)url.openConnection();

                hc.setRequestProperty("apikey",getString(R.string.apikey));
                hc.setRequestProperty("Authorization","Bearer "+getString(R.string.apikey));

                InputStream input = hc.getInputStream();
                String dbOutput = readStream(input);

                if(hc.getResponseCode() == 200){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            addSearchBarLayout();
                            try {
                                JSONArray jsonArray = new JSONArray(dbOutput);
                                if(jsonArray.length()>0){
                                    for(int i=0; i<jsonArray.length(); i++){
                                        result = new AttendanceListDetails(jsonArray.getJSONObject(i).get("attendanceID").toString(),jsonArray.getJSONObject(i).get("userID").toString(),jsonArray.getJSONObject(i).get("subjectCode").toString(),jsonArray.getJSONObject(i).get("type").toString(),jsonArray.getJSONObject(i).get("date").toString(),jsonArray.getJSONObject(i).get("time").toString(),jsonArray.getJSONObject(i).get("qrCode").toString(),jsonArray.getJSONObject(i).get("location").toString());
                                        addNewLayout();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),"No records found.",Toast.LENGTH_SHORT).show();
                                }

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

    private void addNewLayout(){

        LinearLayout newVerticalLayout = new LinearLayout(this);
        newVerticalLayout.setOrientation(LinearLayout.VERTICAL);
        newVerticalLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newVerticalLayout.setPadding(10, 10, 10, 10);
        parentLayout.addView(newVerticalLayout);

        int paddingInDp = 10;  // desired padding in dp
        float density = getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (paddingInDp * density + 0.5f);  // convert dp to pixels

        // Create a new vertical LinearLayout
        LinearLayout newLayout = new LinearLayout(this);
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create TextViews for each field in the layout
        TextView subjectCodeTextView = new TextView(this);
        subjectCodeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        subjectCodeTextView.setText(result.getSubjectCode());
        subjectCodeTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        TextView typeTextView = new TextView(this);
        typeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        typeTextView.setText(result.getType());
        typeTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        TextView dateTextView = new TextView(this);
        dateTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        dateTextView.setText(result.getDate());
        dateTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        TextView timeTextView = new TextView(this);
        timeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        timeTextView.setText(result.getTime());
        timeTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        Button viewButton = new Button(this);
        viewButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        viewButton.setText("View");
        viewButton.setTag(result.getAttendanceID());
        viewButton.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveSessionInfo(v);
            }
        });

        Button qrButton = new Button(this);
        qrButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        qrButton.setText("QR");
        qrButton.setTag(result.getAttendanceID());
        qrButton.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQrCode(v);
            }
        });

        // Add TextViews and Buttons to the new layout
        newLayout.addView(subjectCodeTextView);
        newLayout.addView(typeTextView);
        newLayout.addView(dateTextView);
        newLayout.addView(timeTextView);
        newLayout.addView(viewButton);
        newLayout.addView(qrButton);

        // Add the new layout to the parent layout
        newVerticalLayout.addView(newLayout);
    }

    private void addSearchBarLayout(){

        LinearLayout searchBar = new LinearLayout(this);
        searchBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        searchBar.setOrientation(LinearLayout.HORIZONTAL);
        searchBar.setPadding(10, 10, 10, 10);

        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        editTextParams.weight = 1;
        editText.setLayoutParams(editTextParams);
        editText.setHint("Enter Subject");

        Button button = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(buttonParams);
        button.setText("Search");
        button.setId(R.id.search_button);

        button.setOnClickListener(view -> {
            searchedSubject = editText.getText().toString();
            if (searchedSubject.isEmpty()) {
                Toast.makeText(AttendanceList.this, "Please enter a subject", Toast.LENGTH_SHORT).show();
            }
            Handler handler = new Handler();
            MyThread connectThread = new MyThread(handler, "SearchRecord");
            connectThread.start();
        });

        Button clearBtn = new Button(this);
        clearBtn.setLayoutParams(buttonParams);
        clearBtn.setText("Clear");
        clearBtn.setId(R.id.search_button);

        clearBtn.setOnClickListener(view -> {
            editText.setText("");
            editText.setHint("Enter Subject");
            Handler handler = new Handler();
            MyThread connectThread = new MyThread(handler, "retrieveAllRecord");
            connectThread.start();
        });

        searchBar.addView(editText);
        searchBar.addView(button);
        searchBar.addView(clearBtn);
        parentLayout.addView(searchBar);
    }

    public void retrieveSessionInfo(View view) {
        //retrieve particular one attendance session record
        String retrievedAttendanceID = view.getTag().toString();
        Log.e("TAG", "retrieveSessionInfo: "+retrievedAttendanceID);

        Intent intent = new Intent(this, CreateSessionActivity.class);
        intent.putExtra("retrievedAttendanceID", retrievedAttendanceID);
        startActivity(intent);

    }

    public void generateQrCode(View view) {
        //go to generateQr Module
    }
}