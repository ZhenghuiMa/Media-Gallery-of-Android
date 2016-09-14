package com.derekma.videogallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WishListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WishListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;

    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<Bitmap> imgs = new ArrayList<Bitmap>();
    ArrayList<Integer> ids = new ArrayList<Integer>();
    ArrayList<Integer> favorites = new ArrayList<>();
    ArrayList<String> playId = new ArrayList<>();

    GridView gridView;

    public SimpleAdapter adapter;

    public static String param;

    public static List<Map<String, Object>> list = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public WishListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WishListFragment newInstance(String param1, String param2) {
        WishListFragment fragment = new WishListFragment();
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
        // Inflate the layout for this fragment

        databaseManager = DatabaseManager.getInstance(getActivity());

        sqLiteDatabase = databaseManager.getWritableDatabase();

        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        gridView = (GridView) view.findViewById(R.id.gridView);

        updateView();

        adapter = new SimpleAdapter(getContext(), list, R.layout.grid_item,
                new String[] { "img", "title"}, new int[] {
                R.id.grid_image, R.id.grid_text});

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                                  @Override
                                  public boolean setViewValue(View view, Object data, String textRepresentation) {
                                      // TODO Auto-generated method stub
                                      if (view instanceof ImageView && data instanceof Bitmap) {
                                          ImageView i = (ImageView) view;
                                          i.setImageBitmap((Bitmap) data);
                                          return true;
                                      }
                                      return false;
                                  }
                              }
        );

        gridView.setAdapter(adapter);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!list.isEmpty()) {
                    showAlert(titles.get(position), favorites.get(position));
                }

                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!list.isEmpty()){
                    Intent i = new Intent(getContext().getApplicationContext(), PlayerActivity.class);
                    i.putExtra("videoId", list.get(position).get("play_id").toString());
                    startActivity(i);
                }
            }
        });

        return  view;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updateView(){

        String sql = "SELECT video.video_id, video.play_id, video.img, video.title, favorite.favorite_id FROM video INNER JOIN favorite ON video.video_id = favorite.video_id where favorite.user_id="+MixedActivity.uId;

        try {

            Cursor c = sqLiteDatabase.rawQuery(sql, null);

            //Log.i("Check Adapter", String.valueOf(c.getCount()));

            int videoIdIndex = c.getColumnIndex("video_id");
            int playIdIndex = c.getColumnIndex("play_id");
            int titleIndex = c.getColumnIndex("title");
            int imgIndex = c.getColumnIndex("img");
            int favoriteIndex = c.getColumnIndex("favorite_id");

            c.moveToFirst();

            titles.clear();
            ids.clear();
            imgs.clear();
            favorites.clear();
            playId.clear();

            c.moveToFirst();

            while ((!c.isAfterLast())) {

                titles.add(c.getString(titleIndex));
                ids.add(c.getInt(videoIdIndex));
                imgs.add(DbBitmapUtility.getImage(c.getBlob(imgIndex)));
                favorites.add(c.getInt(favoriteIndex));
                playId.add(c.getString(playIdIndex));

                c.moveToNext();

            }

            list.clear();

            if (!(ids.isEmpty())) {

                Map<String, Object> map;

                for (int i = 0; i < c.getCount(); i++) {
                    map = new HashMap<String, Object>();
                    map.put("img", imgs.get(i));
                    map.put("title", titles.get(i));
                    map.put("favorite", favorites.get(i));
                    map.put("play_id", playId.get(i));
                    list.add(map);
                }
            }

            //Log.i("Check Size", String.valueOf(list.size()));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showAlert(String title, final int favoriteId) {

        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_popup_reminder)
                .setTitle(title)
                .setMessage("Do you want to delete this video from your Favorite?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int res = databaseManager.deleteFromFavoriteList(sqLiteDatabase, favoriteId);

                        if (res == 0) {
                            //Log.i("Delete Result", "Failed");
                            Toast.makeText(getContext(), "Delete failed, Please try again!", Toast.LENGTH_LONG).show();
                        } else {
                            //Log.i("Delete Result", String.valueOf(res));
                            updateView();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Delete successfully!", Toast.LENGTH_LONG).show();
                        }

                    }
                }).show();

    }
}
