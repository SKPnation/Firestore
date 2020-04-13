package com.example.firestore.utility;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestore.R;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    ImageView mAvatarIv;
    TextView mNameTv, mEmailTv;
    View mView;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        mAvatarIv = itemView.findViewById(R.id.avatarIv);
        mNameTv = itemView.findViewById(R.id.nameTv);
        mEmailTv = itemView.findViewById(R.id.emailTv);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }
    private UsersViewHolder.ClickListener mClickListener;
    //interface for click listener
    public interface ClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnClickListener(UsersViewHolder.ClickListener clickListener){
    mClickListener = clickListener;
    }
}
