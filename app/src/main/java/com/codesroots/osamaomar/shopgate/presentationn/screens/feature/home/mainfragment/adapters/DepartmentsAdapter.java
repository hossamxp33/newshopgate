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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.Category;
import com.codesroots.osamaomar.shopgate.entities.Subcat;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.subcategryfragment.SubcategryFragment;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.smallstore.smallStoreFramgent;

import java.util.List;

import static com.codesroots.osamaomar.shopgate.entities.names.CAT_ID;
import static com.codesroots.osamaomar.shopgate.entities.names.CAT_NAME;
import static com.codesroots.osamaomar.shopgate.entities.names.CAT_TYPE;

public class DepartmentsAdapter extends RecyclerView.Adapter<DepartmentsAdapter.ViewHolder>  {

    private Context context;
    private List<Category> categories;
    public  DepartmentsAdapter(Context mcontext, List<Category> categories1) {
        context = mcontext;
        categories = categories1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.depart_adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {


        Glide.with(context).load(categories.get(position).getPhoto()).dontAnimate().placeholder(R.drawable.dept1).into(holder.Image);
        holder.name.setText(categories.get(position).getName());

        Bundle bundle = new Bundle();
        bundle.putInt(CAT_ID,categories.get(position).getId());
        bundle.putString(CAT_NAME,categories.get(position).getName());
        Fragment sucCates_fragment = new SubcategryFragment();
        Fragment product_fragment = new smallStoreFramgent();
        sucCates_fragment.setArguments(bundle);
        product_fragment.setArguments(bundle);

        if (categories.size()>0)
        holder.mView.setOnClickListener(v ->
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().
                replace(R.id.mainfram, sucCates_fragment).addToBackStack(null).commit());
        else
        {
            bundle.putInt(CAT_TYPE,0);
            bundle.putInt(CAT_ID,categories.get(position).getId());
            holder.mView.setOnClickListener(v -> ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().
                    replace(R.id.mainfram, product_fragment).addToBackStack(null).commit());
        }


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        private ImageView Image;
        private TextView name;

        ViewHolder(View view) {
            super(view);
            mView = view;
            Image = mView.findViewById(R.id.item_img);
            name = mView.findViewById(R.id.name);
        }
    }
}