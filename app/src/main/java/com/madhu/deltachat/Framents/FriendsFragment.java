package com.madhu.deltachat.Framents;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madhu.deltachat.Activities.ChatActivity;
import com.madhu.deltachat.Activities.UserProfileActivity;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.ModelDataClasses.ChatUsers;
import com.madhu.deltachat.ModelDataClasses.UserFriends;
import com.madhu.deltachat.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.CompletableOnSubscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference friendsReference;
    private FirebaseUser currentUser;
    private FirebaseRecyclerAdapter<UserFriends, FriendsFragment.FriendsUserViewHolder> adapter;



    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friends, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot);
        friendsReference=FirebaseDatabase.getInstance().getReference().child(Constants.friends);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.friends_users_lsit);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("msg", "onStart()");

        /*Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.databaseRoot);

        FirebaseRecyclerOptions<ChatUsers> options =
                new FirebaseRecyclerOptions.Builder<ChatUsers>()
                        .setQuery(query, new SnapshotParser<ChatUsers>() {
                            @NonNull
                            @Override
                            public ChatUsers parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new ChatUsers(snapshot.child(Constants.userName).getValue().toString(),
                                        snapshot.child(Constants.userStatus).getValue().toString(),
                                        snapshot.child(Constants.userImage).getValue().toString());
                            }
                        })
                        .build();

        /*adapter = new FirebaseRecyclerAdapter<ChatUsers, FriendsUserViewHolder>(options) {
            @Override
            public FriendsUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);

                return new FriendsUserViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(FriendsUserViewHolder holder, final int position, ChatUsers model) {
                holder.set_name(model.getName());
                holder.set_status(model.getStatus());

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);*/

        adapter = new FirebaseRecyclerAdapter<UserFriends, FriendsFragment.FriendsUserViewHolder>(
                UserFriends.class,
                R.layout.user_single_layout,
                FriendsUserViewHolder.class,
                friendsReference


        ) {


            @Override
            protected void populateViewHolder(final FriendsUserViewHolder friendsUserViewHolder, final UserFriends userFriends, int i) {
                //Log.i("msg", "databasereference" + databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).toString());
                Log.i("msg", "chatuseres" + userFriends.getDate());
                //databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())).toString() != chatUsers.getUid())



                final String list_user_id=getRef(i).getKey();

                databaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName=dataSnapshot.child(Constants.userName).getValue().toString();
                        String userStatus=dataSnapshot.child(Constants.userStatus).getValue().toString();
                        String userThumbnail=dataSnapshot.child(Constants.userThumbnail).getValue().toString();

                        friendsUserViewHolder.set_name(userName);
                        friendsUserViewHolder.setStatus(userStatus);
                        friendsUserViewHolder.setThumbNail(userThumbnail, getContext());
                        friendsUserViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i==0)
                                        {

                                            Intent userProfileIntent = new Intent(getContext(), UserProfileActivity.class);
                                            userProfileIntent.putExtra("Uid", list_user_id);
                                            startActivity(userProfileIntent);

                                        }
                                        if (i==1)
                                        {

                                            Intent chatActivityIntent = new Intent(getContext(), ChatActivity.class);
                                            chatActivityIntent.putExtra("Uid", list_user_id );
                                            chatActivityIntent.putExtra("user_name", userName);
                                            startActivity(chatActivityIntent);

                                        }

                                    }
                                });
                                builder.show();

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                /*if (!(userFriends.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                    friendsReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(userFriends.getUid()))
                            {
                                friendsUserViewHolder.set_name(userFriends.getName());
                                friendsUserViewHolder.set_status(userFriends.getStatus());
                                friendsUserViewHolder.setThumbNail(userFriends.getThumbNail(), getContext());
                                friendsUserViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String Uid = userFriends.getUid();
                                        Intent userProfileIntent = new Intent(getContext(), UserProfileActivity.class);
                                        userProfileIntent.putExtra("Uid", Uid);
                                        startActivity(userProfileIntent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }*/





            }




        };
        recyclerView.setAdapter(adapter);

    }

    public static class FriendsUserViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView userName;
        private TextView userStatus;
        private CircleImageView circleImageView;

        public FriendsUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.user_single_cardView);

            userName = itemView.findViewById(R.id.name_user_single);

            userStatus = itemView.findViewById(R.id.status_user_single);

            circleImageView = itemView.findViewById(R.id.circle_image_single);

        }

        public void set_name(String name) {

            userName.setText(name);
        }

        public void setStatus(String status) {

            userStatus.setText(status);
        }

        public void setThumbNail(String thumbNailImage, Context context) {

            Picasso.with(context).load(thumbNailImage).placeholder(R.drawable.default_profile_pic).into(circleImageView);
        }
    }



}
