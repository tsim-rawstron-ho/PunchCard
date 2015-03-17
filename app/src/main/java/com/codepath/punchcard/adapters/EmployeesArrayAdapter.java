package com.codepath.punchcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.punchcard.R;
import com.codepath.punchcard.models.User;

import java.util.List;

/**
 * Created by alvin on 3/17/15.
 */
public class EmployeesArrayAdapter extends ArrayAdapter<User> {

    private static class ViewHolder {
        ImageView profileImage;
        TextView email;
        TextView name;
    }
    public EmployeesArrayAdapter(Context context, List<User> users) {
        super(context, android.R.layout.simple_list_item_1, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_employee, parent, false);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.email = (TextView) convertView.findViewById(R.id.tvEmail);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(user.getFirstName() + " " + user.getLastName());
        viewHolder.email.setText(user.getUsername());
        return convertView;
    }
}
