package com.danielviveiros.spotifystreamer.track;

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

/**
 * Custom adapter to handle artist list (search) information
 * Created by dviveiros on 24/06/15.
 */
public class TopTracksAdapter extends CursorAdapter {

    private Context context;

    public TopTracksAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    public class ViewHolder {
        ImageView albumIconView;
        TextView albumNameView;
        TextView trackNameView;

        public ViewHolder( View view ) {
            this.albumNameView = (TextView) view.findViewById(R.id.album_name);
            this.trackNameView = (TextView) view.findViewById(R.id.track_name);
            this.albumIconView = (ImageView) view.findViewById(R.id.album_icon);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.toptracks_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        StreamerTrack track = new StreamerTrack( cursor );
        viewHolder.trackNameView.setText(track.getName());
        viewHolder.albumNameView.setText(track.getAlbumName());
        if (track.getAlbumImageUrl() != null) {
            Picasso.with(context).load(track.getAlbumImageUrl()).into(viewHolder.albumIconView);
        }
    }

}
