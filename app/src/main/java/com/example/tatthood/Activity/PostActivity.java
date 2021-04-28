package com.example.tatthood.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.example.tatthood.ModelData.Category;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class PostActivity extends AppCompatActivity{

    Uri imageUri;
    String imageUrl;
    Toolbar post_toolbar;
    StorageTask uploadTask;
    StorageReference storageReference;
    EditText post_description;
    ImageView close_post, post_image_add;
    TextView post_txt;
    CheckBox personalCheckbox;
    MultiSpinnerSearch multiSelectSpinnerWithSearch;
    FirebaseUser currentUser;
    private List<String> sCategory;
    private List<KeyPairBoolData> categoryList;
    private String editPostId, postUrl, description;
    private String isUpdateKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_layout);
        personalCheckbox = findViewById(R.id.personal_checkBox);
        close_post = findViewById(R.id.post_cancel);
        post_description = findViewById(R.id.post_description);
        post_image_add = findViewById(R.id.post_add_image);
        post_txt = findViewById(R.id.post_txt);
        post_toolbar =findViewById(R.id.post_toolbar);
        // multi spinner categories selection
        multiSelectSpinnerWithSearch = findViewById(R.id.multipleItemSelectionSpinner);
        multiSelectSpinnerWithSearch.setSearchEnabled(true);
        multiSelectSpinnerWithSearch.setSearchHint("Choose any category");
        multiSelectSpinnerWithSearch.setEmptyTitle("Not Data Found!");
        multiSelectSpinnerWithSearch.setShowSelectAllButton(false);
        multiSelectSpinnerWithSearch.setClearText("Close & Clear");
        categoryList  = new ArrayList<>();
        sCategory = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    int i = 0 ;
                    String categoryItems = snapshot.child("categoryName").getValue(String.class);
                    if (categoryItems!=null){
                            KeyPairBoolData h = new KeyPairBoolData();
                            h.setId(i+1);
                            h.setName(categoryItems);
                            h.setSelected(false);
                            categoryList.add(h);
                    }
                    Log.d("tag", "onDataChange: "+ categoryItems);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dont start new activity but go back
                    finish();
            }
        });

        post_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!categoryList.isEmpty() && !post_description.getText().toString().equals("")){
                    uploadImage();
                } else{
                    Toast.makeText(PostActivity.this,"Choose a category",Toast.LENGTH_SHORT).show();
                };
            }
        });

        //Spinner with checkbox library
        multiSelectSpinnerWithSearch.setItems(categoryList, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                       sCategory.add(items.get(i).getName());
                    }
                }
            }
        });
        // EditPost get data from previous Activity
        // Add modification to default layout
        Intent intent = getIntent();
        isUpdateKey = ""+ intent.getStringExtra("key");
        editPostId = "" + intent.getStringExtra("editPostId");
        postUrl =  intent.getStringExtra("post_url");
        description = intent.getStringExtra("postDescription");
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdateKey.equals("editPost")){
            loadPostData(editPostId);
        } else {
            post_toolbar.setTitle("New post");
            CropImage.activity().setAspectRatio(4,5).start(PostActivity.this);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        if (isUpdateKey.equals("editPost")){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(editPostId);
            if(personalCheckbox.isChecked()){
               reference.child("personal_tattoo").setValue(true);
            }
            reference.child("description").setValue(post_description.getText().toString());
            saveCategorySelected(editPostId,sCategory);


            progressDialog.dismiss();
        } else {
            if (imageUri != null) {
                long timeStamp = System.currentTimeMillis();
                StorageReference referencefile = storageReference.child(timeStamp+"."+getFileExtension(imageUri));
                uploadTask = referencefile.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return referencefile.getDownloadUrl();
                    }
                });

                uploadTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            String datePost = postDate(timeStamp);
                            Uri downloadUri = task.getResult();
                            imageUrl = downloadUri.toString();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                            String postId = reference.push().getKey();
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("postid",postId);
                            hashMap.put("postimage",imageUrl);
                            hashMap.put("description",post_description.getText().toString());
                            hashMap.put("publisher", currentUser.getUid());
                            hashMap.put("timestamp",datePost);

                            //has to be updatable
                            if(personalCheckbox.isChecked()){
                                hashMap.put("personal_tattoo",true);
                            }
                            reference.child(postId).setValue(hashMap);
                            //postId, imageUrl
                            saveCategorySelected(postId,sCategory);
                            progressDialog.dismiss();

                            startActivity(new Intent(PostActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(PostActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else{
                Toast.makeText(this,"No image selected!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            post_image_add.setImageURI(imageUri);
        } else {
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveCategorySelected (String idPostEdited, List<String> postEditedCategory ){
        HashMap<String,Object> hashMapCategory = new HashMap<>();
        HashMap<String,Object> hashInfoCat = new HashMap<>();
        DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference("CategoryMatched");
        refCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.child("matched").child(idPostEdited).exists()){
                        dataSnapshot.child("matched").child(idPostEdited).getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        postEditedCategory.forEach((n) -> {
            hashInfoCat.put("categoryName_uppercase", n.toUpperCase());
            hashInfoCat.put("categoryName",n);
            hashMapCategory.put("postid",idPostEdited);
            hashMapCategory.put("imageurl",postUrl);
            //need to be tested
            refCategory.child(n.trim().toLowerCase()).setValue(hashInfoCat);
            refCategory.child(n.trim().toLowerCase()).child("matched").child(idPostEdited).setValue(hashMapCategory);
        });
    }



    private String postDate(long postTime){
        TimeZone tz = Calendar.getInstance().getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(postTime));
        return localTime;
    }

    private void loadPostData(String postToEditId) {
        DatabaseReference refMatchedCategory = FirebaseDatabase.getInstance().getReference("CategoryMatched");
        post_toolbar.setTitle("Edit post");
        post_txt.setText("Update");
        post_description.setText(description);
        GlideApp.with(this).load(postUrl).into(post_image_add);

        refMatchedCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  if (dataSnapshot.child("matched").child(postToEditId).exists()){
                      Category matchedCategory = dataSnapshot.getValue(Category.class);
                      Log.i("IdPostMatched", "we have a match: "+ matchedCategory.getCategoryName());
                      for (int i = 0 ; i < categoryList.size(); i++) {
                          if (categoryList.get(i).getName().toLowerCase().equals(matchedCategory.getCategoryName().toLowerCase())) {
                              categoryList.get(i).setSelected(true);
                              categoryList.get(i).isSelected();
                              Log.i("spinnerList", "eaquals to: " + categoryList.get(i).getName() +  " status selected: " + categoryList.get(i).isSelected() );
                          }
                      }

                  }
                }
                multiSelectSpinnerWithSearch.setItems(categoryList, new MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(List<KeyPairBoolData> items) {
                        sCategory.clear();
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).isSelected()) {
                                sCategory.add(items.get(i).getName());
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
