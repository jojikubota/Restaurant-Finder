package com.jojikubota.android.restaurantfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by joji on 3/16/16.
 */
public class SearchFragment extends Fragment {

    // Variables
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;
    private TextView mLocationText;
    private ImageButton mLocationIcon;
    private ListView mDrawerList;
    private ArrayAdapter<String> mArrayAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private RecyclerView mSearchRecyclerView;
    private SearchAdapter mAdapter;
    private ActionBar mActionBar;
    private RestaurantList mRestaurantList;
    private ImageButton mSortButton;
    private Boolean isRelevance;

    // List View holder
    private class SearchHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Restaurant mRestaurant;
        private WebView mImageUrl;
        private TextView mBusinessNameTextView;
        private TextView mAddressTextView;
        private WebView mRatingUrl;

        public SearchHolder(View itemView) {
            super(itemView);
            // Create references
            mImageUrl = (WebView) itemView
                    .findViewById(R.id.list_search_image_url);
            mBusinessNameTextView = (TextView) itemView
                    .findViewById(R.id.list_search_name_text_view);
            mAddressTextView = (TextView) itemView
                    .findViewById(R.id.list_serach_address_text_view);
            mRatingUrl = (WebView) itemView
                    .findViewById(R.id.list_serach_rating_url);

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

    // List Adaptor
    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

        private List<Restaurant> mRestaurants;

        public SearchAdapter(List<Restaurant> restaurants) {
            mRestaurants = restaurants;
        }

        @Override
        public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_search, parent, false);
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchHolder holder, int position) {
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

        // Run Google places api
        mClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Set up the singleton (data container)
        mRestaurantList = RestaurantList.get(getContext());

        // Set the sort type to default = relevance
        isRelevance = true;
    }

    // Update List view / recycle view
    private void updateUI() {
        final List<Restaurant> restaurants = mRestaurantList.getRestaurants();
//        mAdapter = new SearchAdapter(restaurants);
//        mSearchRecyclerView.setAdapter(mAdapter);


        // Run async search
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mAdapter = new SearchAdapter(restaurants);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mSearchRecyclerView.setAdapter(mAdapter);
            }

        }.execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate main view.
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mSearchRecyclerView = (RecyclerView) v
                .findViewById(R.id.search_recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Add empty adaptor
        List<Restaurant> restaurants = mRestaurantList.getRestaurants();
        mAdapter = new SearchAdapter(restaurants);
        mSearchRecyclerView.setAdapter(mAdapter);

        // Create reference to location text
        mLocationText = (TextView) (v.findViewById(R.id.location_text));
        mLocationText.setText("San Francisco");

        // Location button
        mLocationIcon = (ImageButton) v.findViewById(R.id.menu_item_location);
        mLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        // Sort button
        mSortButton = (ImageButton) v.findViewById(R.id.menu_item_sort);
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                if (query == "") {
                    query = "sushi";
                }
                try {
                    runYelpSearch(query);
                    if (isRelevance) {
                        isRelevance = false;
                    } else {
                        isRelevance = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Setup navigation drawer
        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout_search);
        mDrawerList = (ListView) v.findViewById(R.id.navList_search);
        addDrawerItems();

        // Add Toggle switch for the navigation drawer
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navi_drawer);
        setupDrawer();

        // Fill search results with default values
        String query = QueryPreferences.getStoredQuery(getActivity());
        if (query == "") {
            query = "sushi";
        }
        try {
            runYelpSearch(query);
            if (isRelevance) {
                isRelevance = false;
            } else {
                isRelevance = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return v;
    }


    // Create Drawer Items
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

//    void updateSearchResult(SearchResponse searchResponse) {
//        // Find the singleton list
//        RestaurantList restaurantList = RestaurantList.get(getContext());
//        for (int i = 0; i < searchResponse.total(); i++) {
//            Restaurant restaurant = new Restaurant();
//            restaurant.setName(searchResponse.businesses().get(i).name());
//            restaurant.setRatingUrl(searchResponse.businesses().get(i).ratingImgUrl());
//            restaurant.setReviewCount(searchResponse.businesses().get(i).reviewCount());
//            restaurant.setAddress(searchResponse.businesses().get(i).location().displayAddress());
//            restaurant.setImageUrl(searchResponse.businesses().get(i).imageUrl());
//            restaurant.setPhone(searchResponse.businesses().get(i).phone());
//            restaurant.setSnippet(searchResponse.businesses().get(i).snippetText());
//
//            restaurantList.addRestaurant(restaurant);
//
//            System.out.println(restaurant);
//        }
//    }

    // Json Parser
    List<Restaurant> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");
        // Find the singleton list
        mRestaurantList.clearRestaurants();
        for (int i = 0; i < businesses.length(); i++) {
            Restaurant restaurant = new Restaurant();
            JSONObject business = businesses.getJSONObject(i);
            restaurant.setName((String) business.opt("name"));
            restaurant.setRatingUrl((String) business.opt("rating_img_url"));
            restaurant.setReviewCount((int) business.opt("review_count"));

            ArrayList<String> addrList = new ArrayList<String>();
            JSONArray jsonArray = (JSONArray) business.getJSONObject("location").opt("display_address");
            if (jsonArray != null) {
                for (int j = 0; j < jsonArray.length(); j++) {
                    addrList.add(jsonArray.get(j).toString());
                }
            }
            restaurant.setAddress(addrList);

            restaurant.setImageUrl((String) business.opt("image_url"));
            restaurant.setPhone((String) business.opt("phone"));
            restaurant.setSnippet((String) business.opt("snippet_text"));

            JSONObject jsonObject = (JSONObject)business.getJSONObject("location").opt("coordinate");
            restaurant.setLatitude((String) (jsonObject.opt("latitude")).toString());
            restaurant.setLongitude((String) (jsonObject.opt("longitude")).toString());

            mRestaurantList.addRestaurant(restaurant);
        }
        return mRestaurantList.getRestaurants();
    }

    private void runYelpSearch(String query) throws IOException {

        // Search param
        final String searchTerm = query;
        final String searchLocation = mLocationText.getText().toString();
        final String searchCategoryFilter = "restaurants";
        final String searchRadiusFilter = "16093.4";
        final String searchLimit = "20";
        String sortType;
        if (isRelevance) {
            sortType = "0";
        } else {
            sortType = "1";
        }
        final String searchSort = sortType;

        // Set the progress bar
        getActivity().setProgressBarIndeterminateVisibility(true);

        // Run async search
        new AsyncTask<Void, Void, List<Restaurant>>() {
            @Override
            protected List<Restaurant> doInBackground(Void... params) {
                String businesses = Yelp.getYelp(getActivity())
                        .search(searchTerm, searchLocation,
                                searchCategoryFilter, searchRadiusFilter,
                                searchLimit, searchSort);
                try {
                    return processJson(businesses);
                } catch (JSONException e) {
                    mRestaurantList.clearRestaurants();
                    return Collections.<Restaurant>emptyList();
                }
            }

            @Override
            protected void onPostExecute(List<Restaurant> restaurants) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                updateUI();
            }
        }.execute();

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

        // Inflate menu
        menuInflater.inflate(R.menu.fragment_search, menu);

        // Create reference for search view
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.expandActionView(searchItem);
        searchView.setIconifiedByDefault(true);
//        searchView.setMaxWidth(750);

        // Set listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Run yelp search
                try {
                    runYelpSearch(query);
                    if (isRelevance) {
                        isRelevance = false;
                    } else {
                        isRelevance = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                System.out.println("QueryTextChange: " + query);

                return false;
            }
        });

        // Put keyword from last search
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

    }

    // Start the location picker
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    // Stop the location picker
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    // Store picked location
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());

                String address = String.format("%s", place.getAddress());
                String delim = ",";
                String[] tokens = address.split(delim);
                String mCity = tokens[1];

                // Update the search location
                mLocationText.setText(mCity);

                String query = QueryPreferences.getStoredQuery(getActivity());
                try {
                    runYelpSearch(query);
                    if (isRelevance) {
                        isRelevance = false;
                    } else {
                        isRelevance = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
