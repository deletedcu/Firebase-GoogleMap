package enfei.com.testfirebase.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by king on 19/08/2017.
 */

public class PermissionUtils {

    private static String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    public static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkMandantoryPermissionsForApp(Context context) {

        return hasPermissions(context, PERMISSIONS);
    }

    public static void requestMandantoryPermissions(Activity activity, int requestCode) {

        ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode);
    }

    public static void requestPermissions(Activity activity, int requestCode, String... permissions) {

        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
