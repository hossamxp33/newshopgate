package com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codesroots.osamaomar.shopgate.R;
import com.codesroots.osamaomar.shopgate.entities.ProductDetails;
import com.codesroots.osamaomar.shopgate.entities.StoreSetting;
import com.codesroots.osamaomar.shopgate.helper.AddorRemoveCallbacks;
import com.codesroots.osamaomar.shopgate.helper.PreferenceHelper;
import com.codesroots.osamaomar.shopgate.helper.ResourceUtil;
import com.codesroots.osamaomar.shopgate.helper.kotlinusercase;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity.MainActivity;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.ProductImagesAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.ProductSizesAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.RelatedProductsAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.home.productdetailsfragment.adapters.SliderProductDetailsAdapter;
import com.codesroots.osamaomar.shopgate.presentationn.screens.feature.rate.RateActivity;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.codesroots.osamaomar.shopgate.entities.names.PRODUCT_ID;


public class ProductDetailsFragment extends Fragment {
    CirclePageIndicator indicator;
    ConstraintLayout product_view;
    ViewPager slider;
    Spinner spinner ,color_spinner;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ProductDetailsViewModel mViewModel;
    RecyclerView images_rec, sizes_rec,recommended_products;
    private int productid;
    private int sizeid,colorid;
    RelativeLayout textscroll;
    FrameLayout loading;
    public TextView product_name, description, price, ratecount, amount, addtocart, charege, oldprice;
    RatingBar ratingBar;
    public ImageView item_img;
    int userid = PreferenceHelper.getUserId(), favid = 0;
    ProductSizesAdapter productSizesAdapter;
    ProductImagesAdapter productImagesAdapter;
    ArrayList<String> images = new ArrayList<>();
    ImageView addToFav;
    Button share , hide_desc , show_desc;
    boolean productfav;
    public float priceafteroffer = 0;

    ProductDetails.ProductdetailsBean productdetails;
    public StoreSetting setting;
    public boolean freecharg = false;
    private String imagurl = "";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_details_fragment, container, false);
        //((MainActivity) getActivity()).head_title.setText(getText(R.string.product_details));
        ((MainActivity) getActivity()).logo.setVisibility(View.VISIBLE);
        productid = getArguments().getInt(PRODUCT_ID, 0);
        findViewsFromXml(view);

        mViewModel = ViewModelProviders.of(this, getViewModelFactory()).get(ProductDetailsViewModel.class);
        mViewModel.productDetailsMutableLiveData.observe(this,this::setDatainViews);
        mViewModel.productDetailsizeMutableLiveData.observe(this,this::setDataToViews);

        if (ResourceUtil.getCurrentLanguage(getActivity()).matches("en"))
            description.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
        description.setMovementMethod(new ScrollingMovementMethod());
        mViewModel.throwableMutableLiveData.observe(this, throwable ->
                Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show());


        share.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        });



        mViewModel.storeSettingMutableLiveData.observe(this, storeSetting ->
        {
            mViewModel.getData();
            setting = storeSetting;
            if (storeSetting.getData()!=null) {
                PreferenceHelper.setInOman(storeSetting.getData().get(0).getInoman());
                PreferenceHelper.setOutOman(storeSetting.getData().get(0).getOutoman());
                PreferenceHelper.setMinChipping(storeSetting.getData().get(0).getShippingPrice());
            }

        });
//



        mViewModel.productDetailsMutableLiveData.observe(this, productDetails ->
        {


            loading.setVisibility(View.GONE);
            if (productDetails.getProductdetails().size() > 0) {
                productdetails = productDetails.getProductdetails().get(0);
                if (productDetails.getProductdetails() != null)
                    setDataToViews(productDetails.getProductdetails().get(0));
            } else
                Toast.makeText(getActivity(), getActivity().getText(R.string.error_in_data), Toast.LENGTH_SHORT).show();
        });

        addtocart.setOnClickListener(v -> {
            if (userid > 0) {
                if (PreferenceHelper.retriveCartItemsValue() != null) {
                    if (!PreferenceHelper.retriveCartItemsValue().contains(sizeid)) {
                        PreferenceHelper.addItemtoCart(sizeid);
                        PreferenceHelper.addColorstoCart(colorid);

                        ((AddorRemoveCallbacks) getActivity()).onAddProduct();
                        Toast.makeText(getActivity(), getActivity().getText(R.string.addtocartsuccess), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getActivity(), getActivity().getText(R.string.aleady_exists), Toast.LENGTH_SHORT).show();
                } else {
                    PreferenceHelper.addItemtoCart(sizeid);
                    PreferenceHelper.addColorstoCart(colorid);

                    ((AddorRemoveCallbacks) getActivity()).onAddProduct();
                    Toast.makeText(getActivity(), getActivity().getText(R.string.addtocartsuccess), Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(getActivity(), getActivity().getText(R.string.loginfirst), Toast.LENGTH_SHORT).show();

        });

        addToFav.setOnClickListener(v ->
        {
            addToFav.setEnabled(false);
            if (!productfav) {
                mViewModel.AddToFav();
                productfav = true;
            } else {
                mViewModel.DeleteFav(userid, productid);
                productfav = false;
            }
        });

        mViewModel.addToFavMutableLiveData.observe(this, aBoolean ->
        {
            addToFav.setEnabled(true);
            addToFav.setImageResource(R.drawable.favoried);
        });

        mViewModel.deleteToFavMutableLiveData.observe(this, aBoolean ->
        {
            addToFav.setEnabled(true);
            addToFav.setImageResource(R.drawable.like);
        });

        mViewModel.throwablefav.observe(this, throwable ->
        {
            addToFav.setEnabled(false);
            Toast.makeText(getActivity(), getText(R.string.error_tryagani), Toast.LENGTH_SHORT).show();
        });

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Intent intent = new Intent(getActivity(), RateActivity.class);
                intent.putExtra(PRODUCT_ID, productid);
                getActivity().startActivity(intent);
            }
            return true;
        });


        return view;
    }

    private void findViewsFromXml(View view) {
        //     images_rec = view.findViewById(R.id.images_rec);
        //  sizes_rec = view.findViewById(R.id.sizes);
        loading = view.findViewById(R.id.progress);
        product_name = view.findViewById(R.id.product_name);
        textscroll  = view.findViewById(R.id.horizontalScrollView1);
        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);
        show_desc = view.findViewById(R.id.show);
        hide_desc = view.findViewById(R.id.hide);

        ratecount = view.findViewById(R.id.rate_count);
        ratingBar = view.findViewById(R.id.rates);
        item_img = view.findViewById(R.id.item_img);
        addToFav = view.findViewById(R.id.fav);
        amount = view.findViewById(R.id.amount);
        addtocart = view.findViewById(R.id.addtocart);
        //   charege = view.findViewById(R.id.charge);
        share = view.findViewById(R.id.share_button);
        product_view = view.findViewById(R.id.product_view);
        //   oldprice = view.findViewById(R.id.oldprice);
//        oldprice.setPaintFlags(oldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        slider = view.findViewById(R.id.sliderr);
        indicator = view.findViewById(R.id.indicatorProductDetails);
        recommended_products = view.findViewById(R.id.recommended_products);
        spinner = view.findViewById(R.id.planets_spinner);
       //   color_spinner = view.findViewById(R.id.color_spinner);
    }

    private void setDatainViews(ProductDetails productDetails) {
        recommended_products.setAdapter(new RelatedProductsAdapter(getActivity(),productDetails.
                getRelated()));

        if (productDetails.getProductdetails().size() > 0) {
            slider.setAdapter(new SliderProductDetailsAdapter(getActivity(), productDetails.getProductdetails().get(0).getProductphotos()));
            indicator.setViewPager(slider);
            init(productDetails.getProductdetails().get(0).getProductphotos().size());
            product_name.setText(productDetails.getProductdetails().get(0).getName());


        } else {
            slider.setVisibility(View.GONE);
        }



    }

    private void setDataToViews(@NotNull ProductDetails.ProductdetailsBean productdetailsBean) {
        loading.setVisibility(View.GONE);
        show_desc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Show button");
                show_desc.setVisibility(View.INVISIBLE);
                hide_desc.setVisibility(View.VISIBLE);
                description.setMaxLines(Integer.MAX_VALUE);

            }
        });

        hide_desc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("Hide button");
                hide_desc.setVisibility(View.INVISIBLE);
                show_desc.setVisibility(View.VISIBLE);
                description.setMaxLines(5);

            }
        });
        ////////////////// Price /////////////////
        String the_price = String.format("%.2f",Float.valueOf(productdetailsBean.
                getProductsizes().get(0).getCurrent_price() *
                PreferenceHelper.getCurrencyValue()) );

            if (productdetailsBean.getProductsizes().size() > 0) {
                amount.setText(productdetailsBean.getProductsizes().get(0).getAmount());
                price.setText(the_price  + PreferenceHelper.getCurrency());
            }else {
                Toast.makeText(getActivity(), getActivity().getText(R.string.error_in_data), Toast.LENGTH_SHORT).show();
            }
        ////////////////////////// Spinner /////////////////////////////////
        List values = new kotlinusercase().makestringarray(productdetailsBean.getProductsizes());
        //  String values = productdetailsBean.getProductsizes().get(0).getSize();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        adapter.notifyDataSetChanged();
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sizeid = productdetailsBean.getProductsizes().get(position).getId();
                //   if (position > 0 )
                if (productdetailsBean.getOffers().size() > 0) {
                    //hasOffer = true;
                    float offerPercentage = Float.valueOf(productdetailsBean.getProductsizes().get(position).getCurrent_price()) * productdetailsBean.getOffers().get(0).getPercentage() / 100;
                    String   priceafteroffer = String.format("%.2f",Float.valueOf(productdetailsBean.getProductsizes().get(position).getCurrent_price()) - offerPercentage);
                    if (PreferenceHelper.getCurrency()!=null)
                        price.setText(priceafteroffer + " " + PreferenceHelper.getCurrency());
                    else
                        price.setText(String.valueOf(the_price) + PreferenceHelper.getCurrency());


                } else {
                    price.setText(the_price + " " +PreferenceHelper.getCurrency());
                    if (Float.valueOf(productdetailsBean.getProductsizes().get(position).getCurrent_price()) < setting.getData().get(0).getShippingPrice()) {

                    }

                }

                amount.setText( String.valueOf(productdetailsBean.getProductsizes().get(position).getAmount())
                );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });
     //   textscroll.smoothScrollBy(0,0);
        ///////////////////////// COLOR SPINNER /////////////////
        ////////////////////////// Spinner /////////////////////////////////
      //  List colorvalues = new kotlinusercase().makestringarrayForColor(productdetailsBean.getProductcolor());
        //  String values = productdetailsBean.getProductsizes().get(0).getSize();
//        ArrayAdapter<String> Coloradapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,colorvalues);
//        Coloradapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        color_spinner.setAdapter(Coloradapter);

//        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        color_spinner.setAdapter(Coloradapter);
//        adapter.notifyDataSetChanged();
//        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                colorid = productdetailsBean.getProductcolor().get(position).getId();
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.setMovementMethod(new ScrollingMovementMethod());

            description.setText(Html.fromHtml(productdetailsBean.getDescription(),Html.FROM_HTML_MODE_COMPACT));
        }


        try {
            for (int i = 0; i < productdetailsBean.getProductphotos().size(); i++)
                images.add(productdetailsBean.getProductphotos().get(i).getPhoto());
            Glide.with(getActivity()).load(productdetailsBean.getImg())
                    .useAnimationPool(true).placeholder(R.drawable.product).into(item_img);
            imagurl = productdetailsBean.getImg();
            if (productdetailsBean.getOffers().size() > 0)
                productSizesAdapter = new ProductSizesAdapter(getActivity(), productdetailsBean.getProductsizes(),
                        this, productdetailsBean.getOffers().get(0).getPercentage());
            else

                productSizesAdapter = new ProductSizesAdapter(getActivity(), productdetailsBean.getProductsizes(), this, 0);
            productImagesAdapter = new ProductImagesAdapter(getActivity(), productdetailsBean.getProductphotos(), this);
            images_rec.setAdapter(productImagesAdapter);
            sizes_rec.setAdapter(productSizesAdapter);
            product_name.setText(productdetailsBean.getName());

            if (productdetailsBean.getOffers().size() > 0) {

                String priceafteroffer = String.format("%.4f", Float.valueOf(productdetailsBean.getProductsizes().
                        get(productSizesAdapter.mSelectedItem).getCurrent_price()) - Float.valueOf(productdetailsBean.getProductsizes()
                        .get(productSizesAdapter.mSelectedItem).getCurrent_price()) *
                        productdetailsBean.getOffers().get(0).getPercentage() / 100);
                if (PreferenceHelper.getCurrencyValue() > 0) {
                    price.setText(String.valueOf(priceafteroffer) + PreferenceHelper.getCurrency());
                    oldprice.setText(productdetailsBean.getProductsizes().
                            get(productSizesAdapter.mSelectedItem).getCurrent_price() + PreferenceHelper.getCurrency());
                } else {
                    price.setText(String.valueOf(priceafteroffer) + getText(R.string.realcoin));
                    oldprice.setText(productdetailsBean.getProductsizes().
                            get(productSizesAdapter.mSelectedItem).getCurrent_price() +""+ getText(R.string.realcoin));
                }
            } else
                price.setText(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getCurrent_price() +  PreferenceHelper.getCurrency());

            if (Float.valueOf(productdetailsBean.getProductsizes().get(productSizesAdapter.mSelectedItem).getCurrent_price()) <
                    setting.getData().get(0).getShippingPrice()) {
                if (PreferenceHelper.getCOUNTRY_ID()==1)
                    charege.setText(String.valueOf(PreferenceHelper.getIN_OMAN())+" "+getString(R.string.coin));
                else
                    charege.setText(String.valueOf(PreferenceHelper.getOUT_OMAN())+" "+getString(R.string.coin));
            }

            if (productdetailsBean.getTotal_rating() != null) {
                if (productdetailsBean.getTotal_rating().size() > 0) {
                    ratecount.setText("(" + productdetailsBean.getTotal_rating().get(0).getCount() + ")");
                    ratingBar.setRating(productdetailsBean.getTotal_rating().get(0).getStars() /
                            productdetailsBean.getTotal_rating().get(0).getCount());
                }
            }

            if (productdetailsBean.getFavourites().size() > 0) {
                addToFav.setImageResource(R.drawable.favoried);
                productfav = true;
                favid = productdetailsBean.getFavourites().get(0).getId();
            } else {
                addToFav.setImageResource(R.drawable.like);
                productfav = false;
            }
        }
        catch (Exception e)
        {}

    }

    public void setImageToImageView(String url) {
        Glide.with(getActivity()).load(url)
                .useAnimationPool(true).placeholder(R.drawable.product).into(item_img);
        imagurl = url;
    }


    private ProductDetailsModelFactory getViewModelFactory() {
        return new ProductDetailsModelFactory(this.getActivity().getApplication(), productid, userid);
    }

    private void init(int size) {
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(4 * density);
        NUM_PAGES = size;
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            slider.setCurrentItem(currentPage++, true);
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 4000, 5000);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int pos) {}
        });
    }

}
