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
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.adapter.DocumentAdapter;
import com.anukul.wallethub.listener.DocumentOnItemClickListener;
import com.anukul.wallethub.model.DocumentModel;
import com.anukul.wallethub.model.LabelModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DocumentViewActivity extends AppCompatActivity implements DocumentOnItemClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private FloatingActionButton floatingActionButton;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private ArrayList<DocumentModel> documentModelArrayList;
    private Intent intent;
    private String labelPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Document");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.activity_documentView_floatingActionBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoImageUploadActivity = new Intent(DocumentViewActivity.this, DocumentUploadActivity.class);
                startActivity(gotoImageUploadActivity);
            }
        });

        intent = getIntent();

        if (intent.hasExtra(AppConstant.FIREBASE_NODE_DOCUMENT)) {

            labelPush = intent.getStringExtra(AppConstant.FIREBASE_NODE_DOCUMENT);
            getDocumentFromLabel();
        } else {
            getDocument();
        }

        recyclerView = findViewById(R.id.document_list);
        documentModelArrayList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentModelArrayList, this, DocumentViewActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(documentAdapter);


    }

    private void getDocumentFromLabel() {


        final String uuid = firebaseAuth.getCurrentUser().getUid();
        //Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_DOCUMENT)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                documentModelArrayList.clear();
                for (DataSnapshot documentModels : dataSnapshot.getChildren()) {


                        final DocumentModel documentModel = documentModels.getValue(DocumentModel.class);


                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(documentModel.getLabelPush())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                                    if (labelModel != null) {
                                        if (documentModel.getLabelPush().equals(labelPush)) {
                                            documentModelArrayList.add(new
                                                    DocumentModel(documentModel.getFileUrl()
                                                    , documentModel.getFileSize(),
                                                    documentModel.getFileType(),
                                                    documentModel.getFileName(),
                                                    labelModel.getLabelName()));

                                            documentAdapter.notifyDataSetChanged();
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
                documentAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getDocument() {

        final String uuid = firebaseAuth.getCurrentUser().getUid();
       // Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        databaseReference
                .child(AppConstant.FIREBASE_NODE_DOCUMENT)
                .child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                documentModelArrayList.clear();
                for (final DataSnapshot documentModels : dataSnapshot.getChildren()) {

                    final DocumentModel documentModel = documentModels.getValue(DocumentModel.class);

                    databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .child(documentModel.getLabelPush())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                                    if (labelModel != null) {


                                        documentModelArrayList.add(new

                                                DocumentModel(documentModels.getKey(), documentModel.getFileUrl()
                                                , documentModel.getFileSize(),
                                                documentModel.getFileType(),
                                                documentModel.getFileName(),
                                                labelModel.getLabelName()));

                                        documentAdapter.notifyDataSetChanged();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
                documentAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onDocumentOnItemClick(final DocumentModel documentModel, View v) {

        final String uuid = firebaseAuth.getCurrentUser().getUid();

        PopupMenu popupMenu = new PopupMenu(DocumentViewActivity.this, v);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete_popup:

                       // Toast.makeText(DocumentViewActivity.this, "" + documentModel.getPushKey(), Toast.LENGTH_SHORT).show();

                        databaseReference
                                .child(AppConstant.FIREBASE_NODE_DOCUMENT)
                                .child(uuid)
                                .child(documentModel.getPushKey())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(DocumentViewActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(DocumentViewActivity.this, "Done Delete", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        break;

                    case R.id.view_popup:

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(documentModel.getFileUrl()));
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

    }
}
