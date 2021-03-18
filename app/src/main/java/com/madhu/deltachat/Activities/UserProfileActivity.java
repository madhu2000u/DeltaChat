    package com.madhu.deltachat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

    public class UserProfileActivity extends AppCompatActivity {

        private CircleImageView profile_pic;
        private TextView user_display_name;
        private TextView user_status;
        private Button sendFriendRequestButton, declineFrndRequest;
        private DatabaseReference friendReqDatabase;
        private DatabaseReference databaseReference;
        private DatabaseReference friendsReference;
        private DatabaseReference notificationReference;
        private FirebaseUser currentUser;

        private int current_FriendsState;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String Uid=getIntent().getStringExtra("Uid");
        //Database References
        databaseReference=FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot).child(Uid);
        friendReqDatabase=FirebaseDatabase.getInstance().getReference().child(Constants.friendReq);
        friendsReference=FirebaseDatabase.getInstance().getReference().child(Constants.friends);
        notificationReference=FirebaseDatabase.getInstance().getReference().child(Constants.notification);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        //Database References


        profile_pic=findViewById(R.id.user_profile_pic);
        user_display_name=findViewById(R.id.user_profile_diaplay_name);
        user_status=findViewById(R.id.user_profile_status);
        sendFriendRequestButton =findViewById(R.id.user_send_requestButton);
        declineFrndRequest=findViewById(R.id.user_decline_request);

        current_FriendsState=Constants.friendState_notFriends;

        declineFrndRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendReqDatabase.child(currentUser.getUid()).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendReqDatabase.child(Uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserProfileActivity.this, "Request Declined", Toast.LENGTH_SHORT).show();     
                            }
                        });
                    }
                });
            }
        });



        sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implementation of friend request pending



                switch (current_FriendsState)
                {
                    case Constants.friendState_notFriends:   // current state is not friends so button sends request
                        sendFriendRequestButton.setEnabled(false);
                        friendReqDatabase.child(currentUser.getUid()).child(Uid).child(Constants.friendReqType).setValue(Constants.friendReqSent).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    friendReqDatabase.child(Uid).child(currentUser.getUid()).child(Constants.friendReqType).
                                            setValue(Constants.friendReqReceived).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            HashMap<String, String> notificationData=new HashMap<>();
                                            notificationData.put(Constants.notificationFrom, currentUser.getUid());
                                            notificationData.put(Constants.notificationType, Constants.notificationTypeReq);

                                            notificationReference.child(Uid).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sendFriendRequestButton.setEnabled(true);
                                                    current_FriendsState=Constants.friendRequestSent;
                                                    Toast.makeText(UserProfileActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                            //sendFriendRequestButton.setText("Request Sent");
                                            //sendFriendRequestButton.setTextColor(getResources().getColor(R.color.blue));
                                            //sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.white));
                                            //ViewCompat.setBackgroundTintList(
                                                    //sendFriendRequestButton,
                                                   // ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                            //current_FriendsState=Constants.friendRequestSent;
                                        }
                                    });

                                }
                                else 
                                {
                                    Toast.makeText(UserProfileActivity.this, "Could not send friend request! Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case Constants.friendRequestSent:    //current state is request sent so button Cancels the request

                        friendReqDatabase.child(currentUser.getUid()).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                friendReqDatabase.child(Uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendFriendRequestButton.setText(R.string.send_friend_request);
                                        sendFriendRequestButton.setTextColor(getResources().getColor(R.color.colorAccent));
                                        current_FriendsState=Constants.friendState_notFriends;
                                    }
                                });
                            }
                        });
                        Toast.makeText(UserProfileActivity.this, "Request Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.friendRequestReceived:    //current state is request received so button accepts request

                        final String current_date= DateFormat.getDateTimeInstance().format(new Date());

                        friendsReference.child(currentUser.getUid()).child(Uid).child(Constants.date).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendsReference.child(Uid).child(currentUser.getUid()).child(Constants.date).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        friendReqDatabase.child(currentUser.getUid()).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                friendReqDatabase.child(Uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        declineFrndRequest.setVisibility(View.GONE);
                                                        //sendFriendRequestButton.setText(R.string.unfriend);
                                                        //sendFriendRequestButton.setTextColor(getResources().getColor(R.color.red));
                                                        //current_FriendsState=Constants.friendState_friends;
                                                    }
                                                });
                                            }
                                        });


                                    }
                                });
                            }
                        });


                        break;
                    case Constants.friendState_friends:   //current state is they are fiends so button unfriends them
                        friendsReference.child(currentUser.getUid()).child(Uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendsReference.child(Uid).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        current_FriendsState=Constants.friendState_notFriends;

                                    }
                                });
                            }
                        });

                }


            }
        });

        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUser.getUid()))
                    if (!dataSnapshot.child(currentUser.getUid()).hasChild(Uid))
                    {
                        declineFrndRequest.setVisibility(View.GONE);
                        sendFriendRequestButton.setText(R.string.send_friend_request);
                        sendFriendRequestButton.setTextColor(getResources().getColor(R.color.colorAccent));
                        current_FriendsState=Constants.friendState_notFriends;
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        friendReqDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.child(currentUser.getUid()).hasChild(Uid)) {
                        String frndRequestType = dataSnapshot.child(currentUser.getUid()).child(Uid).child(Constants.friendReqType).getValue().toString();

                        switch (frndRequestType) {
                            case Constants.friendReqSent:
                                sendFriendRequestButton.setText(R.string.cancel_friend_request);
                                sendFriendRequestButton.setTextColor(getResources().getColor(R.color.blue));
                                //sendFriendRequestButton.setBackgroundColor(getResources().getColor(R.color.white));
                                //ViewCompat.setBackgroundTintList(
                                //sendFriendRequestButton,
                                //ColorStateList.valueOf(getResources().getColor(R.color.white)));
                                current_FriendsState = Constants.friendRequestSent;
                                break;
                            case Constants.friendReqReceived:
                                sendFriendRequestButton.setText(R.string.accept_friend_request);
                                sendFriendRequestButton.setTextColor(getResources().getColor(R.color.blue));
                                current_FriendsState = Constants.friendRequestReceived;
                                declineFrndRequest.setVisibility(View.VISIBLE);
                                break;
                        }

                        switch (current_FriendsState) {
                            case Constants.friendState_notFriends:
                                sendFriendRequestButton.setText(R.string.send_friend_request);
                                sendFriendRequestButton.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    } else {

                        friendsReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(Uid)) {
                                    sendFriendRequestButton.setText(R.string.unfriend);
                                    sendFriendRequestButton.setTextColor(getResources().getColor(R.color.red));
                                    current_FriendsState = Constants.friendState_friends;

                                }
                                else{

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (NullPointerException e)
                {


                    /*declineFrndRequest.setVisibility(View.GONE);
                    sendFriendRequestButton.setText(R.string.send_friend_request);
                    sendFriendRequestButton.setTextColor(getResources().getColor(R.color.colorAccent));
                    current_FriendsState=Constants.friendState_notFriends;*/


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child(Constants.userName).getValue().toString();
                String image=dataSnapshot.child(Constants.userImage).getValue().toString();
                String status=dataSnapshot.child(Constants.userStatus).getValue().toString();


               

                if (!image.equals("default"))Picasso.with(UserProfileActivity.this).load(image).placeholder(R.drawable.default_profile_pic)
                        .into(profile_pic);
                
                user_display_name.setText(name);
                user_status.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
