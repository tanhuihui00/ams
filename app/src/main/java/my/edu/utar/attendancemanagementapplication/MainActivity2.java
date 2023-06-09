package my.edu.utar.attendancemanagementapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String username = retrievePrefs.getString("username", "");
        String userRole = retrievePrefs.getString("role", "");
        Boolean loggedIn = retrievePrefs.getBoolean("loggedIn", false);

        if (username.equals("") || loggedIn == false){
            Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (userRole.equalsIgnoreCase("lecturer")){
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        Button viewAttendanceBtn = findViewById(R.id.view_attendance_button);
        viewAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, StudentViewAttendance.class);
                startActivity(intent);
            }
        });

        Button scanAttendanceBtn = findViewById(R.id.scan_attendance_button);
        scanAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, QRCodeScanner.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit application");
        builder.setMessage("Do you want to save your login details? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton("Log out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                getApplicationContext().getSharedPreferences("login_prefs", 0).edit().clear().commit();
                finish();
                finishAffinity();
                System.exit(0);
            }
        });
        builder.show();
    }
}