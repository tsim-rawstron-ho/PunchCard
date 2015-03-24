package com.codepath.punchcard.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.punchcard.R;
import com.codepath.punchcard.models.User;
import com.gc.materialdesign.views.ButtonRectangle;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private EditText etUsername;
    private EditText etPasswordCurrent;
    private EditText etFirstName;
    private EditText etLastName;
    private Spinner spRole;
    private boolean changingPassword;
    private User user;
    private EditText etPasswordNew1;
    private EditText etPasswordNew2;

    public static UpdateProfileFragment newInstance() {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        getDialog().setTitle("Update Profile Info");
        getDialog().getWindow().getAttributes().windowAnimations = R.transition.bounce;
        getDialog().getWindow().setWindowAnimations(R.transition.bounce);


        user = (User) ParseUser.getCurrentUser();
        changingPassword = false;

        etUsername = (EditText) view.findViewById(R.id.etUsername);
        etPasswordCurrent = (EditText) view.findViewById(R.id.etPasswordCurrent);
        etPasswordNew1 = (EditText) view.findViewById(R.id.etPasswordNew1);
        etPasswordNew2 = (EditText) view.findViewById(R.id.etPasswordNew2);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        spRole = (Spinner) view.findViewById(R.id.spRole);
        final ButtonRectangle btnSave = (ButtonRectangle) view.findViewById(R.id.btnSave);
        final ButtonRectangle btnChangePw = (ButtonRectangle) view.findViewById(R.id.btnChangePw);
        final ButtonRectangle btnCancel = (ButtonRectangle) view.findViewById(R.id.btnCancel);

        etUsername.setText(user.getUsername());
        etFirstName.setText(user.getFirstName());
        etLastName.setText(user.getLastName());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.roles, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        btnChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingPassword = true;
                btnChangePw.setVisibility(View.GONE);
                etPasswordNew1.setVisibility(View.VISIBLE);
                etPasswordNew2.setVisibility(View.VISIBLE);
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword1 = etPasswordNew1.getText().toString();
                String newPassword2 = etPasswordNew2.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                
                // TODO(ASH): Validate we have the correct current-password.
                if (etPasswordCurrent.getText().toString().isEmpty()) {
                    showToast("You must enter your current password to update profile information.");
                    return;
                }

                if (changingPassword) {
                    if (newPassword1.isEmpty() || newPassword2.isEmpty()) {
                        showToast("Both the 'New Password' and 'Confirm Password' fields must be filled.");
                        return;
                    }

                    if (!newPassword1.equals(newPassword2)) {
                        showToast("New Password and Confirm Password fields must match.");
                        return;
                    }
                    currentUser.setPassword(newPassword1);
                }

                String username = etUsername.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                currentUser.setUsername(username);
                currentUser.put("firstName", firstName);
                currentUser.put("lastName", lastName);

                try {
                    currentUser.save();
                    mListener.onProfileUpdated(firstName, lastName, username);
                    getDialog().dismiss();
                } catch (ParseException e) {
                    showToast("Could not update user information.");
                }
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        selectedRole = (String) parent.getAdapter().getItem(position);
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onProfileUpdated(String firstName, String lastName, String username);
    }
}
