package enfei.com.testfirebase.models;

import android.graphics.Bitmap;

/**
 * Created by king on 18/08/2017.
 */

public class CurrentUser {

    public static CurrentUser shared = new CurrentUser();

    public User user;

    public CurrentUser() {}

    public static void login(User user) {
        shared.user = user;
    }

    public static void logout() {
        shared.user = null;
    }

    public static void setProfileImage(Bitmap bitmap) {
        shared.user.image = bitmap;
    }

}
