package my.edu.utar.attendancemanagementapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
}
