package my.edu.utar.attendancemanagementapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences retrievePrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String username = retrievePrefs.getString("username", "");
        Boolean loggedIn = retrievePrefs.getBoolean("loggedIn", false);

        if (username.equals("") || loggedIn == false){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        Button createSessionButton = findViewById(R.id.create_session_button);
        createSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateSessionActivity.class);
                startActivity(intent);
            }
        });

        Button modifySessionButton = findViewById(R.id.modify_session_button);
        modifySessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AttendanceList.class);
                startActivity(intent);
            }
        });

        Button viewReportButton = findViewById(R.id.view_report_button);
        viewReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add code to launch the view report activity here
                Intent intent = new Intent(MainActivity.this, TeacherViewAttendance.class);
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
                finish();
                System.exit(0);
                finishAffinity();
            }
        });
        builder.setNegativeButton("Log out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getApplicationContext().getSharedPreferences("login_prefs", 0).edit().clear().commit();
                finish();
                System.exit(0);
                finishAffinity();

            }
        });
        builder.show();
    }
}
