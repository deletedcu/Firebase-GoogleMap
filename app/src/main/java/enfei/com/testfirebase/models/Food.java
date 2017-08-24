package enfei.com.testfirebase.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by king on 17/08/2017.
 */

public class Food extends Object {

    public String id;
    public String name;
    public double price;
    public Bitmap image;
    public String type;
    public String currencyType;

    public Food() {

    }

    public Food(String id, String name, String type, double price, String currencyType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.currencyType = currencyType;
    }

    public HashMap<String, Object> firebaseDetails() {
        HashMap<String, Object> result = new HashMap<>();
        if (!TextUtils.isEmpty(name))
            result.put("name", name);
        if (!TextUtils.isEmpty(type))
            result.put("type", type);
        if (!TextUtils.isEmpty(currencyType))
            result.put("currencyType", currencyType);
        result.put("price", price);
        result.put("id", id);

        return result;
    }

}
