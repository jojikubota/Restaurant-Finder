package com.jojikubota.android.restaurantfinder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by joji on 3/16/16.
 */
public class Restaurant {

    private String mName;
    private String mImageUrl;
    private String mRatingUrl;
    private int mReviewCount;
    private ArrayList<String> mAddress;
    private String mPhone;
    private String mSnippet;
    private boolean mFavorite;
    private String  mLatitude;
    private String mLongitude;



    // For mName
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    // for mPhotoFile
    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    // For mRating
    public String getRatingUrl() {
        return mRatingUrl;
    }
    public void setRatingUrl(String rating) {
        mRatingUrl = rating;
    }

    // For mReviewCount
    public int getReviewCount() {
        return mReviewCount;
    }
    public void setReviewCount(int reviewCount) {
        mReviewCount = reviewCount;
    }

    // for mAddress
    public ArrayList<String>  getAddress() {
        return mAddress;
    }
    public void setAddress(ArrayList<String> address) {
        mAddress = address;
    }

    // For mPhone
    public String getPhone() {
        return mPhone;
    }
    public void setPhone(String phone) {
        mPhone = phone;
    }

    // for mSnippet
    public String getSnippet() {
        return mSnippet;
    }
    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }

    // for mFavorite
    public boolean isFavorite() {
        return mFavorite;
    }
    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    // mLatitude
    public String getLat() {
        return mLatitude;
    }
    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    // mLongitude
    public String getLong() {
        return mLongitude;
    }
    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }
}
