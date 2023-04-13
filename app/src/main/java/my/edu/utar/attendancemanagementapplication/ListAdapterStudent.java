package my.edu.utar.attendancemanagementapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.ArrayList;

public class ListAdapterStudent extends ArrayAdapter<StudentOverallAttendance> {
    public ListAdapterStudent(Context context, ArrayList<StudentOverallAttendance> userArrayList){
        super(context,R.layout.student_list_item,userArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StudentOverallAttendance attendance=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.student_list_item,parent,false);
        }

        TextView classcode=convertView.findViewById(R.id.codetext);
        TextView count=convertView.findViewById(R.id.count);
        TextView type=convertView.findViewById(R.id.type);
        classcode.setText(attendance.getClasscode());
        type.setText(attendance.getType());
        count.setText("Attendance Counted: "+attendance.getCount());

        return convertView;
    }
}
