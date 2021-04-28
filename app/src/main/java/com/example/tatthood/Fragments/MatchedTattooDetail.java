package com.example.tatthood.Fragments;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static android.content.ContentValues.TAG;

public class MatchedTattooDetail extends Fragment {
    public ImageView image_profile, image_post, like, comment, save;
    public TextView username, likes, comments, description, time;
    public ImageButton btn_post_options;
    private Post postDetails ;
    FirebaseUser firebaseUser;

    public MatchedTattooDetail() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_matched_tattoo_detail, container, false);
        image_post = itemView.findViewById(R.id.post_image);
        image_profile = itemView.findViewById(R.id.image_profile);
        like = itemView.findViewById(R.id.like_ic);
        comment = itemView.findViewById(R.id.comment_ic);
        save = itemView.findViewById(R.id.save);
        username = itemView.findViewById(R.id.username);
        likes = itemView.findViewById(R.id.likes);
        comments = itemView.findViewById(R.id.comments);
        description = itemView.findViewById(R.id.description);
        time = itemView.findViewById(R.id.time);
        btn_post_options = itemView.findViewById(R.id.post_options);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        postDetails = (Post) this.getArguments().getSerializable("postClicked");
       // Log.i(TAG, "onPostCreateView: "+postDetails.getPostid());

        return itemView;
    }


    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    comments.setText(" " + snapshot.getChildrenCount());
                } else {
                    comments.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getTimeAgo(TextView time, String stamp_post) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");;
        Log.i(TAG, "timestamp value in format date: "+ stamp_post);
        try {
            long timeStamp = sdf.parse(stamp_post).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(timeStamp, now, DateUtils.MINUTE_IN_MILLIS);
            Log.i(TAG, "timestamp milli value is: " + timeStamp);
            time.setText(ago);
        } catch (ParseException e) {
            Log.i(TAG, "timestamp is wrong: ");
            e.printStackTrace();
        }

    }
    private void asLiked(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void nLikes(final TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(" "+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(String postid, final ImageView imageView) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark_colored);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save_post);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_users")
                .child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                GlideApp.with(getContext()).load(user.getimageUrl()).into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}