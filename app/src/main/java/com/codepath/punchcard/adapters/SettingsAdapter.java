package com.codepath.punchcard.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codepath.punchcard.R;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;
import java.util.Date;
import java.util.List;

/**
 * Created by harris on 3/7/15.
 */
public class SettingsAdapter<T> extends ArrayAdapter<Pair<String, Object>> {

    private final Context context;

    public SettingsAdapter(Context context, int resource, List<Pair<String, Object>> objects) {
      super(context, resource, objects);
      this.context = context;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    Pair<String, Object> item = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.setting_list_cell, parent, false);
    }

    TextView description = (TextView) convertView.findViewById(R.id.setting_description);
    TextView value = (TextView) convertView.findViewById(R.id.setting_value);
    if (item.first != null) {
      description.setVisibility(View.VISIBLE);
      description.setText(item.first);
    } else {
      description.setVisibility(View.GONE);
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)value.getLayoutParams();
      params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      value.setLayoutParams(params);
    }

    switch (position) {
      case 0:
        List<String> employeeNames = (List<String>) item.second;
        value.setText(employeeNames.toString());
        break;
      case 1:
        Date shiftDate = (Date) item.second;
        value.setText(DateHelper.formateDate(shiftDate));
        break;

      case 2:
        Date startTime = (Date) item.second;
        value.setText(DateHelper.formateTime(startTime));
        break;

      case 3:
        Date endTime = (Date) item.second;
        value.setText(DateHelper.formateTime(endTime));
        break;

    }

    return convertView;
  }


}
