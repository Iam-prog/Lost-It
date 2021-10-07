package edu.ewubd.lost_it;

import android.content.Context;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {
    private Context p_context;
    private List<PostClass> post_Class;
    private OnItemClickListener post_Listener;

    public PostAdapter(Context p1_context, List<PostClass> postClass1) {
        p_context = p1_context;
        post_Class = postClass1;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(p_context).inflate(R.layout.layout_post_row, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        PostClass current_post = post_Class.get(position);
        Picasso.with(p_context)
                .load(current_post.getPost_imageUrl())
                .fit()
                .centerCrop()
                .into(holder.Imv_post_image_holder);
        holder.tv_post_details.setText(current_post.getPost_details());


        FirebaseDatabase.getInstance("https://lost-it-a15e2-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").
                orderByChild("email")
                .equalTo(current_post.user_Email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value_of_key = "";
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    value_of_key = childSnapshot.getKey();
                }
                if (snapshot.exists()) {
                    String img_url = snapshot.child(value_of_key).child("userImageUrl").getValue(String.class);
                    String current_address = snapshot.child(value_of_key).child("currentAddress").getValue(String.class);
                    String name = snapshot.child(value_of_key).child("name").getValue(String.class);

                    Picasso.with(p_context)
                            .load(img_url)
                            .fit()
                            .centerCrop()
                            .into(holder.Imv_poster_img);
                    holder.poster_name.setText(name);
                    holder.tv_post_date.setText("posted on " + current_post.getPost_date() + "  " + current_address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return post_Class.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener
            , MenuItem.OnMenuItemClickListener{
        public TextView tv_post_date;
        public TextView tv_post_details;
        public ImageView Imv_post_image_holder;
        public ImageView Imv_poster_img;
        public TextView poster_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tv_post_date = itemView.findViewById(R.id.post_date);
            tv_post_details = itemView.findViewById(R.id.post_details1);
            Imv_post_image_holder = itemView.findViewById(R.id.imv_post_image_holder);
            Imv_poster_img = itemView.findViewById(R.id.Imv_poster_img);
            poster_name = itemView.findViewById(R.id.poster_name);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if (post_Listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    post_Listener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (post_Listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            post_Listener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        post_Listener = listener;
    }
}