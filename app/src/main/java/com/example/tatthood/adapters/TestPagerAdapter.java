package com.example.tatthood.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatthood.Activity.CommentsActivity;
import com.example.tatthood.Fragments.Profile;
import com.example.tatthood.ModelData.Post;
import com.example.tatthood.ModelData.User;
import com.example.tatthood.Modules.GlideApp;
import com.example.tatthood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TestPagerAdapter extends RecyclerView.Adapter<TestPagerAdapter.TestPagerViewHolder> {

    public Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;



    public TestPagerAdapter( Context mContext, List<Post> mPost) {

        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public TestPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_swipe_item,parent,false);

        return new TestPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestPagerViewHolder holder, int position) {
        Post post = mPost.get(position);
        GlideApp.with(mContext).load(post.getPostimage()).into(holder.image_post);
        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

    publisherInfo(holder.image_profile,holder.username,post.getPublisher());

    asLiked(post.getPostid(),holder.like);
    nLikes(holder.likes,post.getPostid());

    getComments(post.getPostid(), holder.comments);
    isSaved(post.getPostid(), holder.save);

        holder.comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("postid",post.getPostid());
            intent.putExtra("publisherid",post.getPublisher());
            mContext.startActivity(intent);
        }
    });

        holder.like.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (holder.like.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid())
                        .child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid())
                        .child(firebaseUser.getUid()).removeValue();
            }
        }
    });
    // implemented
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("id",post.getPublisher());
            editor.apply();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_testPager,new Profile()).commit();
        }
    });

        holder.save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (holder.save.getTag().equals("save")){
                FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid())
                        .child(post.getPostid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid())
                        .child(post.getPostid()).removeValue();
            }
        }
    });
}

    @Override
    public int getItemCount() {
        return mPost.size();
    }


    public class TestPagerViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, image_post,like,comment, save;
        public TextView username, likes, comments, description;

        public TestPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            image_post = itemView.findViewById(R.id.post_image);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            description = itemView.findViewById(R.id.description);
        }
    }
    private void getComments(String postid, TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    comments.setText("View all " + snapshot.getChildrenCount() + " comments");
                } else {
                    comments.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void asLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.liked);
                    imageView.setTag("liked");
                } else
                {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void nLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(String postid, final ImageView imageView){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmark_colored);
                    imageView.setTag("saved");
                } else
                {
                    imageView.setImageResource(R.drawable.ic_save_post);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView image_profile, TextView username, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users")
                .child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                GlideApp.with(mContext).load(user.getimageUrl()).into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
