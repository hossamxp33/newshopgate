package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainfragment.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.Offernew;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.ProductDetailsFragment;

import java.util.List;

import static com.codesroots.osamaomar.shopgate.entities.names.PRODUCT_ID;

public class MoreSalesProductsAdapter extends RecyclerView.Adapter<MoreSalesProductsAdapter.ViewHolder> {


    private Context context;
    List<Offernew> offerproducts;

    public MoreSalesProductsAdapter(Context context, List<Offernew> offers) {
        this.context = context;
        this.offerproducts = offers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_sales_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        try {
            //////////// Round Float Number //////////////
            String price = String.format("%.2f",Integer.valueOf((int) offerproducts.get(position).getProduct().getProductsizes().get(position).getCurrent_price()) *
                    PreferenceHelper.getCurrencyValue() );
            ////////////  //////////////

            holder.name.setText(offerproducts.get(position).getProduct().getName());
            holder.discount.setText(context.getText(R.string.disscount)+" "+offerproducts.get(position).getPercentage()+" "+"%");

            if (offerproducts.get(position).getProduct() != null) {
                    Glide.with(context.getApplicationContext())
                            .load(offerproducts.get(position).getProduct().getImg())
                            .placeholder(R.drawable.product)
                            .into(holder.item_img);
            }
            if (PreferenceHelper.getCurrencyValue() > 0)
                holder.price.setText(price + " " + PreferenceHelper.getCurrency());

            else
                holder.price.setText(offerproducts.get(position).getProduct().getProductsizes().get(position).getCurrent_price() + " " + context.getText(R.string.coin));


            //

//            if (productsbysales.get(position).getProduct().getTotal_rating() != null) {
//                if (productsbysales.get(position).getProduct().getTotal_rating().size() > 0)
//                    holder.ratingBar.setRating(productsbysales.get(position).getProduct().getTotal_rating().get(0).getStars() /
//                            productsbysales.get(position).getProduct().getTotal_rating().get(0).getCount());
//            }


//            holder.item_img.setOnClickListener(v ->
//                    {
//                        Intent intent = new Intent(context, ImageActivity.class);
//                        intent.putExtra("url", productsbysales.get(position).getProduct().img);
//                        context.startActivity(intent);
//                    }
//            );

            holder.mView.setOnClickListener(v ->
            {
                Fragment fragment = new ProductDetailsFragment();
                Bundle bundle = new Bundle();
                if (offerproducts.get(position).getProduct() != null)
                    bundle.putInt(PRODUCT_ID, offerproducts.get(position).getProduct_id());
                fragment.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().
                        replace(R.id.mainfram, fragment)
                        .addToBackStack(null).commit();
            });
//            holder.ratingBar.setOnTouchListener((v, event) -> {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    Intent intent = new Intent(context, RateActivity.class);
//                    intent.putExtra(PRODUCT_ID, offerproducts.get(position).getId());
//                    context.startActivity(intent);
//                }
//                return true;
//            });



        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        if (offerproducts != null) {
            return offerproducts.size();
        } else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private ImageView item_img;
        TextView price, name,discount;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            item_img = view.findViewById(R.id.item_img);
            name = view.findViewById(R.id.item_name);
            price = view.findViewById(R.id.price);
            ratingBar = view.findViewById(R.id.rates);
            discount = mView.findViewById(R.id.discount);
        }
    }

}
