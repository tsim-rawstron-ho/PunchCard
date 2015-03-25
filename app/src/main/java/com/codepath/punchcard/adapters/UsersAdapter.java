package com.codepath.punchcard.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codepath.punchcard.R;
import com.codepath.punchcard.helpers.DateHelper;
import com.codepath.punchcard.models.User;
import com.parse.ParseFile;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.Date;
import java.util.List;

/**
 * Created by harris on 3/7/15.
 */
public class UsersAdapter<T> extends ArrayAdapter<User> {

  public UsersAdapter(Context context, int resource, List<User> objects) {
    super(context, resource, objects);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    final User user = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list_cell, parent, false);
    }
    ImageView profilePic = (CircularImageView) convertView.findViewById(R.id.employee_pic);
    CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.employee_checked);
    TextView name = (TextView) convertView.findViewById(R.id.employ_name);

    final ParseFile profileImageFile = user.getParseFile(User.PROFILE_IMAGE);
    if (profileImageFile != null) {
      Picasso.with(getContext()).
          load(profileImageFile.getUrl()).resize(800, 800).centerCrop().into(profilePic);
    }
    name.setText(user.getFirstName() + " " + user.getLastName());
    checkbox.setChecked(user.isPicked());
    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        user.setPicked(isChecked);
      }
    });
    return convertView;
  }


}
