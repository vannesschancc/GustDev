package com.google.firebase.quickstart.database.fragment;

/**
 * Created by tangjinhao on 11/15/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.PostDetailActivity;
import com.google.firebase.quickstart.database.ProfileActivity;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.ChatActivity;
import com.google.firebase.quickstart.database.models.Info;
import com.google.firebase.quickstart.database.models.Message;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.models.Profile;
import com.google.firebase.quickstart.database.viewholder.PeopleViewHolder;
import com.google.firebase.quickstart.database.viewholder.PostViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.quickstart.database.PostDetailActivity;
import com.google.firebase.quickstart.database.R;
import com.google.firebase.quickstart.database.models.Post;
import com.google.firebase.quickstart.database.viewholder.PostViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class PeopleFragment extends Fragment {

    private static final String TAG = "PeopleFragment";
    private ArrayList<String> mchatroom = new ArrayList<String>();

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Profile, PeopleViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public PeopleFragment() {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_people, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mRecycler = rootView.findViewById(R.id.people_list);
        mRecycler.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        //Query peopleQuery = mDatabase.child("profiles").child(getUid());
        final Query peopleQuery = mDatabase.child("profiles").limitToFirst(30);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Profile>()
                .setQuery(peopleQuery, Profile.class).build();

        mAdapter = new FirebaseRecyclerAdapter<Profile, PeopleViewHolder>(options) {
            @Override
            public PeopleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PeopleViewHolder(inflater.inflate(R.layout.people_contact, viewGroup, false));
            }


            @Override
            protected void onBindViewHolder(PeopleViewHolder viewHolder, int position, final Profile model) {
                final DatabaseReference peopleRef = getRef(position);
                // Set click listener for the whole post view
                final String infoKey = peopleRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch Profile Activity
                        Intent profileActivity = new Intent(getActivity(), ProfileActivity.class);
                        profileActivity.putExtra("intentUserID",infoKey);
                        startActivity(profileActivity);
                    }
                });
                // Bind Post to ViewHolder, setting OnClickListener for the star button
                // can be used as message
                // for two people,
                viewHolder.bindToPeople(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        // find the user Uid
                        final String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final DatabaseReference userhash = mDatabase.child("user-user");
                        // determine if the user-user pair exist
                                    Log.d("Chat","Success");
                                    userhash.child(Uid).addListenerForSingleValueEvent(new ValueEventListener(){
                                        public void onDataChange(DataSnapshot snapshot1) {
                                            boolean Create = true;
                                            for (DataSnapshot peopleSnapshot: snapshot1.getChildren()) {
                                                // TODO: handle the post
                                               // Log.d("ChatChat", peopleSnapshot.getKey());

                                                if (peopleSnapshot.getKey().equals( infoKey)) {
                                                        Create = false;
                                                        Log.d("ChatChat", peopleSnapshot.getValue().toString());
                                                        Intent chatActivity = new Intent(getActivity(),ChatActivity.class);
                                                        chatActivity.putExtra("Path","/chat-room/" + peopleSnapshot.getValue().toString());
                                                        chatActivity.putExtra("ReceiverName", model.username);
                                                        chatActivity.putExtra("receiver",infoKey);
                                                        startActivity(chatActivity);
                                                }
                                            }
                                            if (Create) {
                                                Log.d("ChatChat", "Create");
                                                Map<String, Object> childUpdates = new HashMap<>();
                                                String roomkey = mDatabase.child("chat-room").push().getKey();
                                                childUpdates.put("/user-user/" + Uid + "/" + infoKey, roomkey);
                                                childUpdates.put("/user-user/" + infoKey + "/" + Uid, roomkey);
                                                mDatabase.updateChildren(childUpdates);
                                                Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
                                                chatActivity.putExtra("Path", "/chat-room/" + roomkey);
                                                chatActivity.putExtra("receiver",infoKey);
                                                chatActivity.putExtra("ReceiverName", model.username);
                                                startActivity(chatActivity);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

