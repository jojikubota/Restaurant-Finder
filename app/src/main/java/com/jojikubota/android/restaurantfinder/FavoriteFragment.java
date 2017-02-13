package com.jojikubota.android.restaurantfinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by joji on 3/18/16.
 */
public class FavoriteFragment extends Fragment {

    private ListView mDrawerList;
    private ArrayAdapter<String> mArrayAdapter;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mFavoriteRecyclerView;
    private FavoriteAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    // View holder
    private class FavoriteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Restaurant mRestaurant;
        private WebView mImageUrl;
        private TextView mBusinessNameTextView;
        private TextView mAddressTextView;
        private WebView mRatingUrl;

        public FavoriteHolder(View itemView) {
            super(itemView);
            // Create reference
            mImageUrl = (WebView) itemView
                    .findViewById(R.id.list_favorite_image_url);
            mBusinessNameTextView = (TextView) itemView
                    .findViewById(R.id.list_favorite_name_text_view);
            mAddressTextView = (TextView) itemView
                    .findViewById(R.id.list_favorite_address_text_view);
            mRatingUrl = (WebView) itemView
                    .findViewById(R.id.list_favorite_rating_url);

            // Listener
            itemView.setOnClickListener(this);
        }

        // Click Handler for the list element
        @Override
        public void onClick(View v) {

            // Call Detail activity
            Intent intent = DetailActivity.newIntent(getActivity(), mRestaurant.getName());
            startActivity(intent);
        }

        public void bindRestaurant(Restaurant restaurant) {
            mRestaurant = restaurant;
            mImageUrl.loadUrl(restaurant.getImageUrl());
            mBusinessNameTextView.setText(mRestaurant.getName());
            ArrayList<String> addressArray = mRestaurant.getAddress();
            String singleAddress = "";
            for (String address : addressArray) {
                if (!singleAddress.equals("")) {
                    singleAddress += " ";
                }
                singleAddress += address;
            }
            mAddressTextView.setText(singleAddress);
            mRatingUrl.loadUrl(restaurant.getRatingUrl());
        }
    }

    // Adapter
    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteHolder> {

        private List<Restaurant> mRestaurants;

        public FavoriteAdapter(List<Restaurant> restaurants) {
            mRestaurants = restaurants;
        }

        @Override
        public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_favorite, parent, false);
            return new FavoriteHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoriteHolder holder, int position) {
            Restaurant restaurant = mRestaurants.get(position);
            holder.bindRestaurant(restaurant);
        }

        @Override
        public int getItemCount() {
            return mRestaurants.size();
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the option menu exits
        setHasOptionsMenu(true);

        // Access SharedPreferences
        mSharedPreferences = getActivity().getSharedPreferences(
                            "RestaurantFinder", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

    }

    // Update List view / recycle view
    private void updateUI() {
        // Check SharedPreferences for saved data
        final List<Restaurant> favRestaurants = new ArrayList<>();
        Map<String, ?> map = mSharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Gson gson = new Gson();
            String json = mSharedPreferences.getString(key, "");
            Restaurant restaurant = gson.fromJson(json, Restaurant.class);

            favRestaurants.add(restaurant);
        }

//        RestaurantList restaurantList = RestaurantList.get(getActivity());
//        List<Restaurant> restaurants = restaurantList.getRestaurants();
//
//        List<Restaurant> favRestaurants = new ArrayList<>();
//        for (Restaurant r : restaurants) {
//            if (r.isFavorite()) {
//                favRestaurants.add(r);
//            }
//        }


        // Run async search
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mAdapter = new FavoriteAdapter(favRestaurants);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mFavoriteRecyclerView.setAdapter(mAdapter);
            }

        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate main view.
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mFavoriteRecyclerView = (RecyclerView) view
                .findViewById(R.id.favorite_recycle_view);
        mFavoriteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Add empty adaptor
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        mAdapter = new FavoriteAdapter(restaurants);
        mFavoriteRecyclerView.setAdapter(mAdapter);

        // Setup navigation drawer
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout_favorite);
        mDrawerList = (ListView) view.findViewById(R.id.navList_favorite);
        addDrawerItems();

        // Add Toggle switch for the navigation drawer
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navi_drawer);
        mActionBar.setTitle("Favorite");
        setupDrawer();

        updateUI();

        return view;
    }



    // Add Drawer Items
    private void addDrawerItems() {
        String[] drawerItems = { "Search", "Favorite" };
        mArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, drawerItems);
        mDrawerList.setAdapter(mArrayAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if (position == 0) {
                    intent = new Intent(getActivity(), SearchActivity.class);
                } else {
                    intent = new Intent(getActivity(), FavoriteActivity.class);
                }

                startActivity(intent);
                mDrawerLayout.closeDrawers();
            }
        });
    }

    // Setup navigation drawer toggling
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                mActionBar.setTitle("Navigation");
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                mActionBar.setTitle("mActivityTitle");
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }
}