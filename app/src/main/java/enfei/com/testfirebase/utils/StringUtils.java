package enfei.com.testfirebase.utils;

/**
 * Created by king on 19/08/2017.
 */

public class StringUtils {

    public static boolean isNotEmpty(String s) {
        if (s != null && s.trim().length() > 0 && !s.equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }

    public static boolean isEmpty(String s) {
        return !isNotEmpty(s);
    }

}
