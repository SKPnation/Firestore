package com.example.firestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TheirProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    ImageView avatarIv;
    TextView nameTv, emailTv, phoneTv;
    RecyclerView postsRecyclerview;

    AdapterPost adapterPost;
    List<Post> postList;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_their_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        postList = new ArrayList<>();

        //init views
        avatarIv = findViewById(R.id.avatarIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        postsRecyclerview = findViewById(R.id.recyclerView_posts);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(uid);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
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
        loadHisPosts();
    }

    private void loadHisPosts() {
        //linear layout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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

                    adapterPost = new AdapterPost(TheirProfileActivity.this, postList);
                    adapterPost.notifyDataSetChanged();
                    postsRecyclerview.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TheirProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //mProfileTv.setText(user.getEmail());
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout){
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
