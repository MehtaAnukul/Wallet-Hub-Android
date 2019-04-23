package com.anukul.wallethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.ViewReportAdapter;
import com.anukul.wallethub.listener.ViewReportOnItemClickListener;
import com.anukul.wallethub.model.DocumentModel;
import com.anukul.wallethub.model.ImageModel;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.MusicModel;
import com.anukul.wallethub.model.VideoModel;
import com.anukul.wallethub.model.ViewReportModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewReportActivity extends AppCompatActivity implements ViewReportOnItemClickListener {


    private RecyclerView viewReportCustomRecyclerView;
    private ArrayList<ViewReportModel> viewReportModelArrayList;
    private ViewReportAdapter viewReportAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int audioCount = 0;
    private int imagesCount = 0;
    private int videoCount = 0;
    private int documentCount = 0;
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private String labelName = "";
    private String pushkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Report List");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewReportCustomRecyclerView = findViewById(R.id.activity_view_report_rv);

        viewReportModelArrayList = new ArrayList<>();
//        categoryModelArrayList.add(new CategoryModel("Video", R.drawable.ic_video_library));
//        categoryModelArrayList.add(new CategoryModel("Music", R.drawable.ic_library_music));
//        categoryModelArrayList.add(new CategoryModel("Document", R.drawable.ic_file));
//        categoryModelArrayList.add(new CategoryModel("Notes", R.drawable.ic_note));
//        categoryModelArrayList.add(new CategoryModel("Label", R.drawable.ic_label));

        viewReportAdapter = new ViewReportAdapter(viewReportModelArrayList, this);

        RecyclerView.LayoutManager categorylayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        viewReportCustomRecyclerView.setLayoutManager(categorylayoutManager);
        viewReportCustomRecyclerView.setAdapter(viewReportAdapter);

        Intent intent = getIntent();

        pushkey = intent.getStringExtra(AppConstant.KEY_PUSHKEY_LABEL);
        final String uuid = firebaseAuth.getCurrentUser().getUid();

        databaseReference
                .child(AppConstant.FIREBASE_NODE_LABEL)
                .child(uuid)
                .child(pushkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                        if (labelModel != null) {
                            labelName = labelModel.getLabelName();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        databaseReference
                .child(AppConstant.FIREBASE_NODE_AUDIO)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        viewReportModelArrayList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            MusicModel musicModel = dataSnapshot1.getValue(MusicModel.class);

                            if (musicModel.getLabelPush().equals(pushkey)) {
                                audioCount++;
                            }


                            viewReportAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child(AppConstant.FIREBASE_NODE_VIDEO)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            VideoModel videoModel = dataSnapshot1.getValue(VideoModel.class);

                            if (videoModel.getVideoLabelPush().equals(pushkey)) {
                                videoCount++;
                            }

                            viewReportAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child(AppConstant.FIREBASE_NODE_IMAGE)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            ImageModel imageModel = dataSnapshot1.getValue(ImageModel.class);

                            if (imageModel.getLabelName().equals(pushkey)) {
                                imagesCount++;
                            }

                            viewReportAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference
                .child(AppConstant.FIREBASE_NODE_DOCUMENT)
                .child(uuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            DocumentModel documentModel = dataSnapshot1.getValue(DocumentModel.class);
                            if (documentModel.getLabelPush().equals(pushkey)) {
                                documentCount++;
                            }
                            viewReportAdapter.notifyDataSetChanged();
                        }

                        viewReportModelArrayList.add(new
                                ViewReportModel(imagesCount + "",
                                "IMAGES", labelName));
                        viewReportModelArrayList.add(new
                                ViewReportModel(videoCount + "",
                                "VIDEO", labelName));
                        viewReportModelArrayList.add(new
                                ViewReportModel(audioCount + "",
                                "AUDIO", labelName));
                        viewReportModelArrayList.add(new
                                ViewReportModel(documentCount + "",
                                "DOCUMENT", labelName));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void viewReportOnItemClick(ViewReportModel viewReportModel) {
        switch (viewReportModel.getMediaName()) {
            case "IMAGES":
                Intent gotoImagesIntent = new Intent(ViewReportActivity.this,
                        ImageViewActivity.class);
                gotoImagesIntent.putExtra(AppConstant.FIREBASE_NODE_IMAGE, pushkey);
                startActivity(gotoImagesIntent);
                break;
            case "VIDEO":
                Intent gotoVideoIntent = new Intent(ViewReportActivity.this,
                        VideoViewActivity.class);
                gotoVideoIntent.putExtra(AppConstant.FIREBASE_NODE_VIDEO, pushkey);
                startActivity(gotoVideoIntent);
                break;
            case "AUDIO":
                Intent gotoAudioIntent = new Intent(ViewReportActivity.this,
                        MusicViewActivity.class);
                gotoAudioIntent.putExtra(AppConstant.FIREBASE_NODE_AUDIO, pushkey);
                startActivity(gotoAudioIntent);
                break;
            case "DOCUMENT":
                Intent gotoDocumentIntent = new Intent(ViewReportActivity.this,
                        DocumentViewActivity.class);
                gotoDocumentIntent.putExtra(AppConstant.FIREBASE_NODE_DOCUMENT, pushkey);
                startActivity(gotoDocumentIntent);
                break;
        }
    }


}
