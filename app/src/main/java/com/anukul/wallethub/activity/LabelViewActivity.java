package com.anukul.wallethub.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.LabelAdapter;
import com.anukul.wallethub.listener.ItemClickListener;
import com.anukul.wallethub.model.DocumentModel;
import com.anukul.wallethub.model.ImageModel;
import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.MusicModel;
import com.anukul.wallethub.model.NoteModel;
import com.anukul.wallethub.model.VideoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LabelViewActivity extends AppCompatActivity implements ItemClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private RecyclerView labelRecyclerView;
    private ArrayList<LabelModel> labelModelArrayList;
    private LabelAdapter labelAdapter;
    LinearLayout linearLayout;
    private FloatingActionButton floatingActionButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public static ActionMode actionMode = null;
    private CoordinatorLayout coordinatorLayout;
    public static boolean isActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_view);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Label");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        coordinatorLayout = findViewById(R.id.activity_label_coordinatorLayout);
        labelRecyclerView = findViewById(R.id.activity_label_recyclerView);

        labelModelArrayList = new ArrayList<>();
        labelAdapter = new LabelAdapter(labelModelArrayList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        labelRecyclerView.setLayoutManager(layoutManager);
        labelRecyclerView.setAdapter(labelAdapter);


        floatingActionButton = findViewById(R.id.activity_label_floatingActionBtn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoCreateLabelAcitivity = new Intent(LabelViewActivity.this, LabelCreateActivity.class);
                gotoCreateLabelAcitivity.putExtra(AppConstant.KEY_CODE, false);
                startActivity(gotoCreateLabelAcitivity);
            }
        });

        getLabelData();
    }

    private void getLabelData() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        if (!uuid.isEmpty()) {

            databaseReference
                    .child(AppConstant.FIREBASE_NODE_LABEL)
                    .child(uuid)
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            labelModelArrayList.clear();

                            for (DataSnapshot labelModel : dataSnapshot.getChildren()) {

                                LabelModel labelModel1 = labelModel.getValue(LabelModel.class);
                                labelModel1.setPushKey(labelModel.getKey());
                                labelModelArrayList.add(labelModel1);

                            }
                            labelAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

        } else {
            Toast.makeText(this, "Token Error Relogin and Try Again", Toast.LENGTH_SHORT).show();
        }

    }


    //NotesCustom card optiom menu (update,delete)
    @Override
    public void onItemClickLabel(final LabelModel labelModel, View view, final int position) {
        switch (view.getId()) {
            case R.id.label_customLayout_optionMenu:
                PopupMenu popupMenu = new PopupMenu(this, view);
                getMenuInflater().inflate(R.menu.custom_option_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.custom_option_menu_update:
                                updateData(position, labelModel);
                                break;
                            case R.id.custom_option_menu_delete:
                                deleteData(position, labelModel);
                                break;
                        }
                        return false;
                    }
                });
                break;
            default:
                final Intent gotoReportIntent = new Intent(LabelViewActivity.this, ViewReportActivity.class);
                gotoReportIntent.putExtra(AppConstant.KEY_PUSHKEY_LABEL, labelModel.getPushKey());
                startActivity(gotoReportIntent);
                Toast.makeText(this, "" + labelModel.getLabelName(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void deleteData(final int position, final LabelModel labelModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LabelViewActivity.this, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        builder.setTitle("DELETE");
        builder.setMessage("Are you sure");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LabelViewActivity.this, "Label Deleted", Toast.LENGTH_SHORT).show();


                final String uuid = firebaseAuth.getCurrentUser().getUid();
                final String pushkey = labelModel.getPushKey();
                if (!uuid.isEmpty()) {
                    databaseReference
                            .child(AppConstant.FIREBASE_NODE_IMAGE)
                            .child(uuid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        ImageModel imageModel = dataSnapshot1.getValue(ImageModel.class);

                                        if (imageModel.getLabelName().equals(pushkey)) {
                                            dataSnapshot1.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                }
                                            });
                                        }

                                    }


                                    databaseReference
                                            .child(AppConstant.FIREBASE_NODE_AUDIO)
                                            .child(uuid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        MusicModel musicModel = dataSnapshot1.getValue(MusicModel.class);

                                                        if (musicModel.getLabelPush().equals(pushkey)) {
                                                            dataSnapshot1.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                                }
                                                            });
                                                        }

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
                                                            dataSnapshot1.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                                }
                                                            });
                                                        }

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
                                                            dataSnapshot1.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                                }
                                                            });
                                                        }

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                                            .child(uuid)
                                            .child(labelModel.getPushKey())
                                            .removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference1) {
                                                    if (databaseError != null) {
                                                        Toast.makeText(LabelViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(LabelViewActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(LabelViewActivity.this, "Deleted" + pushkey, Toast.LENGTH_SHORT).show();


                                                    }
                                                }
                                            });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LabelViewActivity.this, "Label Not Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void updateData(int position, LabelModel labelModel) {
        Intent updateIntent = new Intent(LabelViewActivity.this, LabelCreateActivity.class);
        updateIntent.putExtra(AppConstant.KEY_CODE, true);
        updateIntent.putExtra(AppConstant.KEY_PUSHKEY_LABEL, labelModel.getPushKey());
        Log.e("UpdatePostion", "" + position);
        startActivityForResult(updateIntent, 100);
    }


    @Override
    public void onItemClick(NoteModel notesModel, View view, int position) {
    }

}
