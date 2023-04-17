package my.edu.utar.attendancemanagementapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QRCodeGenerator extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Spinner attendanceIdSpinner;
    private Button generateQrCodeButton;


    private static final String SUPABASE_URL = "https://wezvcdkmgwkuwlmmkklu.supabase.co/rest/v1/Attendances?select=attendanceID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);

        qrCodeImageView = findViewById(R.id.qr_code_image_view);
        //attendanceIdSpinner = findViewById(R.id.attendance_id_spinner);
        //generateQrCodeButton = findViewById(R.id.generate_qr_code_button);

        //retrieve the attendanceID from the database
        //new RetrieveAttendanceIdsTask().execute();

        String attendanceId = getIntent().getStringExtra("retrievedAttendanceID");

        // create an Intent to start the QRCodeScanner activity
        Intent intent = new Intent(QRCodeGenerator.this, QRCodeScanner.class);

        // add the attendanceId as an extra in the Intent
        intent.putExtra("attendanceId", attendanceId);

        // start the QRCodeScanner activity
        startActivity(intent);

        // generate the QR code
        Bitmap qrCode = generateQrCode(attendanceId);

        // display the QR code in the ImageView
        qrCodeImageView.setImageBitmap(qrCode);

        // show success dialog
        showSuccessDialog(attendanceId);

        /*

        generateQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String retrievedAttendanceID = getIntent().getStringExtra("retrievedAttendanceID");

                String attendanceId = retrievedAttendanceID;

                // generate the QR code
                Bitmap qrCode = generateQrCode(attendanceId);

                // display the QR code in the ImageView
                qrCodeImageView.setImageBitmap(qrCode);

                // show success dialog
                showSuccessDialog(attendanceId);
            }
        });*/

        /*
        // add an onItemSelectedListener to the Spinner
        attendanceIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String attendanceId = parent.getItemAtPosition(position).toString();
                Toast.makeText(QRCodeGenerator.this, "Selected attendance ID: " + attendanceId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
*/

    }
/*
    private class RetrieveAttendanceIdsTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void...voids) {
            List<String> attendanceIds = new ArrayList<>();

            try{
                URL url = new URL(SUPABASE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("apikey", getString(R.string.apikey));
                connection.setRequestProperty("Authorization", "Bearer " + getString(R.string.apikey));

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = null;

                    while ((line = reader.readLine())!= null) {
                        // Add the entire response string to the attendanceIds list
                        attendanceIds.add(line);
                    }
                    reader.close();
                }
                connection.disconnect();
        } catch(IOException e){
                e.printStackTrace();
            }

            return attendanceIds;
    }

        @Override
        protected void onPostExecute(List<String> attendanceIds) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(QRCodeGenerator.this, android.R.layout.simple_spinner_item, attendanceIds);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            attendanceIdSpinner.setAdapter(adapter);
        }
    }
    */

    private Bitmap generateQrCode(String attendanceId) {
        // create a QR code writer
        QRCodeWriter writer = new QRCodeWriter();

        try {
            // encode the attendance ID as a bit matrix
            BitMatrix bitMatrix = writer.encode(attendanceId, BarcodeFormat.QR_CODE, 512, 512);

            // convert the bit matrix to a bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showSuccessDialog(String attendanceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("QR code for attendance ID " + attendanceId + " has been generated successfully!")
                .setTitle("QR code generated")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss the dialog
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}



