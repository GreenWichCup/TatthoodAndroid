package com.example.tatthood;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    ImageView saveChanges, cancelChanges, changeProfilePhoto,currentProfilePhoto;
    MaterialEditText fullName,username,bio;
    private Uri profileImageUri;
    private StorageTask uploadTask;
    StorageReference profileStorageReference;
    FirebaseUser firebaseUser ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cancelChanges = findViewById(R.id.cancel);
        saveChanges = findViewById(R.id.save_edit);
        changeProfilePhoto = findViewById(R.id.change_photo);
        bio = findViewById(R.id.bio);
        fullName = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        cancelChanges = findViewById(R.id.cancel);
        currentProfilePhoto = findViewById(R.id.image_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileStorageReference = FirebaseStorage.getInstance().getReference("images");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                GlideApp.with(getApplicationContext()).load(user.getimageUrl()).into(currentProfilePhoto);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        currentProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uptadeProfile(username.getText().toString(),bio.getText().toString());
            }
        });
    }
    private void uptadeProfile(String username, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users")
                .child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("username",username);
            hashMap.put("bio",bio);
            reference.updateChildren(hashMap);

            finish();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("uploading...");
        pd.show();

        if (profileImageUri != null){
            StorageReference referencefile = profileStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(profileImageUri));
            uploadTask = referencefile.putFile(profileImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return referencefile.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users")
                                .child(firebaseUser.getUid());
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl",imageUrl);
                        reference.updateChildren(hashMap);
                        pd.dismiss();

                    } else {
                        Toast.makeText(EditProfileActivity.this,"Error uploading failed!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else{
            Toast.makeText(this,"No image selected!",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            profileImageUri = result.getUri();
            uploadImage();

        } else {
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
}