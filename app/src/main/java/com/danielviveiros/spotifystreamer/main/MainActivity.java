package com.danielviveiros.spotifystreamer.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.danielviveiros.spotifystreamer.R;
import com.danielviveiros.spotifystreamer.artist.ArtistFilterFragment;
import com.danielviveiros.spotifystreamer.track.TopTracksActivity;
import com.danielviveiros.spotifystreamer.track.TopTracksFragment;
import com.danielviveiros.spotifystreamer.util.Constants;


public class MainActivity extends AppCompatActivity
        implements ArtistFilterFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TOP_TRACKS_FRAGMENT_TAG = "TTTAG";

    //one or two pane?
    private boolean mTwoPane;

    //Top tracks fragment
    private TopTracksFragment mTopTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top_tracks_container) != null) {
            //two pane mode
            mTwoPane = true;
            if (savedInstanceState == null) {
                TopTracksFragment mTopTracksFragment = new TopTracksFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, mTopTracksFragment, TOP_TRACKS_FRAGMENT_TAG)
                        .commit();
            } else {
                mTwoPane = false;
            }
        }

        //register the callback
        ArtistFilterFragment artistFilterFragment = (ArtistFilterFragment) getSupportFragmentManager().findFragmentById(
                R.id.fragment_artist_filter);
        artistFilterFragment.addCallback(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get the artist fragment here?
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent( this, SettingsActivity.class );
            startActivity( intent );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String artistKey, String artistName) {
        if (mTwoPane) { //tablet
            TopTracksFragment topTracksFragment = new TopTracksFragment();

            //arguments
            Bundle args = new Bundle();
            args.putString( Constants.ARTIST_ID_KEY, artistKey);
            args.putString(Constants.ARTIST_NAME_KEY, artistName);
            args.putBoolean( Constants.IS_LARGE_SCREEN_KEY, mTwoPane);

            topTracksFragment.setArguments(args);
            //topTracksFragment.onArtistChanged( artistKey, artistName );
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, topTracksFragment, TOP_TRACKS_FRAGMENT_TAG)
                    .commit();

            mTopTracksFragment = topTracksFragment;
        } else { //smartphone
            Intent detailIntent = new Intent(this, TopTracksActivity.class)
                    .putExtra(Constants.ARTIST_ID_KEY, artistKey)
                    .putExtra(Constants.ARTIST_NAME_KEY, artistName)
                    .putExtra(Constants.IS_LARGE_SCREEN_KEY, mTwoPane);
            startActivity(detailIntent);
        }
    }

}
