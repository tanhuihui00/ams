package my.edu.utar.attendancemanagementapplication;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);

        if (result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "QR Code Scan Cancelled.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QRCodeScanner.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }else{
                //get the session ID
                String sessionId = result.getContents();

                Intent intent = new Intent(QRCodeScanner.this, IdentityAuthentication.class);
                intent.putExtra("sessionId", sessionId);
                startActivity(intent);
                finish();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}