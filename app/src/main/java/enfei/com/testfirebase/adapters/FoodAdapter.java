package enfei.com.testfirebase.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import enfei.com.testfirebase.R;
import enfei.com.testfirebase.models.Food;
import enfei.com.testfirebase.services.FirebaseService;
import enfei.com.testfirebase.services.ImageResultListener;

/**
 * Created by king on 18/08/2017.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> dataList;
    private Context mContext;

    private int[] resourceList = new int[] {
            R.drawable.bg_dishes_1,
            R.drawable.bg_dishes_2,
            R.drawable.bg_dishes_3,
            R.drawable.bg_dishes_4,
            R.drawable.bg_starters_1,
            R.drawable.bg_starters_2,
            R.drawable.bg_starters_3,
            R.drawable.bg_drinks_1,
            R.drawable.bg_drinks_2,
            R.drawable.bg_drinks_3,
            R.drawable.bg_drinks_4
    };

    public FoodAdapter(Context context, List<Food> list) {
        super();
        this.mContext = context;
        this.dataList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Food item = dataList.get(position);
        holder.tvName.setText(item.name);
        holder.tvPrice.setText(String.format("%.0f%s", item.price, item.currencyType));

        Random random = new Random();
        int resourceId = random.nextInt(11);
        holder.imageView.setImageResource(resourceList[resourceId]);
//        if (item.image == null) {
//            holder.progressBar.setVisibility(View.VISIBLE);
//            String imagePath = "foods/" + item.id + "/photo";
//            FirebaseService.shared.downloadPhoto(imagePath, new ImageResultListener() {
//                @Override
//                public void onResult(boolean isSuccess, String error, Bitmap bitmap) {
//                    holder.progressBar.setVisibility(View.GONE);
//                    if (bitmap != null) {
//                        item.image = bitmap;
//                        holder.imageView.setImageBitmap(item.image);
//                    } else {
//                        holder.imageView.setImageResource(R.drawable.bg_dishes_1);
//                    }
//                }
//            });
//        } else {
//            holder.imageView.setImageBitmap(item.image);
//        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView tvName;
        public TextView tvPrice;
        public ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_food);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

}
