package com.codepath.punchcard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.punchcard.fragments.HistoryShiftListFragment;
import com.codepath.punchcard.fragments.UpcomingShiftListFragment;
import com.codepath.punchcard.fragments.UpdateProfileFragment;
import com.codepath.punchcard.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment implements UpdateProfileFragment.OnFragmentInteractionListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int SELECT_IMAGE_REQUEST_CODE = 200;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivProfileImage;
    private Uri uriSavedImage;

    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        View ivEdit = v.findViewById(R.id.ivEdit);

        final ParseUser parseUser = ParseUser.getCurrentUser();
        final ParseFile profileImageFile = parseUser.getParseFile(User.PROFILE_IMAGE);
        User user = (User) parseUser;
        String username = user.getUsername();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        updateProfileInfoViews(username, firstName, lastName);
        if (profileImageFile != null) {
            Picasso.with(getActivity()).load(profileImageFile.getUrl()).into(ivProfileImage);
        }

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setAdapter(profilePagerAdapter);
        tabStrip.setViewPager(pager);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                final boolean dir = imagesFolder.mkdirs();// <----
                File image = new File(imagesFolder, "image_001.jpg");
                uriSavedImage = Uri.fromFile(image);

                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, SELECT_IMAGE_REQUEST_CODE);
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileFragment updateProfileFragment = UpdateProfileFragment.newInstance();
                updateProfileFragment.setListener(ProfileFragment.this);
                updateProfileFragment.show(getFragmentManager(), "Edit Dialog");
            }
        });
        return v;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            ivProfileImage.setImageURI(uriSavedImage);

            final String msg = "Unable to upload profile image.";
            InputStream iStream;
            byte[] inputData = new byte[0];
            try {
                iStream = getActivity().getContentResolver().openInputStream(uriSavedImage);
                inputData = getBytes(iStream);
            } catch (IOException e) {
                showToast(msg);
                e.printStackTrace();
            }

            ParseFile file = new ParseFile("profile.png", inputData);
            final ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put(User.PROFILE_IMAGE, file);
            try {
                currentUser.save();
            } catch (com.parse.ParseException e) {
                showToast(msg);
                e.printStackTrace();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void updateProfileInfoViews(String username, String firstName, String lastName) {
        tvName.setText(firstName + " " + lastName);
        tvEmail.setText(username);
    }

    //@Override
    //public void onAttach(Activity activity) {
    //    super.onAttach(activity);
    //    ((MainActivity) activity).onSectionAttached(
    //            getArguments().getInt(ARG_SECTION_NUMBER));
    //}

    @Override
    public void onProfileUpdated(String firstName, String lastName, String username) {
        updateProfileInfoViews(username, firstName, lastName);
    }

    public class ProfilePagerAdapter extends FragmentPagerAdapter {
        private String[] tabTitles = {"Upcoming", "History"};
        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return UpcomingShiftListFragment.newInstance((User) ParseUser.getCurrentUser());
            } else if (position == 1) {
                return HistoryShiftListFragment.newInstance((User) ParseUser.getCurrentUser());
            } else {
                return null;
            }
        }
        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
