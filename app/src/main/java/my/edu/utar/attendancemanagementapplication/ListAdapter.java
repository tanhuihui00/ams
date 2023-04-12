package my.edu.utar.attendancemanagementapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;



import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Students> {
    FragmentManager fragmentManager;
    private OnItemClick mCallback;
    public ListAdapter(Context context, ArrayList<Students>userArrayList, FragmentManager fragmentManager, OnItemClick listener){
        super(context,R.layout.list_item,userArrayList);
        this.fragmentManager=fragmentManager;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Students students=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        TextView name=convertView.findViewById(R.id.nametextView);
        TextView id=convertView.findViewById(R.id.idtextView);
        TextView status=convertView.findViewById(R.id.statustextView);
        ImageButton edit=convertView.findViewById(R.id.editbtn);

        name.setText(students.getName());
        id.setText(students.getId());
        status.setText(students.getStatus());
        edit.setOnClickListener(view -> {
            mCallback.onClick(String.valueOf(position));
            openDialog();

        });

        return convertView;
    }

    public void openDialog() {
        EditDialog dialog=new EditDialog();

        dialog.show( fragmentManager,"exampledialog");

    }

    public interface OnItemClick {
        void onClick (String value);
    }
}
