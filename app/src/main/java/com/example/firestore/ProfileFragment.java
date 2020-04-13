package com.example.firestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestore.models.Post;
import com.example.firestore.utility.AdapterPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore fFirestore;
    CollectionReference cReference;

    //views
    ImageView avatarIv;
    TextView nameTv, emailTv, phoneTv;
    RecyclerView postsRecyclerview;

    AdapterPost adapterPost;
    List<Post> postList;
    String uid;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        fFirestore = FirebaseFirestore.getInstance();
        cReference = fFirestore.collection("Users");

        //init views
        avatarIv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        postsRecyclerview = view.findViewById(R.id.recyclerView_posts);

        /*We have to get info of currently signed in user. We can get it using user's email or uid
          Im gonna retrieve user detail using email*/
        /*By using orderByChild query we will show the detail from a node whose key named email has a value
        equal to currently signed in email.
        It will search all nodes, where the matches it will get its detail*/

        DocumentReference docRef = cReference.document(user.getUid());
        docRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException ffe) {
                nameTv.setText(documentSnapshot.getString("name"));
                emailTv.setText(documentSnapshot.getString("email"));
                phoneTv.setText(documentSnapshot.getString("phone"));
                String image = documentSnapshot.getString("image");

                try {
                    //if image is received then set
                    Picasso.get().load(image).resize(260, 260).into(avatarIv);
                }
                catch (Exception e){
                    //if there i snay exception while getting imgae then set default
                    Picasso.get().load(R.drawable.ic_add_image).into(avatarIv);
                }
            }
        });

        checkUserStatus();
        loadMyPosts();

        return view;
    }

    private void loadMyPosts() {

        postList = new ArrayList<>();

        //linear layout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //set to recyclerView
        postsRecyclerview.setLayoutManager(linearLayoutManager);

        //init posts list
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
        //Retrieve posts that have uids similar to that of the current user
        Query query = postRef.orderByChild("uid").equalTo(uid);
        //get all data from this reference
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);

                    //add to list
                    postList.add(post);

                    adapterPost = new AdapterPost(getActivity(), postList);
                    adapterPost.notifyDataSetChanged();
                    postsRecyclerview.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //mProfileTv.setText(user.getEmail());
            uid = user.getUid();
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
