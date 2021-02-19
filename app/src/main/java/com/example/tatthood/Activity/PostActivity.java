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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.R;
import com.example.tatthood.adapters.SelectedCategoryAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    Uri imageUri;
    String imageUrl;
    StorageTask uploadTask;
    StorageReference storageReference;
    EditText post_description;
    ImageView close_post, post_image_add;
    TextView post_txt;
    Spinner spinnerListCategory;
    CheckBox personalCheckbox;

    FirebaseUser currentUser;

    RecyclerView selectedCategoryRecyclerView;
    private SelectedCategoryAdapter selectedCategoryAdapter;
    private List<String> sCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post_layout);

        spinnerListCategory = findViewById(R.id.spinner);

        personalCheckbox = findViewById(R.id.personal_checkBox);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // selected categoryList
        selectedCategoryRecyclerView = findViewById(R.id.selected_category_recyclerview);
        selectedCategoryRecyclerView.setHasFixedSize(false);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        selectedCategoryRecyclerView.setLayoutManager(layoutManager);
        sCategory = new ArrayList<>();
        selectedCategoryAdapter = new SelectedCategoryAdapter(this,sCategory);
        selectedCategoryRecyclerView.setAdapter(selectedCategoryAdapter);

        //send Post to firebaseDatabase
        close_post = findViewById(R.id.post_cancel);
        post_description = findViewById(R.id.post_description);
        post_image_add = findViewById(R.id.post_add_image);
        post_txt = findViewById(R.id.post_txt);
        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        close_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,App_Main_Page.class));
                finish();
            }
        });

        post_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sCategory.isEmpty() && !post_description.getText().toString().equals("")){
                    uploadImage();
                } else{
                    Toast.makeText(PostActivity.this,"Choose a category",Toast.LENGTH_SHORT).show();
                };
            }
        });

        CropImage.activity().setAspectRatio(4,5).start(PostActivity.this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> categoryList = new ArrayList<String>();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String categoryItems = snapshot.child("categoryName").getValue(String.class);
                    if (categoryItems!=null){
                        categoryList.add(categoryItems);
                    }
                    Log.d("tag", "onDataChange: "+categoryItems);
                }

                Spinner spinnerProperty = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, categoryList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProperty.setAdapter(addressAdapter);

                spinnerProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String categoryValue =  parent.getItemAtPosition(position).toString();
                            sCategory.add(categoryValue);

                        selectedCategoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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



        if (imageUri != null){
            StorageReference referencefile = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = referencefile.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return referencefile.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postId = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("postid",postId);
                        hashMap.put("postimage",imageUrl);
                        hashMap.put("description",post_description.getText().toString());
                        hashMap.put("publisher", currentUser.getUid());
                        if(personalCheckbox.isChecked()){
                            hashMap.put("personal_tattoo",true);
                        }
                        reference.child(postId).setValue(hashMap);

                        HashMap<String,Object> hashMapCategory = new HashMap<>();
                        HashMap<String,Object> hashInfoCat = new HashMap<>();
                        DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference("CategoryMatched");
                        String catName = refCategory.push().getKey();
                        sCategory.forEach((n) -> {
                            hashInfoCat.put("categoryName_uppercase", n.toUpperCase());
                            hashInfoCat.put("categoryName",n.trim().toLowerCase());
                            hashMapCategory.put("postid",postId);
                            hashMapCategory.put("imageurl",imageUrl);
                            refCategory.child(n.trim().toLowerCase()).setValue(hashInfoCat);
                            refCategory.child(n.trim().toLowerCase()).child("matched").child(postId).setValue(hashMapCategory);
                        });
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

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongClick(int position) {
        sCategory.remove(sCategory.get(position));
        selectedCategoryAdapter.notifyDataSetChanged();
    }
}
