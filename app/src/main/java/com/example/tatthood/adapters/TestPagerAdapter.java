
package com.example.tatthood.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TestPagerAdapter extends RecyclerView.Adapter<TestPagerAdapter.TestPagerViewHolder> {

    public Context mContext;
    private List<Post> mPost;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String mUid ;
    public IpagerAdapter mListener;

    public TestPagerAdapter(Context mContext, List<Post> mPost, IpagerAdapter mListener) {
        this.mListener = mListener;
        this.mContext = mContext;
        this.mPost = mPost;
        mUid = firebaseUser.getUid();
    }

    @NonNull
    @Override
    public TestPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_swipe_item, parent, false);

        return new TestPagerViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TestPagerViewHolder holder, int position) {
        Post post = mPost.get(position);
        String post_publisher = post.getPublisher();
        String post_id  = post.getPostid() ;
        String post_image  = post.getPostimage() ;
        String post_description  = post.getDescription() ;
        String post_timeStamp = post.getTimestamp();

        GlideApp.with(mContext).load(post_image).into(holder.image_post);
        if (post_description.equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        publisherInfo(holder.image_profile, holder.username, post_publisher);
        asLiked(post_id, holder.like);
        nLikes(holder.likes, post_id);
        getComments(post_id, holder.comments);
        isSaved(post_id, holder.save);
        getTimeAgo(holder.time, post_timeStamp);

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post_id);
                intent.putExtra("publisherid", post_publisher);
                mContext.startActivity(intent);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post_id)
                            .child(mUid).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post_id)
                            .child(mUid).removeValue();
                }
            }
        });
        // implemented
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", post_publisher);
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.container_testPager, new Profile()).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid())
                            .child(post_id).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saved").child(firebaseUser.getUid())
                            .child(post_id).removeValue();
                }
            }
        });

        holder.btn_post_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.btn_post_options,post_publisher,mUid,post_id,post_image,position,post_description);
            }
        });
    }

    private void showMoreOptions(ImageButton btn_post_options, String post_publisher, String mUid, String post_id, String post_image,int posItem,String description) {
        PopupMenu popupMenu = new PopupMenu(mContext,btn_post_options, Gravity.END);
        if (post_publisher.equals(mUid)) {
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");

        }else {
            popupMenu.getMenu().add(Menu.NONE,2,0,"Report");
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                  removeItem(posItem);
                  mListener.goBackToProfile(post_id, post_image,posItem);
                  notifyDataSetChanged();
                } else if ( id == 1) {
                    mListener.editPost(post_id, post_image,description);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public static class TestPagerViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, image_post, like, comment, save;
        public TextView username, likes, comments, description, time;
        public ImageButton btn_post_options;
        public IpagerAdapter mListener;

        public TestPagerViewHolder(@NonNull View itemView, IpagerAdapter mListener) {
            super(itemView);
            this.mListener = mListener;
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
        }
    }

    public interface IpagerAdapter {
        void goBackToProfile(String post_id,String post_image,int position);
        void editPost(String post_id, String post_url, String description);
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
                GlideApp.with(mContext).load(user.getimageUrl()).into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeItem(int position) {
        if (position > -1 && position < mPost.size()) {
            mPost.remove(position);
            notifyDataSetChanged();
            notifyItemRemoved(position);
        }
    }
}
