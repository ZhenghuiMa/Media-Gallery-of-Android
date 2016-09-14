package com.derekma.videogallery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListView listView;

    public static List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private DatabaseManager databaseManager;

    private SQLiteDatabase sqLiteDatabase;

    public VideosFragment() {
        // Required empty public constructor
    }

    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        void onFavoriteSelected(int position, long uid, String vid);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideosFragment newInstance(String param1, String param2) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseManager = DatabaseManager.getInstance(getActivity());

        sqLiteDatabase = databaseManager.getWritableDatabase();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        listView = (ListView) view.findViewById(R.id.videoListFragment);

        DownloadVideosTask downloadVideosTask = new DownloadVideosTask(sqLiteDatabase);

        downloadVideosTask.updateListView();

        if(list.size() == 0){
            MixedActivity.mDialog = new ProgressDialog(getContext());
            MixedActivity.mDialog.setTitle("Loading");
            MixedActivity.mDialog.setMessage("Please wait.");
            MixedActivity.mDialog.setCancelable(false);
            MixedActivity.mDialog.show();
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.videos_list_item,
                new String[] { "img", "title", "channel" }, new int[] {
                R.id.listitem_pic, R.id.listitem_title, R.id.listitem_content });

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }}
        );

        downloadVideosTask.adapter = adapter;

        listView.setAdapter(adapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String playType = sharedPref.getString(SettingsActivity.PLAY_TYPE, "");
        Log.i("Check Settings", playType);

        if(playType.equals("web")){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i = new Intent(getContext().getApplicationContext(), WebPlayerActivity.class);
                    i.putExtra("videoId", list.get(position).get("play_id").toString());
                    i.putExtra("description", list.get(position).get("description").toString());
                    startActivity(i);

                }
            });
        }else{
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i = new Intent(getContext().getApplicationContext(), PlayerActivity.class);
                    i.putExtra("videoId", list.get(position).get("play_id").toString());
                    i.putExtra("description", list.get(position).get("description").toString());
                    startActivity(i);

                }
            });
        }


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showAlert(position, list.get(position).get("title").toString(), list.get(position).get("description").toString(), (Integer)list.get(position).get("video_id"), list.get(position).get("play_id").toString());

                return true;
            }
        });

        try {
            downloadVideosTask.execute(Config.YOUTUBE_API_URL);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try {
            mCallback = (OnHeadlineSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * A public method to show Alert,
     * User can choose another way to play video.
     * Also can press "Add to my WishList" to add video to WishList.
     * @param title title for video.
     * @param description description for video.
     * @param videoId video ID.
     */
    public void showAlert(final int position, String title, final String description, final int videoId, final String playId) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_popup_reminder)
                .setTitle(title)
                .setMessage("You can \"Play this video in WebView\" and you can \"Add video to WishList\"!")
                .setPositiveButton("Add video to WishList", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ContentValues cv = new ContentValues();

                        cv.put("user_id", MixedActivity.uId);
                        cv.put("video_id", videoId);

                        int update = databaseManager.updateFavoriteList(sqLiteDatabase, cv, videoId);

                        if (update == 0) {
                            databaseManager.insertFavoriteList(sqLiteDatabase, cv);

                            Toast.makeText(getContext(), "Insert video into favorite list successfully!", Toast.LENGTH_LONG).show();
                            //Cursor c = MainActivity.mySQLiteDB.rawQuery("SELECT * FROM favorite", null);
                            //Log.i("Check favorite insert", String.valueOf(c.getCount()));
                        } else {
                            Toast.makeText(getContext(), "Update video in favorite successfully!", Toast.LENGTH_LONG).show();
                        }

                        mCallback.onFavoriteSelected(position, MixedActivity.uId, playId);

                    }
                }).setNegativeButton("Play in App", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(getContext().getApplicationContext(), PlayerActivity.class);
                i.putExtra("videoId", playId);
                i.putExtra("description", description);
                startActivity(i);

            }
        }).setNeutralButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }
}
