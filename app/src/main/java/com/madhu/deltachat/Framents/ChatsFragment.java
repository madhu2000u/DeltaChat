package com.madhu.deltachat.Framents;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madhu.deltachat.Activities.UserProfileActivity;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.ModelDataClasses.ChatUsers;
import com.madhu.deltachat.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<ChatUsers, ChatsUserViewHoler> adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot);

        recyclerView = view.findViewById(R.id.chat_users_list);
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

        adapter = new FirebaseRecyclerAdapter<ChatUsers, ChatsUserViewHoler>(
                ChatUsers.class,
                R.layout.user_single_layout,
                ChatsUserViewHoler.class,
                databaseReference


        ) {
            @Override
            protected void populateViewHolder(final ChatsUserViewHoler chatsUserViewHoler, final ChatUsers chatUsers, int i) {
                Log.i("msg", "databasereference" + databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).toString());
                Log.i("msg", "chatuseres" + chatUsers.getUid());
                //databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())).toString() != chatUsers.getUid())

                if (!(chatUsers.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                    chatsUserViewHoler.set_name(chatUsers.getName());
                    chatsUserViewHoler.set_status(chatUsers.getStatus());
                    chatsUserViewHoler.setThumbNail(chatUsers.getThumbNail(), getContext());
                    chatsUserViewHoler.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String Uid = chatUsers.getUid();
                            Intent userProfileIntent = new Intent(getContext(), UserProfileActivity.class);
                            userProfileIntent.putExtra("Uid", Uid);
                            startActivity(userProfileIntent);
                        }
                    });
                }

            }
        };
        recyclerView.setAdapter(adapter);

    }

    public static class ChatsUserViewHoler extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView userName;
        private TextView userStatus;
        private CircleImageView circleImageView;

        public ChatsUserViewHoler(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.user_single_cardView);

            userName = itemView.findViewById(R.id.name_user_single);

            userStatus = itemView.findViewById(R.id.status_user_single);

            circleImageView = itemView.findViewById(R.id.circle_image_single);

        }

        public void set_name(String name) {

            userName.setText(name);
        }

        public void set_status(String status) {

            userStatus.setText(status);
        }

        public void setThumbNail(String thumbNailImage, Context context) {

            Picasso.with(context).load(thumbNailImage).placeholder(R.drawable.default_profile_pic).into(circleImageView);
        }
    }


}
