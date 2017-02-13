package com.jojikubota.android.restaurantfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by joji on 3/20/16.
 */
public class DetailActivity extends SingleFragmentActivity {

    public static final String EXTRA_RESTAURANT_NAME =
            "com.jojikubota.android.restaurantfinder.restaurant_name";

    public static Intent newIntent(Context packageContext, String name) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(EXTRA_RESTAURANT_NAME, name);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new DetailFragment();
    }
}
