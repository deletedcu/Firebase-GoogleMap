package enfei.com.testfirebase.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by king on 18/08/2017.
 */

public class Restaurant extends Object implements Parcelable {

    public String id;
    public String name;
    public String subTitle;
    public String description;
    public MyLocation location;
    public String address;
    public int price_level;
    public String place_id;
    public double rating;
    public List<String> photos;

    public Restaurant() {

    }

    public Restaurant(String name, String subTitle, String description, String address, MyLocation location) {
        this.name = name;
        this.subTitle = subTitle;
        this.description = description;
        this.address = address;
        this.location = location;
    }

    public Restaurant(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("name");
            double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            location = new MyLocation(lat, lng);
            place_id = jsonObject.getString("place_id");
            price_level = jsonObject.getInt("price_level");
            rating = jsonObject.getDouble("rating");
            address = jsonObject.getString("vicinity");
            JSONArray jsonArray = jsonObject.getJSONArray("types");
            subTitle = "";
            for (int i = 0; i < jsonArray.length(); i ++) {
                subTitle += jsonArray.get(i).toString() + " & ";
            }
            if (subTitle.length() > 0)
                subTitle = subTitle.substring(0, subTitle.length() - 3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        subTitle = in.readString();
        description = in.readString();
        location = in.readParcelable(MyLocation.class.getClassLoader());
        address = in.readString();
        price_level = in.readInt();
        place_id = in.readString();
        rating = in.readDouble();
        photos = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(subTitle);
        dest.writeString(description);
        dest.writeParcelable(location, flags);
        dest.writeString(address);
        dest.writeInt(price_level);
        dest.writeString(place_id);
        dest.writeDouble(rating);
        dest.writeStringList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public HashMap<String, Object> firebaseDetails() {
        HashMap<String, Object> result = new HashMap<>();
        if (!TextUtils.isEmpty(name))
            result.put("name", name);
        if (!TextUtils.isEmpty(subTitle))
            result.put("subTitle", subTitle);
        if (!TextUtils.isEmpty(description))
            result.put("description", description);
        if (location != null)
            result.put("location", location);
        if (!TextUtils.isEmpty(address))
            result.put("address", address);
        result.put("id", id);
        result.put("place_id", place_id);
        result.put("rating", rating);
        result.put("price_level", price_level);
        if (photos != null && !photos.isEmpty())
            result.put("photos", photos);

        return result;
    }

}
