package com.anukul.wallethub.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.VideoAdapter;
import com.anukul.wallethub.listener.VideoOnItemClickListener;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.VideoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VideoViewActivity extends AppCompatActivity implements VideoOnItemClickListener {

    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private FloatingActionButton floatingActionButton;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private ArrayList<VideoModel> videoModelArrayList;
    private String labelPush;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Video");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.activity_videoView_floatingActionBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoImageUploadActivity = new Intent(VideoViewActivity.this, VideoUploadActivity.class);
                startActivity(gotoImageUploadActivity);
            }
        });
        intent = getIntent();
        if (intent.hasExtra(AppConstant.FIREBASE_NODE_VIDEO)) {
            labelPush = intent.getStringExtra(AppConstant.FIREBASE_NODE_VIDEO);
            getVideoFromLabel();
        } else {
            getVideos();
        }

        recyclerView = findViewById(R.id.activity_video_view_rv);
        videoModelArrayList = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoModelArrayList, this, VideoViewActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoAdapter);


    }

    private void getVideoFromLabel() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_VIDEO)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        videoModelArrayList.clear();
                        for (DataSnapshot videoModels : dataSnapshot.getChildren()) {

                            final VideoModel videoModel = videoModels.getValue(VideoModel.class);

                            databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                                    .child(uuid)
                                    .child(videoModel.getVideoLabelPush())
                                    .addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);

                                            if (labelModel != null) {
                                                if (videoModel.getVideoLabelPush().equals(labelPush)) {
                                                    videoModelArrayList.add(new
                                                            VideoModel(videoModel.getVideoUrl()
                                                            , videoModel.getVideoTitle(),
                                                            videoModel.getVideoDuration(),
                                                            videoModel.getVideoThumbailUrl(),
                                                            videoModel.getVideoSize(),
                                                            videoModel.getVideoType(),
                                                            labelModel.getLabelName()));

                                                    Log.e("OH", videoModelArrayList.size() + "");
                                                    videoAdapter.notifyDataSetChanged();


                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        }
                        videoAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    private void getVideos() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_VIDEO)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        videoModelArrayList.clear();
                        for (final DataSnapshot videoModels : dataSnapshot.getChildren()) {

                            final VideoModel videoModel = videoModels.getValue(VideoModel.class);

                            databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                                    .child(uuid)
                                    .child(videoModel.getVideoLabelPush())
                                    .addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);

                                            if (labelModel != null) {
                                                videoModelArrayList.add(new
                                                        VideoModel(videoModels.getKey(), videoModel.getVideoUrl()
                                                        , videoModel.getVideoTitle(),
                                                        videoModel.getVideoDuration(),
                                                        videoModel.getVideoThumbailUrl(),
                                                        videoModel.getVideoSize(),
                                                        videoModel.getVideoType(),
                                                        labelModel.getLabelName()));

                                                Log.e("OH", videoModelArrayList.size() + "");
                                                videoAdapter.notifyDataSetChanged();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        }
                        videoAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    @Override
    public void videoOnItemClick(final VideoModel videoModel, View v) {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        PopupMenu popupMenu = new PopupMenu(VideoViewActivity.this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_popup:

                        Toast.makeText(VideoViewActivity.this, "" + videoModel.getPushkey(), Toast.LENGTH_SHORT).show();

                        databaseReference
                                .child(AppConstant.FIREBASE_NODE_VIDEO)
                                .child(uuid)
                                .child(videoModel.getPushkey())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(VideoViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(VideoViewActivity.this, "Done Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;

                    case R.id.view_popup:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(videoModel.getVideoUrl()));
                        startActivity(intent);
                        break;
                }

                return true;

            }
        });


    }
}
