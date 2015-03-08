package com.codepath.punchcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.codepath.punchcard.R;
import com.codepath.punchcard.models.Shift;
import java.util.List;

/**
 * Created by harris on 3/7/15.
 */
public class ShiftAdapter<T> extends ArrayAdapter<Shift> {
  public ShiftAdapter(Context context, List<Shift> shifts) {
    super(context, android.R.layout.simple_list_item_1, shifts);
  }

  //@Override public View getView(int position, View convertView, ViewGroup parent) {
  //  Shift shift = getItem(position);
  //  if (convertView == null) {
  //    convertView = LayoutInflater.from(getContext()).inflate(R.layout.shift_cell, parent, true);
  //  }
  //
  //  return convertView;
  //}
}
