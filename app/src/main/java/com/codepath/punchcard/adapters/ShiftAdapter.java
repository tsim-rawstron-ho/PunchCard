package com.codepath.punchcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.punchcard.MainActivity;
import com.codepath.punchcard.R;
import com.codepath.punchcard.models.Shift;

import java.util.List;

/**
 * Created by harris on 3/7/15.
 */
public class ShiftAdapter<T> extends ArrayAdapter<Shift> {

    private final Context context;

    public ShiftAdapter(Context context, int resource, List<Shift> objects) {
      super(context, resource, objects);
      this.context = context;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    Shift shift = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.shift_list_cell, parent, false);
    }

      convertView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // KLUGE::
              ((MainActivity) context).startShift();
          }
      });

    return convertView;
  }
}
