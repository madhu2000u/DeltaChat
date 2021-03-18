package com.madhu.deltachat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.contentcapture.DataRemovalRequest;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madhu.deltachat.Adapters.MainPagerAdapter;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.R;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager viewPager;
    private MainPagerAdapter mainPagerAdapter;
    private DatabaseReference userReference;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
        userReference= FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot)
                .child(mAuth.getCurrentUser().getUid());}

        viewPager=findViewById(R.id.viewPager);

        mainPagerAdapter=new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainPagerAdapter);

        tabLayout=findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            intentStarter();

        }
        else{
            userReference.child(Constants.online).setValue(true);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser()!=null)
        userReference.child(Constants.online).setValue(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_logout_button:
                FirebaseAuth.getInstance().signOut();
                intentStarter();
                break;
            case R.id.accountSettings:
                Intent settingsInternt=new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsInternt);
        }
        return true;
    }

    private void intentStarter()
    {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();

    }
}
























