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

public class ListAdapterAttendance extends ArrayAdapter<AttendanceClass> {
    public ListAdapterAttendance(Context context, ArrayList<AttendanceClass> classlist){
        super(context,R.layout.student_list_item,classlist);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttendanceClass attendance=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.attendance_item,parent,false);
        }


        TextView datetime =convertView.findViewById(R.id.datetime);
        TextView status=convertView.findViewById(R.id.statustxt);
        datetime.setText(attendance.getDate()+" "+attendance.getTime());
        status.setText(attendance.getStatus());

        return convertView;
    }
}
