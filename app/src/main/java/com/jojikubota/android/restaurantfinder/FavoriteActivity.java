package com.jojikubota.android.restaurantfinder;

import android.support.v4.app.Fragment;

/**
 * Created by joji on 3/18/16.
 */
public class FavoriteActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new FavoriteFragment();
    }
}
