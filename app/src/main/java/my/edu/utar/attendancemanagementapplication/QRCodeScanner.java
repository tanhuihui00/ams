package my.edu.utar.attendancemanagementapplication;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
                Toast.makeText(this, "QR Code Scan Cancelled",Toast.LENGTH_SHORT).show();
            }else{
                //get the current date and time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                String attendanceId = getIntent().getStringExtra("attendanceId");

                try {
                    URL url = new URL("https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?select=subjectCode,type&attendanceID=eq"+ attendanceId);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    String output;
                    String subjectCode = "";
                    String type = "";
                    while ((output = br.readLine()) != null) {
                        JSONObject json = new JSONObject(output);
                        subjectCode = json.getString("subjectCode");
                        type = json.getString("type");
                    }
                    conn.disconnect();

                    //show the message with the current time, subject code, and type
                    String message = "Subject Code: " + subjectCode + "\nType: " + type + "\nYou have successfully scanned the attendance at: " + currentTime;
                    qrMessageTextView.setText(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}