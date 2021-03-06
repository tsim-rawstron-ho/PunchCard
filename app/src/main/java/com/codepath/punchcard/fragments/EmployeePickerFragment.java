package com.codepath.punchcard.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.codepath.punchcard.activities.CreateNewShiftActivity;
import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.UsersAdapter;
import com.codepath.punchcard.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class EmployeePickerFragment extends DialogFragment {
  private List<User> pickedUsers;

  public interface UserChosenListener {
      public void userChosen(List<User> users);
    }
    private ListView employList;
    private List<User> users;
    private UsersAdapter<User> usersAdapter;
    private UserChosenListener listener;

  public static EmployeePickerFragment newInstance() {
        EmployeePickerFragment fragment = new EmployeePickerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EmployeePickerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      ParseQuery<User> query = ParseQuery.getQuery("_User");
      query.addAscendingOrder("firstName");
      query.whereEqualTo("company", ((User)ParseUser.getCurrentUser()).getCompany());
      query.findInBackground(new FindCallback<User>() {
        @Override public void done(List<User> list, ParseException e) {
          for (User user : list) {
            if (pickedUsers.contains(user)) {
              user.setPicked(true);
            }
          }
          usersAdapter.addAll(list);
        }
      });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_employee_picker, container, false);
      Dialog dialog = getDialog();
      dialog.setTitle("Pick Employee for the shift");
      dialog.getWindow().setBackgroundDrawable(
          new ColorDrawable(android.graphics.Color.TRANSPARENT));
      dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

      employList = (ListView) view.findViewById(R.id.employee_list);
        final Button btnChhose = (Button) view.findViewById(R.id.btn_choose);
        btnChhose.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            List<User> chosen = new ArrayList<User>();
            for (User user : users) {
              if (user.isPicked()) {
                chosen.add(user);
              }
            }
            listener.userChosen(chosen);
            dismiss();
          }
        });
        users = new ArrayList<User>();
        pickedUsers = ((CreateNewShiftActivity)getActivity()).getPickedUsers();
        usersAdapter = new UsersAdapter<User>(getActivity(), android.R.layout.simple_list_item_1,
            users);
        employList.setAdapter(usersAdapter);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

  public void setListener(UserChosenListener listener) {
    this.listener = listener;
  }
}
