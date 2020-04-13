package com.example.firestore.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestore.ChatActivity;
import com.example.firestore.R;
import com.example.firestore.TheirProfileActivity;
import com.example.firestore.UsersFragment;
import com.example.firestore.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<UsersViewHolder> {

    UsersFragment usersFragment;
    List<User> userList;
    Context context;

    public AdapterUsers(UsersFragment usersFragment, List<User> userList, Context context) {
        this.usersFragment = usersFragment;
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);

        UsersViewHolder viewHolder = new UsersViewHolder(view);
        //handle item clicks
        viewHolder.setOnClickListener(new UsersViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final String hisUID = userList.get(position).getUid();
                String userImage = userList.get(position).getImage();
                String userName = userList.get(position).getName();
                final String userEmail = userList.get(position).getEmail();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            //Profile clicked
                            /*click to go to specific user profile with uid, this is uid is of clicked user
                             * which will be used to show user specific data/posts*/
                            Intent intent = new Intent(context, TheirProfileActivity.class);
                            intent.putExtra("uid", hisUID);
                            context.startActivity(intent);
                        }
                        if (which == 1){
                            //Chat clicked
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid", hisUID);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();

                //Toast.makeText(context, userName+"\n"+userEmail, Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        //get Data
        holder.mNameTv.setText(userList.get(position).getName());
        holder.mEmailTv.setText(userList.get(position).getEmail());
        try {
            Picasso.get().load(userList.get(position).getImage()).into(holder.mAvatarIv);
        }
        catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
