package com.madhu.deltachat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {


    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    //Settings Widget Instances
    private CircleImageView circleImageView;
    private TextView displayName, Status;
    private Button changeProfilPicButton, changeNameButton, changeStatusButton;
    //Settings Widget Instances
    private ProgressDialog progressDialog;

    private StorageReference storageReference;

    private int galleryIntentResult = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        circleImageView = findViewById(R.id.settings_profile_pic);
        displayName = findViewById(R.id.diaplay_name);
        Status = findViewById(R.id.status);
        changeProfilPicButton = findViewById(R.id.settings_change_image);
        changeNameButton = findViewById(R.id.settings_change_name);
        changeStatusButton = findViewById(R.id.settings_change_status);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Updating...");

        storageReference = FirebaseStorage.getInstance().getReference();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_UID = currentUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot).child(current_UID);

        changeProfilPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        //.setMinCropWindowSize(500,500)
                        .start(SettingsActivity.this);

                /*Intent gallery=new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select Image"), galleryIntentResult);*/

            }
        });

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implementation pending


            }
        });

        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implementation pending
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Toast.makeText(SettingsActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                String name = dataSnapshot.child(Constants.userName).getValue().toString();
                String status = dataSnapshot.child(Constants.userStatus).getValue().toString();
                String image = dataSnapshot.child(Constants.userImage).getValue().toString();
                String thumb_nail = dataSnapshot.child(Constants.userThumbnail).getValue().toString();

                displayName.setText(name);
                Status.setText(status);
                if (!image.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_profile_pic).into(circleImageView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /*if (requestCode==galleryIntentResult && resultCode==RESULT_OK)
        {
            String imageUri=data.getDataString();
            Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();
        }*/
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_nailFile= new File(resultUri.getPath());
                Bitmap thumb_nailBitMap=null;

                try {
                    thumb_nailBitMap= new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxHeight(200)
                            .setQuality(60)
                            .compressToBitmap(thumb_nailFile);

                }catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_nailBitMap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                final byte[] thumb_nailByte=byteArrayOutputStream.toByteArray();



                final StorageReference filePath = storageReference.child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
                final StorageReference thumb_nailFilePath=storageReference.child("profile_images").child("thumb_nails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //progressDialog.dismiss();

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadable_Url = uri.toString();
                                    final UploadTask uploadTask=thumb_nailFilePath.putBytes(thumb_nailByte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        //String thumb_nailDownloadableUri;
                                        @Override
                                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> thumbNailTask) {

                                            thumb_nailFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {

                                                    String thumb_nailDownloadableUri = uri.toString();
                                                    if (thumbNailTask.isSuccessful())
                                                    {
                                                        Map updated_hashMap=new HashMap();
                                                        updated_hashMap.put(Constants.userImage,downloadable_Url);
                                                        Log.i("msg",thumb_nailDownloadableUri);
                                                        updated_hashMap.put(Constants.userThumbnail, thumb_nailDownloadableUri);

                                                        databaseReference.updateChildren(updated_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SettingsActivity.this, "Uri Upload Success", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                    else {
                                                        Toast.makeText(SettingsActivity.this, "Uri upload failed", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });



                                        }
                                    });

                                }
                            });

                            //Log.d("msg",downloadable_URL);
                            //databaseReference.child(Constants.userImage).setValue()
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Toast.makeText(SettingsActivity.this, resultUri.toString(), Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }
}
