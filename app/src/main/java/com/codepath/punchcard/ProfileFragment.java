package com.codepath.punchcard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.punchcard.fragments.HistoryShiftListFragment;
import com.codepath.punchcard.fragments.UpcomingShiftListFragment;
import com.codepath.punchcard.fragments.UpdateProfileFragment;
import com.codepath.punchcard.models.User;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment implements UpdateProfileFragment.OnFragmentInteractionListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int SELECT_IMAGE_REQUEST_CODE = 200;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivProfileImage;

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
        User user = (User) ParseUser.getCurrentUser();
        String username = user.getUsername();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        updateProfileInfoViews(username, firstName, lastName);

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setAdapter(profilePagerAdapter);
        tabStrip.setViewPager(pager);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), SELECT_IMAGE_REQUEST_CODE);
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
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            Uri selectedImageUrl = data.getData();
            String path = selectedImageUrl.getPath();
            ivProfileImage.setImageURI(selectedImageUrl);
        }
    }

    private void updateProfileInfoViews(String username, String firstName, String lastName) {
        tvName.setText(firstName + " " + lastName);
        tvEmail.setText(username);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

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
