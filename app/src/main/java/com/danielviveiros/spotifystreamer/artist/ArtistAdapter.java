package com.danielviveiros.spotifystreamer.artist;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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
public class ArtistAdapter extends CursorAdapter {

    /** Log tag */
    private final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    private Context context;

    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    /**
     * View holder pattern
     */
    public class ViewHolder {
        ImageView artistIconView;
        TextView artistNameView;

        /**
         * Constructor
         */
        public ViewHolder( View view ) {
            this.artistNameView = (TextView) view.findViewById(R.id.artist_name);
            this.artistIconView = (ImageView) view.findViewById(R.id.artist_icon);
        }

    }

        /*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Artist artist = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.artistfilter_listitem, null);
            holder = new ViewHolder();
            holder.artistNameView = (TextView) convertView.findViewById(R.id.artist_name);
            holder.artistIconView = (ImageView) convertView.findViewById(R.id.artist_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.artistNameView.setText(artist.name);
        List<Image> images = artist.images;
        if ((images != null) && (images.size() > 0)) {
            //get the last image, the smaller one
            Image artistImage = images.get( images.size() - 1 );
            Picasso.with(context).load(artistImage.url).into(holder.artistIconView);
        }

        return convertView;
    }
    */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v( LOG_TAG, "newView called. cursor.size = " + cursor.getCount());

        View view = LayoutInflater.from(context).inflate(R.layout.artistfilter_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v( LOG_TAG, "bindView called. cursor.size = " + cursor.getCount());
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        StreamerArtist artist = new StreamerArtist( cursor );
        viewHolder.artistNameView.setText(artist.getName());
        if (artist.getImageUrl() != null) {
            Picasso.with(context).load(artist.getImageUrl()).into(viewHolder.artistIconView);
        }

        Log.v( LOG_TAG, "artist binded = " + artist );
    }

}
