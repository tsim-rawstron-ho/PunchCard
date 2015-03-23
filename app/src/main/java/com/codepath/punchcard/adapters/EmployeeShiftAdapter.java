package com.codepath.punchcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import com.codepath.punchcard.R;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;

import java.util.List;

/**
 * Created by alvin on 3/22/15.
 */
public class EmployeeShiftAdapter extends ArrayAdapter<Shift> {

    private static class ViewHolder {
        TextView date;
        TextClock start;
        TextClock end;
    }


    public EmployeeShiftAdapter(Context context, List<Shift> shifts) {
        super(context, android.R.layout.simple_list_item_1, shifts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Shift shift = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_employee_shift, parent, false);
            viewHolder.start = (TextClock) convertView.findViewById(R.id.tcStart);
            viewHolder.end = (TextClock) convertView.findViewById(R.id.tcEnd);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText(DateHelper.formateDate(shift.getStartTime()));
        viewHolder.start.setText(shift.getStartTimeString());
        viewHolder.end.setText(shift.getEndTimeString());
        return convertView;
    }
}
