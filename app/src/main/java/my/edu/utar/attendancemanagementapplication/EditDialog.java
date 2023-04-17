package my.edu.utar.attendancemanagementapplication;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;




public class EditDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    String status;
    CustomDialogInterface customDialogInterface;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.edit_dialog,null);

        builder.setView(view).setTitle("Edit Status").setNegativeButton("Cancel", (dialogInterface, i) -> {

        }).setPositiveButton("Edit", (dialogInterface, i) -> {
            if(status!=null){
                customDialogInterface.applyText(status);
            }
        });

        spinner=view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity(),R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        status=adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        customDialogInterface = (CustomDialogInterface) context;
    }

    public interface CustomDialogInterface{
        void applyText(String status);
    }
}
