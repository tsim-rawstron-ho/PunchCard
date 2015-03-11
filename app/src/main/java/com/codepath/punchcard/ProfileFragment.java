package com.codepath.punchcard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.punchcard.fragments.HistoryShiftListFragment;
import com.codepath.punchcard.fragments.UpcomingShiftListFragment;
import com.codepath.punchcard.models.User;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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
        User user = (User) ParseUser.getCurrentUser();
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        TextView tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        tvName.setText(user.getFirstName() + " " + user.getLastName());
        tvEmail.setText(user.getUsername());

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setAdapter(profilePagerAdapter);
        tabStrip.setViewPager(pager);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
