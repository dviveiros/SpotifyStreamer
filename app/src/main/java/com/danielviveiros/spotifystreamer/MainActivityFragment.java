package com.danielviveiros.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    /** Log tag */
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    /** Adapter to deal with the list of artists */
    private ArrayAdapter<String> mArtistListAdapter;

    /** List view */
    private ListView mForecastListView;

    /** Filter */
    private String mArtistFilter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //set the listener for the edit text
        EditText artistEditText = (EditText) rootView.findViewById(R.id.artist_filter);

        //prepare the list view and adapter
        mArtistListAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.artist_textview,R.id.artist_textview);
        mForecastListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mForecastListView.setAdapter(mArtistListAdapter);

        //configure the listener for the artist text field
        artistEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mArtistFilter = v.getText().toString();
                    updateArtistList();
                    return true;
                }
                return false;
                /*
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!event.isShiftPressed()) {
                        // the user is done typing.
                        mArtistFilter = v.getText().toString();
                        Log.v(LOG_TAG, "Artist filter = " + mArtistFilter);
                        updateArtistList();
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
                */
            }
        });



        return rootView;
    }

    /**
     * Updates the list of artists shown
     */
    private void updateArtistList() {

        //hides the keyboard
        InputMethodManager imm = (InputMethodManager)
                this.getActivity().getSystemService(this.getActivity().
                        getApplicationContext().INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        //show the result
        Toast toast = Toast.makeText(this.getActivity().getBaseContext(), mArtistFilter, Toast.LENGTH_SHORT);
        toast.show();
    }
}
