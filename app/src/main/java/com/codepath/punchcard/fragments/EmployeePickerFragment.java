package com.codepath.punchcard.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.codepath.punchcard.CreateNewShiftActivity;
import com.codepath.punchcard.R;
import com.codepath.punchcard.adapters.SettingsAdapter;
import com.codepath.punchcard.adapters.UsersAdapter;
import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class EmployeePickerFragment extends DialogFragment {
    public interface UserChosenListener {
      public void userChosen(List<User> users);
    }
    private ListView employList;
    private ArrayList<User> users;
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
      query.findInBackground(new FindCallback<User>() {
        @Override public void done(List<User> list, ParseException e) {
          usersAdapter.addAll(list);
        }
      });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_picker, container, false);
        getDialog().setTitle("Pick Employee for the shift");

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
        users = new ArrayList<>();
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
