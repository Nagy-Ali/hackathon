package com.vispire.applications.volajj.RequestsFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vispire.applications.volajj.R;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRequestFragment extends Fragment {

    DatabaseReference db;
    RequestsAdapter adapter;
    ArrayList<Request> requestsList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NewRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewRequestFragment newInstance(String param1, String param2) {
        NewRequestFragment fragment = new NewRequestFragment();
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

        requestsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_new_request, container, false);

        adapter = new RequestsAdapter(getActivity(), requestsList, getActivity().getSupportFragmentManager());

        ListView listView = rootView.findViewById(R.id.new_requests_list);
        listView.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference();

        db.child("Request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChildren()) {
                    String title;
                    String status;
                    String imageUri;
                    String longitude;
                    String latitude;
                    String user;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                            Log.i("knkn,", ds.toString());
                            if (ds.hasChild("requestType")) {
                                title = ds.child("requestType").getValue(String.class);
                            } else {
                                title = "Unknown Type";
                            }

                            if (ds.hasChild("status")) {
                                status = ds.child("status").getValue(String.class);
                            } else {
                                status = "Unknown Status";
                            }

                            if (ds.hasChild("imageUri")) {
                                imageUri = ds.child("imageUri").getValue(String.class);
                            } else {
                                imageUri = "none";
                            }

                            if (ds.hasChild("latitude")) {
                                longitude = ds.child("latitude").getValue(Double.class).toString();
                            } else {
                                longitude = "unknown";
                            }

                            if (ds.hasChild("longitude")) {
                                latitude = ds.child("longitude").getValue(Double.class).toString();
                            } else {
                                latitude = "unknown";
                            }

                            if (ds.hasChild("QRCode")) {
                                user = ds.child("QRCode").getValue(String.class);
                            } else {
                                user = "";
                            }

                            Request request = new Request(title, status, imageUri, longitude,
                                    latitude, user);
                            requestsList.add(request);
                            adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
