package com.jojikubota.android.restaurantfinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joji on 3/18/16.
 */

// Singleton
public class RestaurantList {
    // Create static variable
    private static RestaurantList sRestaurantList;

    private List<Restaurant> mRestaurants;

    // Public constructor
    public static RestaurantList get(Context context) {
        if (sRestaurantList == null) {
            sRestaurantList = new RestaurantList(context);
        }

        return sRestaurantList;
    }

    // Constructor
    private RestaurantList(Context context) {
        mRestaurants = new ArrayList<>();
    }

    // Getter
    public List<Restaurant> getRestaurants() {
        return mRestaurants;
    }

    // Setter
    public void addRestaurant(Restaurant restaurant) {
        mRestaurants.add(restaurant);
    }

    // Find Restaurant
    public Restaurant getRestaurant(String name) {
        for (Restaurant restaurant : mRestaurants) {
            if (restaurant.getName().equals(name)) {
                return restaurant;
            }
        }
        return null;
    }

    // Clear Restaurant List
    public void clearRestaurants() {
        mRestaurants.clear();
    }
}
