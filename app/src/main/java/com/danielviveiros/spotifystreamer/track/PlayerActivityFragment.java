package com.danielviveiros.spotifystreamer.track;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielviveiros.spotifystreamer.R;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLAYER_LOADER = 2;

    public PlayerActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLAYER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_fragment, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(),
                TrackRepository.FULL_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        String albumName = data.getString( TrackRepository.COL_INDEX_ALBUM_NAME );
        String albumImageUrl = data.getString( TrackRepository.COL_INDEX_FULL_ALBUM_IMAGE_URL );
        String trackName = data.getString( TrackRepository.COL_INDEX_NAME );
        String urlPreview = data.getString( TrackRepository.COL_INDEX_URL_PREVIEW );
        String artistName = data.getString( TrackRepository.COL_INDEX_ARTIST_NAME);

        TextView artistTextView = (TextView) getView().findViewById( R.id.artist_textview );
        TextView albumTextView = (TextView) getView().findViewById( R.id.album_textview );
        TextView trackTextView = (TextView) getView().findViewById( R.id.track_textview );
        ImageView imageView = (ImageView) getView().findViewById( R.id.album_artwork );

        artistTextView.setText( artistName );
        albumTextView.setText( albumName );
        trackTextView.setText( trackName );
        Picasso.with(getActivity()).load(albumImageUrl).into(imageView);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
