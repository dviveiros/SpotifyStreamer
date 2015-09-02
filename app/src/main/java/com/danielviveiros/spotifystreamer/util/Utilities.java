package com.danielviveiros.spotifystreamer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Utility class for Spotify Streamer
 *
 * Created by dviveiros on 11/08/15.
 */
public class Utilities {

    /**
     * Gets the smaller image to use as artist icon
     */
    public static String getSmallerImage(List<Image> images) {

        Image artistImage = null;

        if ((images != null) && (images.size() > 0)) {
            for ( Image image: images ) {
                if (artistImage == null) {
                    artistImage = image;
                }
                if (image.height < artistImage.height) {
                    artistImage = image;
                }
            }
            return artistImage.url;
        } else {
            return Constants.DEFAULT_IMAGE;
        }
    }

    /**
     * Gets the larger image to use as artist icon
     */
    public static String getLargerImage(List<Image> images) {

        Image artistImage = null;

        if ((images != null) && (images.size() > 0)) {
            for ( Image image: images ) {
                if (artistImage == null) {
                    artistImage = image;
                }
                if (image.height > artistImage
                        .height) {
                    artistImage = image;
                }
            }
            return artistImage.url;
        } else {
            return Constants.DEFAULT_IMAGE;
        }
    }

    /**
     * Check if there is internet connection
     */
    public static boolean isOnline( Context context ) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
