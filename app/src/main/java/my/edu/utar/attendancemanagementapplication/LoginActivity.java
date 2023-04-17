package my.edu.utar.attendancemanagementapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String username = retrievePrefs.getString("username", "");
        String userRole = retrievePrefs.getString("role", "");
        Boolean loggedIn = retrievePrefs.getBoolean("loggedIn", false);

        if (!username.equals("") && loggedIn != false){
            if(userRole.equalsIgnoreCase("student")){
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Handler handler = new Handler();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                MyThread connectThread = new MyThread(email, password, handler);
                connectThread.start();
            }
        });
    }

    private class MyThread extends Thread{

        private String mEmail;
        private String mPW;
        private Handler mHandler;

        public MyThread(String email, String pw, Handler handler){
            this.mEmail = email;
            this.mPW = pw;
            this.mHandler = handler;
        }

        public void run(){

            try {
                //filter based on email
                URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Student?email=eq."+mEmail);
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
                                if(jsonArray.length()>0){
                                    result = jsonArray.getJSONObject(0).get("password").toString();

                                    if(mPW.equals(result)){
                                        Toast.makeText(getApplicationContext(),"Login successfully.",Toast.LENGTH_SHORT).show();

                                        // Store login details
                                        SharedPreferences savePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = savePrefs.edit();
                                        editor.putString("username", jsonArray.getJSONObject(0).get("userID").toString());
                                        editor.putString("role", jsonArray.getJSONObject(0).get("role").toString());
                                        editor.putBoolean("loggedIn", true);
                                        editor.putString("name",jsonArray.getJSONObject(0).get("name").toString());
                                        editor.commit();

                                        SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                                        String role = prefs.getString("role", "");

                                        Intent intent;
                                        if(role.equalsIgnoreCase("student")){
                                            intent = new Intent(LoginActivity.this, MainActivity2.class);
                                        }else{
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                        }
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        Toast.makeText(getApplicationContext(),"Incorrect login credentials, please try again.",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),"Email is not yet registered, please try again or register.",Toast.LENGTH_SHORT).show();
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
}