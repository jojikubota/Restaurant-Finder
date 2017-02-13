package com.jojikubota.android.restaurantfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by joji on 3/19/16.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {

    // Variables
    private ArrayAdapter<String> mAdapter;
    private ImageButton mFavorite;
    private TextView mName;
    private WebView mRating;
    private TextView mReviewCount;
    private TextView mAddress;
    private TextView mPhone;
    private TextView mSnippet;
    private Restaurant mRestaurant;
    private Boolean isFav;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ActionBar mActionBar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstantceState) {
        super.onCreate(savedInstantceState);

        // Notify the option menu exits
        setHasOptionsMenu(true);

        // Turn on the up button
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setIcon(R.drawable.ic_up_button);

        // Retrieve extra
        String name = (String) getActivity().getIntent()
                .getSerializableExtra(DetailActivity.EXTRA_RESTAURANT_NAME);

        // Initialize .this restaurant
        mRestaurant = RestaurantList.get(getActivity()).getRestaurant(name);

        // Turn on google map option
        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);

        // SharedPreferences
        mSharedPreferences = getActivity()
                .getSharedPreferences("RestaurantFinder", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        // Set favorite flag
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(mRestaurant.getPhone(), "");
        if (!json.equals("")) {
            isFav = true;
            mRestaurant.setFavorite(true);
        } else {
            isFav = false;
            mRestaurant.setFavorite(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate main view.
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        // References to widgets
        mFavorite = (ImageButton) v.findViewById(R.id.favorite);
        if (isFav) {
            mFavorite.setImageResource(R.drawable.red);
        } else {
            mFavorite.setImageResource(R.drawable.black);
        }
        // Save favorites using gson / SharedPreferences
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFav) {
                    mFavorite.setImageResource(R.drawable.black);
                    mRestaurant.setFavorite(false);
                    isFav = false;
                    // Delete from the favorite list
                    mEditor.remove(mRestaurant.getPhone().toString()).commit();
                } else {
                    mFavorite.setImageResource(R.drawable.red);
                    mRestaurant.setFavorite(true);
                    isFav = true;
                    // Add to the favorite list
                    Gson gson = new Gson();
                    String json = gson.toJson(mRestaurant);
                    mEditor.putString(mRestaurant.getPhone().toString(), json);
                    mEditor.commit();
                }

                Gson testGson = new Gson();
                String testJson = mSharedPreferences.getString(mRestaurant.getPhone(), "");
                Restaurant r = testGson.fromJson(testJson, Restaurant.class);
                if (r != null) {
                    System.out.println(r.getName());
                } else {
                    System.out.println("no data");
                }
            }
        });
        mName = (TextView) v.findViewById(R.id.name);
        mName.setText(mRestaurant.getName());
        mRating = (WebView) v.findViewById(R.id.rating);
        mRating.loadUrl(mRestaurant.getRatingUrl());
        mReviewCount = (TextView) v.findViewById(R.id.review_count);
        mReviewCount.setText(Integer.toString(mRestaurant.getReviewCount()));
        mAddress = (TextView) v.findViewById(R.id.address);
        String fullAddress = "";
        for (String address : mRestaurant.getAddress()) {
            if (!fullAddress.equals("")) {
                fullAddress += " ";
            }
            fullAddress += address;
        }
        mAddress.setText(fullAddress);
        mPhone = (TextView) v.findViewById(R.id.phone);
        mPhone.setText(mRestaurant.getPhone());
        mSnippet = (TextView) v.findViewById(R.id.snippet);
        mSnippet.setText(mRestaurant.getSnippet());

        // Get the map and register for the read callback
//        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
//                                                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Move the camera to center
    public void showRestaurant(View v) {
        // Wait until map is ready
        if (mMap == null) {
            return;
        }
        // Center camera on location
        Double lat = Double.parseDouble(mRestaurant.getLat());
        Double lon = Double.parseDouble(mRestaurant.getLong());
        LatLng latLng= new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
    }

    // Called when the map is read to add objects to the map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Marker
        Double lat = Double.parseDouble(mRestaurant.getLat());
        Double lon = Double.parseDouble(mRestaurant.getLong());
        LatLng latLng= new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng));

        // Map
        mMap = googleMap;

        final View mapView = this.getChildFragmentManager()
                .findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    showRestaurant(null);
                }
            });
        }
    }
}

