package com.jojikubota.android.restaurantfinder;
/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import android.content.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {

    OAuthService service;
    Token accessToken;

    public static Yelp getYelp(Context context) {
        return new Yelp(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret),
                context.getString(R.string.token), context.getString(R.string.token_secret));
    }

    /**
     * Setup the Yelp API OAuth credentials.
     * <p/>
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey    Consumer key
     * @param consumerSecret Consumer secret
     * @param token          Token
     * @param tokenSecret    Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    /**
     * Search with term string location.
     *
     * @param term     Search term
     * @param location Search location
     * @param category Search category
     * @param radius   Search radius
     * @param limit    Search limit
     * @param sort Search sort
     * @return JSON string response
     */
    public String search(String term, String location,
                         String category, String radius, String limit, String sort) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("category_filter", category);
        request.addQuerystringParameter("radius_filter", radius);
        request.addQuerystringParameter("limit", limit);
        request.addQuerystringParameter("sort", sort);

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }
}
