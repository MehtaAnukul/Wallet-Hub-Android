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
import com.anukul.wallethub.adapter.MusicAdapter;
import com.anukul.wallethub.listener.MusicOnItemClickListener;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.MusicModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MusicViewActivity extends AppCompatActivity implements MusicOnItemClickListener {

    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private FloatingActionButton floatingActionButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private ArrayList<MusicModel> musicModelArrayList;
    private Intent intent;
    private String labelPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_view);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Music");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.activity_music_floatingActionBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoImageUploadActivity = new Intent(MusicViewActivity.this, MusicUploadActivity.class);
                startActivity(gotoImageUploadActivity);
            }
        });
        intent = getIntent();


        if (intent.hasExtra(AppConstant.FIREBASE_NODE_AUDIO)) {

            labelPush = intent.getStringExtra(AppConstant.FIREBASE_NODE_AUDIO);
            getAudioFromLabel();
        } else {
            getMusics();
        }
        recyclerView = findViewById(R.id.activity_music_view_rv);
        musicModelArrayList = new ArrayList<>();
        musicAdapter = new MusicAdapter(musicModelArrayList, this, MusicViewActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(musicAdapter);


    }

    private void getAudioFromLabel() {
        final String uuid = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_AUDIO)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot musicModels : dataSnapshot.getChildren()) {

                    final MusicModel musicModel = musicModels.getValue(MusicModel.class);

                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(musicModel.getLabelPush()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                            if (musicModel.getLabelPush().equals(labelPush)) {
                                musicModelArrayList.add(new
                                        MusicModel(musicModel.getAudioUrl()
                                        , musicModel.getAudioTitle(),
                                        musicModel.getAudioDuration(),
                                        musicModel.getAudioThumbailUrl(), musicModel.getAudioSize(),
                                        musicModel.getAudioType(), labelModel.getLabelName()));

                                Log.e("OH", musicModelArrayList.size() + "");
                            }
                            musicAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                musicAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getMusics() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_AUDIO)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musicModelArrayList.clear();
                for (final DataSnapshot musicModels : dataSnapshot.getChildren()) {

                    final MusicModel musicModel = musicModels.getValue(MusicModel.class);

                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(musicModel.getLabelPush()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);


                            if (labelModel != null) {

                                musicModelArrayList.add(new
                                        MusicModel(musicModels.getKey(), musicModel.getAudioUrl()
                                        , musicModel.getAudioTitle(),
                                        musicModel.getAudioDuration(),
                                        musicModel.getAudioThumbailUrl(), musicModel.getAudioSize(),
                                        musicModel.getAudioType(), labelModel.getLabelName()));

                                Log.e("OH", musicModelArrayList.size() + "");
                                musicAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                musicAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void musicOnItemClick(final MusicModel musicModel, View v) {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        PopupMenu popupMenu = new PopupMenu(MusicViewActivity.this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_popup:

                        Toast.makeText(MusicViewActivity.this, "" + musicModel.getPushkey(), Toast.LENGTH_SHORT).show();

                        databaseReference
                                .child(AppConstant.FIREBASE_NODE_AUDIO)
                                .child(uuid)
                                .child(musicModel.getPushkey())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(MusicViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MusicViewActivity.this, "Done Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;

                    case R.id.view_popup:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(musicModel.getAudioUrl()));
                        startActivity(intent);
                        break;
                }

                return true;

            }
        });

    }
}
