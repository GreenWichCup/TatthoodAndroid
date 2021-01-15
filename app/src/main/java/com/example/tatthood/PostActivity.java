package com.example.tatthood;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Interfaces.RecyclerViewClickInterface;
import com.example.tatthood.ModelData.Category;
import com.example.tatthood.adapters.CategoryAdapter;
import com.example.tatthood.adapters.SelectedCategoryAdapter;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    RecyclerView category_recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> mCategory;
    EditText category_search_bar;

    RecyclerView selectedCategoryRecyclerView;
    private SelectedCategoryAdapter selectedCategoryAdapter;
    private List<String> sCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //search category recyclerView
        category_recyclerView = findViewById(R.id.category_recyclerview);
        category_recyclerView.setHasFixedSize(true);
        category_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        category_search_bar = findViewById(R.id.category_search_bar);
        mCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this,mCategory,this);
        category_recyclerView.setAdapter(categoryAdapter);

        readCategory();

        category_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCategory(s.toString().toUpperCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // selected categoryList
        selectedCategoryRecyclerView = findViewById(R.id.selected_category_recyclerview);
        selectedCategoryRecyclerView.setHasFixedSize(false);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
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
                uploadImage();
            }
        });

        CropImage.activity().setAspectRatio(4,5).start(PostActivity.this);
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
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postId).setValue(hashMap);

                       sCategory.forEach((n) -> {
                           DatabaseReference refCategoryPost = FirebaseDatabase.getInstance().getReference("Category").child(n.trim().toLowerCase()).child("matched");
                           //need one variable for each category
                           HashMap<String,Object> hashMapCategory = new HashMap<>();
                           hashMapCategory.put("postid",postId);
                           refCategoryPost.child(postId).setValue(hashMapCategory);
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

    private void searchCategory(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Category")
                .orderByChild("categoryName_uppercase")
                .startAt(s.toUpperCase())
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCategory.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);
                    mCategory.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void readCategory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (category_search_bar.getText().toString().equals("")){
                    mCategory.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Category category = dataSnapshot.getValue(Category.class);
                        mCategory.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String catString = mCategory.get(position).getCategoryName().toLowerCase();
        sCategory.add(catString);
        selectedCategoryAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLongClick(int position) {
        sCategory.remove(sCategory.get(position));
        selectedCategoryAdapter.notifyDataSetChanged();
    }
}
