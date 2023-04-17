package my.edu.utar.attendancemanagementapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupPassword, signupId, signupPhone;
    RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        roleRadioGroup = findViewById(R.id.role_radio_group);

        Handler handler = new Handler();

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupId = findViewById(R.id.signup_id);
        signupPhone = findViewById(R.id.signup_phone);
        Button signUpBtn = findViewById(R.id.signup_button);
        final RadioGroup roleRadioGroup = findViewById(R.id.role_radio_group);
        final RadioButton studentRadioButton = findViewById(R.id.student_radio_button);
        final RadioButton lecturerRadioButton = findViewById(R.id.lecturer_radio_button);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                String id = signupId.getText().toString();
                String phone = signupPhone.getText().toString();
                String role = "";

                int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
                if (selectedRoleId == R.id.student_radio_button) {
                    role = "Student";
                } else if (selectedRoleId == R.id.lecturer_radio_button) {
                    role = "Lecturer";
                } else {
                    Toast.makeText(SignupActivity.this, "Please select a role.", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyThread connectThread = new MyThread(name, email, password, id, phone, role, handler, getString(R.string.apikey));
                connectThread.start();
            }
        });
    }

    private class MyThread extends Thread{

        private String mName;
        private String mEmail;
        private String mPW;
        private String mID;
        private String mPhone;
        private String mRole;
        private Handler mHandler;
        private String mApiKey;

        public MyThread(String name, String email, String pw, String id, String phone, String role, Handler handler,String apiKey) {
            this.mName = name;
            this.mEmail = email;
            this.mPW = pw;
            this.mID = id;
            this.mPhone = phone;
            this.mRole = role;
            this.mHandler = handler;
            this.mApiKey = apiKey;
        }

        public void run(){

            try {
                URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Student");
                HttpURLConnection hc = (HttpURLConnection)url.openConnection();

                hc.setRequestMethod("POST");
                hc.setRequestProperty("apikey",mApiKey);
                hc.setRequestProperty("Authorization","Bearer "+mApiKey);
                hc.setRequestProperty("Content-Type","application/json");
                hc.setRequestProperty("Prefer","return=minimal");

                String requestBody = "{\"userID\": \""+mID+"\", \"password\": \""+mPW+"\", \"name\": \""+mName+"\",\"email\": \""+mEmail+"\",\"phone\": \""+mPhone+"\",\"role\": \""+mRole+"\"}";


                hc.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(hc.getOutputStream());
                os.writeBytes(requestBody);
                os.flush();
                os.close();

                if(hc.getResponseCode() == 201){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Your account is created successfully. Login now",Toast.LENGTH_SHORT).show();
                            goToLoginPage();
                        }
                    });
                }else{
                    Log.i("MainActivity2","Response code: "+hc.getResponseCode());
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
    public void goToLoginPage(){
        Intent intent =new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}