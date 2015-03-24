package dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.punchcard.R;
import com.gc.materialdesign.views.ButtonRectangle;

/**
 * Created by alvin on 3/17/15.
 */
public class InviteEmployeeDialog extends DialogFragment {
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private OnInviteEmployee listener;

    public interface OnInviteEmployee {
        void inviteEmployee(String email, String firstName, String lastName);
    }

    public static InviteEmployeeDialog newInstance() {
        InviteEmployeeDialog frag = new InviteEmployeeDialog();
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnInviteEmployee) {
            listener = (OnInviteEmployee) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement OnInviteEmployee");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_employee, container);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        getDialog().setTitle("Add employee");
        // Show soft keyboard automatically
        etEmail.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ButtonRectangle btnInvite = (ButtonRectangle) view.findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.inviteEmployee(etEmail.getText().toString(),
                                        etFirstName.getText().toString(),
                                        etLastName.getText().toString());
                getDialog().dismiss();
            }
        });

        ButtonRectangle btnCancel = (ButtonRectangle) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }


}
