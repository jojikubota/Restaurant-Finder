package com.jojikubota.android.restaurantfinder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends SingleFragmentActivity {

//    private ListView mDrawerList;
//    private ArrayAdapter<String> mArrayAdapter;


    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Nagivation drawer setup
//        String[] drawerItems = { "Search", "Favorite" };
//        mDrawerList = (ListView) findViewById(R.id.navList_search);
//        mArrayAdapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_list_item_1, drawerItems);
//        mDrawerList.setAdapter(mArrayAdapter);
//    }
}
