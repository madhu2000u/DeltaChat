package com.madhu.deltachat.ApplicationClasses;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madhu.deltachat.Constants.Constants;

public class DeltaChat extends Application {

    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth=FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            userDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot)
                    .child(mAuth.getCurrentUser().getUid());

            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot!=null){
                        userDatabase.child(Constants.online).onDisconnect().setValue(false);
                        userDatabase.child(Constants.online).setValue(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }
}
