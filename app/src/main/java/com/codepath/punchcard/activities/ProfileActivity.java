package com.codepath.punchcard.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.punchcard.R;
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

public class ProfileActivity extends ActionBarActivity implements UpdateProfileFragment.OnFragmentInteractionListener {
    public static final int SELECT_IMAGE_REQUEST_CODE = 200;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivProfileImage;
    private Uri uriSavedImage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        View ivEdit = findViewById(R.id.ivEdit);

        final ParseUser parseUser = ParseUser.getCurrentUser();
        final ParseFile profileImageFile = parseUser.getParseFile(User.PROFILE_IMAGE);
        User user = (User) parseUser;
        String username = user.getUsername();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        updateProfileInfoViews(username, firstName, lastName);
        if (profileImageFile != null) {
            Picasso.with(this).load(profileImageFile.getUrl()).into(ivProfileImage);
        }

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(profilePagerAdapter);
        tabStrip.setViewPager(pager);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUpdateDialog();
            }
        });
    }

    private void launchCamera() {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        final boolean dir = imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image_001.jpg");
        uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, SELECT_IMAGE_REQUEST_CODE);
    }

    private void launchUpdateDialog() {
        UpdateProfileFragment updateProfileFragment = UpdateProfileFragment.newInstance();
        updateProfileFragment.setListener(this);
        updateProfileFragment.show(getSupportFragmentManager(), "Edit Dialog");
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onProfileUpdated(String firstName, String lastName, String username) {
        updateProfileInfoViews(username, firstName, lastName);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            ivProfileImage.setImageURI(uriSavedImage);

            final String msg = "Unable to upload profile image.";
            InputStream iStream;
            byte[] inputData = new byte[0];
            try {
                iStream = this.getContentResolver().openInputStream(uriSavedImage);
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
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void updateProfileInfoViews(String username, String firstName, String lastName) {
        tvName.setText(firstName + " " + lastName);
        tvEmail.setText(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                ParseUser.getCurrentUser().logOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}
