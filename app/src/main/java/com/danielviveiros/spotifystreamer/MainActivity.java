package com.danielviveiros.spotifystreamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;


public class MainActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Spotify-related attributes
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "spotify-streamer-login://callback";
    private static final String CLIENT_ID = "e8adcbe86eb7453994fdd474bf780712";
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mAccessToken = prefs.getString(Constants.ACCESS_TOKEN_KEY, null);
        if ( mAccessToken == null ) {
            loginSpotify();
        }
    }

    /**
     * Login to Spotify
     */
    public void loginSpotify() {
        //Spotify login
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback with the token provided by spotify
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    mAccessToken = response.getAccessToken();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.ACCESS_TOKEN_KEY, mAccessToken);
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.e(LOG_TAG, "Error authenticating in Spotify: " + response.getCode() + ", "
                            + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.e(LOG_TAG, "Unexpected behavior authenticating in Spotify: " +
                            response.getCode() + ", " + response.getState() );
            }
        }
    }

    /**
     * Returns the Spotify access token
     */
    public String getSpotifyAccessToken() {
        return mAccessToken;
    }

    @Override
    public void onLoggedIn() {
        Log.d(LOG_TAG, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(LOG_TAG, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(LOG_TAG, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(LOG_TAG, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(LOG_TAG, "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(LOG_TAG, "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d(LOG_TAG, "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
