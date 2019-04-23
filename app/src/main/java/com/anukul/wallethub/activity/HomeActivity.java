package com.anukul.wallethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.CategoryAdapter;
import com.anukul.wallethub.listener.CategoryOnItemClickListener;
import com.anukul.wallethub.model.CategoryModel;
import com.anukul.wallethub.model.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CategoryOnItemClickListener {

    private RecyclerView categoryCustomRecyclerView;
    private ArrayList<CategoryModel> categoryModelArrayList;
    private CategoryAdapter categoryAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_home_fabBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,LabelCreateActivity.class);
                intent.putExtra(AppConstant.KEY_CODE,false);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();


        View hView = navigationView.getHeaderView(0);
        final TextView userNameTv = (TextView) hView.findViewById(R.id.nav_header_username_tv);
        final TextView userEmailTv = (TextView) hView.findViewById(R.id.nav_header_useremail_tv);
        final ImageView imageView = (ImageView) hView.findViewById(R.id.imageView);

        databaseReference
                .child(AppConstant.FIREBASE_NODE_USERS)
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        userNameTv.setText(userModel.getUserName());
                        userEmailTv.setText(userModel.getUserEmail());

                        Glide.with(getApplicationContext())
                                .load(userModel.getProfileUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(R.mipmap.ic_launcher)
                                .into(imageView);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void initView() {
        categoryCustomRecyclerView = findViewById(R.id.content_home_categoryRecyclerView);

        categoryModelArrayList = new ArrayList<>();
        categoryModelArrayList.add(new CategoryModel("Image", R.drawable.ic_image));
        categoryModelArrayList.add(new CategoryModel("Video", R.drawable.ic_video_library));
        categoryModelArrayList.add(new CategoryModel("Music", R.drawable.ic_library_music));
        categoryModelArrayList.add(new CategoryModel("Document", R.drawable.ic_file));
        categoryModelArrayList.add(new CategoryModel("Notes", R.drawable.ic_note));
        categoryModelArrayList.add(new CategoryModel("Label", R.drawable.ic_label));

        categoryAdapter = new CategoryAdapter(categoryModelArrayList, this);

        RecyclerView.LayoutManager categorylayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        categoryCustomRecyclerView.setLayoutManager(categorylayoutManager);
        categoryCustomRecyclerView.setAdapter(categoryAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the profile action
            Intent gotoProfileActivity = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(gotoProfileActivity);
        } else if (id == R.id.nav_logout) {

            firebaseAuth.signOut();
            final Intent gotoLogin = new Intent(HomeActivity.this, SignInActivity.class);
            startActivity(gotoLogin);
            finish();

        } else if (id == R.id.nav_about) {
            Intent gotoAboutActivity = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(gotoAboutActivity);

        } else if (id == R.id.nav_feedback) {
            Intent gotoFeedbackActivity = new Intent(HomeActivity.this, FeedbackActivity.class);
            startActivity(gotoFeedbackActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void categoryOnItemClick(CategoryModel categoryModel) {


        final String itemName = categoryModel.getCategoryImgName();

        switch (itemName) {
            case "Image":
                Intent gotoViewImageActivty = new Intent(HomeActivity.this, ImageViewActivity.class);
                startActivity(gotoViewImageActivty);
                break;
            case "Music":
                Intent gotoMusicViewActivity = new Intent(HomeActivity.this, MusicViewActivity.class);
                startActivity(gotoMusicViewActivity);
                break;
            case "Video":
                Intent gotoVideoViewActivity = new Intent(HomeActivity.this, VideoViewActivity.class);
                startActivity(gotoVideoViewActivity);
                break;
            case "Notes":
                Intent gotoViewNotesActivity = new Intent(HomeActivity.this, NotesViewActivity.class);
                startActivity(gotoViewNotesActivity);
                break;
            case "Label":
                Intent gotolabelActivity = new Intent(HomeActivity.this, LabelViewActivity.class);
                startActivity(gotolabelActivity);
                break;
            case "Document":
                Intent gotoDocumentActivity = new Intent(HomeActivity.this,
                        DocumentViewActivity.class);
                startActivity(gotoDocumentActivity);
                break;
        }

    }
}
