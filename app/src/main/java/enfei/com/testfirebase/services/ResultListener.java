package enfei.com.testfirebase.services;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

/**
 * Created by king on 18/08/2017.
 */

public interface ResultListener {
    public void onResult(boolean isSuccess, String error, List data);
}
