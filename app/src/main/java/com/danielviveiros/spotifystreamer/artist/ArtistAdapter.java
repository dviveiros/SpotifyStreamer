package com.danielviveiros.spotifystreamer.artist;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielviveiros.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom adapter to handle artist list (search) information
 * Created by dviveiros on 24/06/15.
 */
public class ArtistAdapter extends CursorAdapter {

    /** Log tag */
    private final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * View holder pattern
     */
    public class ViewHolder {
        @Bind(R.id.artist_icon) ImageView artistIconView;
        @Bind(R.id.artist_name) TextView artistNameView;

        /**
         * Constructor
         */
        public ViewHolder( View view ) {
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.artistfilter_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        StreamerArtist artist = new StreamerArtist( cursor );
        viewHolder.artistNameView.setText(artist.getName());
        if (artist.getImageUrl() != null) {
            Picasso.with(context).load(artist.getImageUrl()).into(viewHolder.artistIconView);
        }
    }

}
