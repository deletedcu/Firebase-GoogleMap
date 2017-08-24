package enfei.com.testfirebase.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by king on 18/08/2017.
 */

public class MyLocation extends Object implements Parcelable {

    public double lat;
    public double lng;

    public MyLocation() {

    }

    public MyLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    protected MyLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>() {
        @Override
        public MyLocation createFromParcel(Parcel in) {
            return new MyLocation(in);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };
}
