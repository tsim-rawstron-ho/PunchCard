package com.codepath.punchcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import com.codepath.punchcard.R;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.Shift;

import com.codepath.punchcard.models.User;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by harris on 3/7/15.
 */
public class ShiftAdapter<T> extends ArrayAdapter<Shift> implements Shift.ShiftListener {

  private final Context context;
  private final Map<String, List<User>> shiftUsersMap;

  public ShiftAdapter(Context context, int resource, List<Shift> objects) {
      super(context, resource, objects);
      this.context = context;
      this.shiftUsersMap = new HashMap<String, List<User>>();
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    Shift shift = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.shift_list_cell, parent, false);
    }

    ImageView employImageView = (ImageView) convertView.findViewById(R.id.imageView);
    if (shiftUsersMap.containsKey(shift.getObjectId())) {
      List<User> users = shiftUsersMap.get(shift.getObjectId());
      for (User user : users) {
        final ParseFile profileImageFile = user.getParseFile(User.PROFILE_IMAGE);
        if (profileImageFile != null) {
          Picasso.with(getContext()).load(profileImageFile.getUrl())
              .resize(50, 50)
              .centerCrop().
          into(employImageView);
          break;
        }
      }
      TextView employeeNames = (TextView ) convertView.findViewById(R.id.shift_employees);
      employeeNames.setText(User.getEmployeeNames(users));
    } else {
      shift.getUsers(this);
    }

    TextView date = (TextView) convertView.findViewById(R.id.datetv);
    date.setText(DateHelper.formateShortDate(shift.getStartTime()));

    TextClock startTime = (TextClock) convertView.findViewById(R.id.start_time);
    TextClock endTime= (TextClock) convertView.findViewById(R.id.end_time);
    startTime.setText(shift.getStartTimeString());
    endTime.setText(shift.getEndTimeString());
    return convertView;
  }

  @Override public void usersFetched(Shift shift, List<User> users) {
    shiftUsersMap.put(shift.getObjectId(), users);
    notifyDataSetChanged();
  }
}
