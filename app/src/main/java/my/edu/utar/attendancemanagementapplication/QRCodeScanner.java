package my.edu.utar.attendancemanagementapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodeScanner extends AppCompatActivity {
    private final Activity activity = this;
    private TextView qrMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        qrMessageTextView = findViewById(R.id.message_textview);

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);

        if (result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "QR Code Scan Cancelled",Toast.LENGTH_SHORT).show();
            }else{
                //get the current time
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                //get the session ID
                String sessionId = result.getContents();

                //show the message with the current time
                String message = "Session ID " + sessionId + "\nScanned at: " + currentTime;
                //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                //update the TextView with the message and current time
                qrMessageTextView.setText(message);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}