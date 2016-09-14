package com.derekma.videogallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.derekma.videogallery.dummy.DummyContent;
import com.derekma.videogallery.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecommendFragment extends Fragment{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecommendFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecommendFragment newInstance(int columnCount) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);

        DummyContent dummyContent = new DummyContent();

        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(DummyItem item) {
                //Toast.makeText(getContext(), item.toString(), Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext().getApplicationContext(), PlayerActivity.class);
                i.putExtra("videoId", item.playId);
                i.putExtra("description", item.comment);
                startActivity(i);
            }
        };

        databaseManager = DatabaseManager.getInstance(getContext());
        sqLiteDatabase = databaseManager.getWritableDatabase();
        databaseManager.getRecommendFromSQLite(sqLiteDatabase);

        MyRecommendRecyclerViewAdapter myRecommendRecyclerViewAdapter = new MyRecommendRecyclerViewAdapter(DummyContent.ITEMS, mListener);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean playType = sharedPref.getBoolean(SettingsActivity.AUTO_UPDATE, true);

        Log.i("Update", String.valueOf(playType));

        if(playType){
            dummyContent.execute();
        }

        dummyContent.myRecommendRecyclerViewAdapter = myRecommendRecyclerViewAdapter;

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(myRecommendRecyclerViewAdapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
