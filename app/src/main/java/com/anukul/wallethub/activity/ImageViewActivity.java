package com.anukul.wallethub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.ImageViewAdapter;
import com.anukul.wallethub.listener.ImageOnItemClickListener;
import com.anukul.wallethub.model.ImageModel;
import com.anukul.wallethub.model.LabelModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity implements ImageOnItemClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private RecyclerView imageCustomRecyclerView;
    private ArrayList<ImageModel> imageModelArrayList;
    private ImageViewAdapter imageViewAdapter;
    private FloatingActionButton floatingActionButton;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ImageView selectedImg;
    private Uri filePath;


    private static final int REQUEST_PERMISSIONS = 100;

    private Intent intent;
    private String labelPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Image");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();


        floatingActionButton = findViewById(R.id.activity_imageView_floatingActionBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoImageUploadActivity = new Intent(ImageViewActivity.this, ImageUploadActivity.class);
                startActivity(gotoImageUploadActivity);
            }
        });
        imageCustomRecyclerView = findViewById(R.id.activity_imageView_recyclerview);


        imageModelArrayList = new ArrayList<>();


        imageViewAdapter = new ImageViewAdapter(imageModelArrayList, this, ImageViewActivity.this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        imageCustomRecyclerView.setLayoutManager(layoutManager);
        imageCustomRecyclerView.setAdapter(imageViewAdapter);


        if (intent.hasExtra(AppConstant.FIREBASE_NODE_IMAGE)) {

            getImagesFromLabel();
            labelPush = intent.getStringExtra(AppConstant.FIREBASE_NODE_IMAGE);
        } else {
            getImages();
        }


        OnItemTouchMultiDragListener onItemTouchMultiDragListener = new OnItemTouchMultiDragListener("HELLO") {
            @Override
            public void onDownTouchableView(int pos) {
                Toast.makeText(ImageViewActivity.this, "DOWNTOAS", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoveTouchableView(int pos) {
                Toast.makeText(ImageViewActivity.this, "MOVE", Toast.LENGTH_SHORT).show();
            }
        };
        imageCustomRecyclerView.addOnItemTouchListener(onItemTouchMultiDragListener);


    }

    private void getImagesFromLabel() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();
        //Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_IMAGE)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageModelArrayList.clear();

                for (DataSnapshot imageModels : dataSnapshot.getChildren()) {

                    final ImageModel imageModel = imageModels.getValue(ImageModel.class);

                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(imageModel.getLabelName()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (imageModel.getLabelName().equals(labelPush)) {

                                LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                                imageModelArrayList.add(new ImageModel(labelModel.getLabelName(), imageModel.getImgUrl()));
                                Log.e("OH", imageModelArrayList.size() + "");
                                imageViewAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                imageViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getImages() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();
        //Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_IMAGE)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageModelArrayList.clear();
                for (final DataSnapshot imageModels : dataSnapshot.getChildren()) {

                    final ImageModel imageModel = imageModels.getValue(ImageModel.class);

                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(imageModel.getLabelName()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                            if (labelModel != null) {
                                imageModelArrayList.add(new ImageModel(labelModel.getLabelName(), imageModel.getImgUrl(), imageModels.getKey()));
                            }
                            Log.e("OH", imageModelArrayList.size() + "");
                            imageViewAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                imageViewAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void imageOnItemClick(final ImageModel imageModel, View v) {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        PopupMenu popupMenu = new PopupMenu(ImageViewActivity.this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_popup:

                        Toast.makeText(ImageViewActivity.this, "" + imageModel.getPushkey(), Toast.LENGTH_SHORT).show();

                        databaseReference
                                .child(AppConstant.FIREBASE_NODE_IMAGE)
                                .child(uuid)
                                .child(imageModel.getPushkey())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(ImageViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ImageViewActivity.this, "Done Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;

                    case R.id.view_popup:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(imageModel.getImgUrl()));
                        startActivity(intent);
                        break;
                }

                return true;

            }
        });
    }
}
