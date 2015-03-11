package com.codepath.punchcard.helpers;

import com.codepath.punchcard.models.Shift;
import com.codepath.punchcard.models.User;
import com.codepath.punchcard.models.UsersShift;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by harris on 3/10/15.
 */
public class FixtureHelper {
  public static void setupFixtureData() {
      ParseQuery<Shift> query = ParseQuery.getQuery("Shift");
      query.findInBackground(new FindCallback<Shift>() {
        public void done(final List<Shift> allShifts, ParseException e) {
          ParseQuery<UsersShift> query = ParseQuery.getQuery("UsersShift");
          query.findInBackground(new FindCallback<UsersShift>() {
            public void done(final List<UsersShift> allUserShifts, ParseException e) {
              try {
                //deleteAllShiftsAndSignups(allUserShifts, allShifts);
                createNewShiftsAndSignups("speed.mating@gmail.com");
                //createNewShiftsAndSignups("alvinzho@gmail.com");
                //createNewShiftsAndSignups("ash@edmodo.com");
              } catch (ParseException e1) {
                e1.printStackTrace();
              }
            }
          });
        }
      });

  }

  private static void createNewShiftsAndSignups(String name) throws ParseException {
    ParseQuery<User> query = ParseQuery.getQuery("_User");
    query.whereEqualTo("username", name);
    query.findInBackground(new FindCallback<User>() {
      @Override public void done(List<User> allUsers, ParseException e) {
        for (User user : allUsers) {
          Shift shift = new Shift();
          shift.setStartTime(new Date());
          shift.setEndTime(new Date());          
          try {
            shift.save();
          } catch (ParseException e1) {
            e1.printStackTrace();
          }
          UsersShift usersShift = new UsersShift();
          usersShift.setShift(shift);
          usersShift.setUser(user);
          try {
            usersShift.save();
          } catch (ParseException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
  }

  private static void deleteAllShiftsAndSignups(List<UsersShift> allUserShifts,
      List<Shift> allShifts) throws ParseException {
    for (UsersShift usersShift : allUserShifts) {
      usersShift.delete();
    }
    for (Shift shift : allShifts) {
      shift.delete();
    }
  }
}
